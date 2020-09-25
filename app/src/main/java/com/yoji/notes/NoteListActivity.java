package com.yoji.notes;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.yoji.notes.database.NoteData;

import java.util.List;

public class NoteListActivity extends NoteActivity implements ItemOnClickListener {

    private LiveData<List<NoteData>> allNotesData;
    private NoteListActivityAdapter adapter;

    private long noteId;

    private View.OnClickListener addItemOnClickListener = v -> startActivity(
            new Intent(NoteListActivity.this, CreateOrEditNoteActivity.class));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_list);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);

        FloatingActionButton addItemFab = findViewById(R.id.addItemFabId);
        addItemFab.setOnClickListener(addItemOnClickListener);
        RecyclerView notesListView = findViewById(R.id.notesListViewId);

        Toolbar toolbar = findViewById(R.id.toolbarId);
        setSupportActionBar(toolbar);
        toolbar.setTitle(R.string.title);

        allNotesData = getNotes();
        adapter = new NoteListActivityAdapter(this, this);
        notesListView.setAdapter(adapter);
        notesListView.setLayoutManager(layoutManager);
        registerForContextMenu(notesListView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        allNotesData.observe(this, notesData -> {
            for (NoteData noteData : notesData) {
                Log.d("log", "Id = " + noteData.getNoteId() +
                        "; Title = " + noteData.getTitle() +
                        "; Text = " + noteData.getText() +
                        "; Deadline = " + noteData.getDeadline());
            }
        });
        allNotesData.observe(this, notesData -> adapter.setAllNotesData(notesData));
        adapter.notifyDataSetChanged();
    }

    @Override
    public void getItemId(long id) {
        noteId = id;
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.edit_note:
                editNote();
                return true;
            case R.id.delete_note:
                deleteNoteById(noteId);
                return true;
            case R.id.share_note:
                shareNote(getNoteById(noteId));
                return true;
            default:
                return false;
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_context, menu);
    }

    private void editNote() {
        Intent intent = new Intent(NoteListActivity.this, CreateOrEditNoteActivity.class);
        intent.putExtra("id", noteId);
        startActivity(intent);
    }

    private void shareNote(NoteData noteData) {
        StringBuilder message = new StringBuilder();
        message.append(noteData.getTitle())
                .append(System.getProperty("line.separator"))
                .append(System.getProperty("line.separator"))
                .append(noteData.getText());
        if (noteData.getDeadline() != null) {
            message.append(System.getProperty("line.separator"))
                    .append(getString(R.string.deadline))
                    .append(noteData.getDeadline().toString());
        }
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_TEXT, message.toString());
        intent.setType("text/plain");

        Intent shareIntent = Intent.createChooser(intent, null);
        startActivity(shareIntent);
    }
}
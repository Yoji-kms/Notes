package com.yoji.notes;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.yoji.notes.database.NoteData;
import com.yoji.notes.database.NotesRepository;

import java.util.List;

public class MainActivity extends AppCompatActivity implements ItemOnClickListener {

    private FloatingActionButton addItemFab;
    private ListView notesListView;

    private NotesRepository notesRepository;
    private LiveData<List<NoteData>> allNotesData;
    private MainActivityAdapter adapter;

    private View.OnClickListener addItemOnClickListener = v -> startActivity(
            new Intent(MainActivity.this, CreateNoteActivity.class));

    private AdapterView.OnItemLongClickListener onItemLongClickListener = (parent, view, position, id) -> {
        view.findViewById(R.id.deleteBtnId).setVisibility(View.VISIBLE);
        view.findViewById(R.id.editBtnId).setVisibility(View.VISIBLE);
        return true;
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        addItemFab = findViewById(R.id.addItemFabId);
        addItemFab.setOnClickListener(addItemOnClickListener);
        notesListView = findViewById(R.id.notesListViewId);

        notesRepository = new NotesRepository(getApplication());
        allNotesData = notesRepository.getAllNotesData();
        adapter = new MainActivityAdapter(this, this);
        notesListView.setAdapter(adapter);
        notesListView.setOnItemLongClickListener(onItemLongClickListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        allNotesData.observe(this, notesData -> {
          for (NoteData noteData : notesData){
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
    public void itemOnClickListener(long id, boolean delete, boolean update) {
        NoteData noteData = notesRepository.getNoteById(id);
        if (delete) notesRepository.deleteNoteData(noteData);
        if (update) {
            Intent intent = new Intent(MainActivity.this, CreateNoteActivity.class);
            intent.putExtra("id", id);
            startActivity(intent);
        }
    }
}
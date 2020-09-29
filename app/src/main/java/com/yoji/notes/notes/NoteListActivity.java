package com.yoji.notes.notes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.yoji.notes.App;
import com.yoji.notes.R;
import com.yoji.notes.authentication.SettingsActivity;
import com.yoji.notes.database.NoteData;

import java.util.ArrayList;
import java.util.List;

public class NoteListActivity extends AppCompatActivity implements ItemOnClickListener {

    private LiveData<List<NoteData>> allNotesData;
    private NoteListActivityAdapter adapter;

    private long noteId;

    private View.OnClickListener addItemOnClickListener = v -> startActivity(
            new Intent(NoteListActivity.this, CreateOrEditNoteActivity.class));

    private DialogInterface.OnClickListener positiveButtonOnClickListener = (dialog, which) ->
            App.getNoteRepository().deleteNoteById(noteId);

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

        allNotesData = App.getNoteRepository().getNotes();
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
                AlertDialog.Builder deleteDialog = new AlertDialog.Builder(this);
                deleteDialog.setTitle(R.string.warning)
                        .setMessage(R.string.delete_warning_message)
                        .setPositiveButton(R.string.yes, positiveButtonOnClickListener)
                        .setNegativeButton(R.string.no, null);
                deleteDialog.create().show();
                return true;
            case R.id.share_note:
                shareNote(App.getNoteRepository().getNoteById(noteId));
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_appbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.settings_menu){
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    private void editNote() {
        Intent intent = new Intent(NoteListActivity.this, CreateOrEditNoteActivity.class);
        intent.putExtra("id", noteId);
        startActivity(intent);
    }

    private void shareNote(NoteData noteData) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_TEXT, createMessageToShare(noteData));
        intent.setType("text/plain");
        Intent[] initialIntentArray = createInitialIntentArray();

        Intent shareIntent = Intent.createChooser(intent, null);
        if (initialIntentArray.length > 0) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                shareIntent.putExtra(Intent.EXTRA_EXCLUDE_COMPONENTS, createExcludeComponentArray());
            } else {
                shareIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, initialIntentArray);
            }
        }else{
            Toast.makeText(this, R.string.no_apps_to_share, Toast.LENGTH_SHORT).show();
        }

        startActivity(shareIntent);
    }

    private String createMessageToShare(NoteData noteData) {
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
        return message.toString();
    }

    private ComponentName[] createExcludeComponentArray(){
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");

        List<ResolveInfo> resolveInfos = getPackageManager().queryIntentActivities(intent, 0);
        List<ComponentName> componentList = new ArrayList<>();

        for (ResolveInfo resolveInfo : resolveInfos) {
            String packageName = resolveInfo.activityInfo.packageName;
            if (!(packageName.contains("twitter")
                    || packageName.contains("facebook")
                    || packageName.contains("whatsapp")
                    || packageName.contains("messenger")
                    || packageName.contains("messaging")
                    || packageName.contains("vkontakte")
                    || packageName.contains("skype")
                    || packageName.contains("viber"))) {
                componentList.add(new ComponentName(packageName, resolveInfo.activityInfo.name));
            }
        }

        return componentList.toArray(new ComponentName[0]);
    }

    private Intent[] createInitialIntentArray(){
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");

        List<ResolveInfo> resolveInfos = getPackageManager().queryIntentActivities(intent, 0);
        List<Intent> intentList = new ArrayList<>();

        for (ResolveInfo resolveInfo : resolveInfos) {
            String packageName = resolveInfo.activityInfo.packageName;
            if (packageName.contains("twitter")
                    || packageName.contains("facebook")
                    || packageName.contains("whatsapp")
                    || packageName.contains("messenger")
                    || packageName.contains("messaging")
                    || packageName.contains("vkontakte")
                    || packageName.contains("skype")
                    || packageName.contains("viber")) {
                Intent extraIntent = new Intent(Intent.ACTION_SEND);
                extraIntent.setComponent(new ComponentName(packageName, resolveInfo.activityInfo.name));
                extraIntent.setType("text/plain");
                intentList.add(extraIntent);
            }
        }

        return intentList.toArray(new Intent[0]);
    }
}
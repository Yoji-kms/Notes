package com.yoji.notes;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;

import com.yoji.notes.authentication.SettingsActivity;
import com.yoji.notes.database.NoteData;
import com.yoji.notes.database.NotesRoomRepository;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

public class NoteActivity extends AppCompatActivity implements NoteRepository {

    private NotesRoomRepository notesRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_note);

        notesRepository = new NotesRoomRepository(getApplication());
    }

    @Override
    public NoteData getNoteById(long id) {
        return notesRepository.getNoteById(id);
    }

    @Override
    public LiveData<List<NoteData>> getNotes() {
        return notesRepository.getAllNotesData();
    }

    @Override
    public void saveNote(NoteData noteData, String title, String text, String deadlineStr) {
        java.sql.Date deadline = stringToDate(deadlineStr);
        boolean hasDeadline = !deadlineStr.isEmpty();
        java.sql.Date currentTime = new java.sql.Date(Calendar.getInstance().getTimeInMillis());

        if (noteData == null) {
            noteData = new NoteData(title, text, deadline, currentTime, hasDeadline);
            notesRepository.insertNoteData(noteData);
        }else{
            noteData.setTitle(title);
            noteData.setText(text);
            noteData.setDeadline(deadline);
            noteData.setLastChangeTime(currentTime);
            noteData.setHasDeadline(hasDeadline);
            notesRepository.updateNoteData(noteData);
        }
    }

    @Override
    public void deleteNoteById(long id) {
        notesRepository.deleteNoteData(getNoteById(id));
    }

    private Date stringToDate(String string){
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        try {
            if (!string.isEmpty()) {
                java.util.Date deadlineJavaUtil = dateFormat.parse(string);
                return new Date(Objects.requireNonNull(deadlineJavaUtil).getTime());
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
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
}

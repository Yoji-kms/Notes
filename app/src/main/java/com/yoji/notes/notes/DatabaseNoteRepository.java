package com.yoji.notes.notes;

import android.annotation.SuppressLint;
import android.app.Application;

import androidx.lifecycle.LiveData;

import com.yoji.notes.database.NoteData;
import com.yoji.notes.database.NotesRoomRepository;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

public class DatabaseNoteRepository implements NoteRepository {

    private NotesRoomRepository notesRepository;

    public DatabaseNoteRepository(Application application) {
        notesRepository = new NotesRoomRepository(application);
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
}

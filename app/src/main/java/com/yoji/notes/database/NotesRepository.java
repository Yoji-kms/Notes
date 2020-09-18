package com.yoji.notes.database;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class NotesRepository {
    private NotesDAO notesDAO;

    private LiveData<List<NoteData>> allNotesData;

    public NotesRepository(Application application){
        NotesDatabase database = NotesDatabase.getDatabase(application);
        notesDAO = database.notesDAO();
        allNotesData = notesDAO.getNotesData();
    }

    public LiveData<List<NoteData>> getAllNotesData() {
        return allNotesData;
    }

    public void insertNoteData(NoteData noteData){
        NotesDatabase.databaseWriteExecutor.submit(() -> notesDAO.insert(noteData));
    }

    public NoteData getNoteById (long id){
        Future<NoteData> noteDataFuture = NotesDatabase.databaseWriteExecutor.submit(
                () -> notesDAO.getNoteDataById(id)
        );
        NoteData noteData = new NoteData();
        try {
            noteData = noteDataFuture.get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        return noteData;
    }

    public void deleteNoteData(NoteData noteData){
        NotesDatabase.databaseWriteExecutor.submit(() -> notesDAO.deleteNoteData(noteData));
    }

    public void updateNoteData(NoteData noteData){
        NotesDatabase.databaseWriteExecutor.submit(() -> notesDAO.updateNoteData(noteData));
    }
}

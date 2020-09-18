package com.yoji.notes.database;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

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

//    public long insertNotesData(NotesData notesData){
//        Future<Long> noteIdFuture = NotesDatabase.databaseWriteExecutor.submit(
//                () -> notesDAO.insert(notesData));
//        long noteId = 0;
//        try{
//            noteId = noteIdFuture.get();
//        }catch (ExecutionException | InterruptedException e){
//            e.printStackTrace();
//        }
//        return noteId;
//    }

    public void insertNoteData(NoteData noteData){
        NotesDatabase.databaseWriteExecutor.submit(() -> notesDAO.insert(noteData));
    }

    public NoteData getNoteById (long id){
        return notesDAO.getNoteDataById(id);
    }

    public void updateNoteData(NoteData noteData){
        NotesDatabase.databaseWriteExecutor.submit(() -> notesDAO.updateNoteData(noteData));
    }
}

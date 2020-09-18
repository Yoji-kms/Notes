package com.yoji.notes.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface NotesDAO {

    @Query("SELECT * FROM notes_data ORDER BY has_deadline DESC, deadline, last_change_time DESC")
    LiveData<List<NoteData>> getNotesData();

    @Query("SELECT * FROM notes_data WHERE id = :id")
    NoteData getNoteDataById(long id);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long insert(NoteData noteData);

    @Delete
    void deleteNoteData(NoteData noteData);

    @Update
    void updateNoteData(NoteData noteData);
}

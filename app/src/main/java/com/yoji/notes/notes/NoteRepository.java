package com.yoji.notes.notes;

import androidx.lifecycle.LiveData;

import com.yoji.notes.database.NoteData;

import java.util.List;

public interface NoteRepository {
    NoteData getNoteById(long id);

    LiveData<List<NoteData>> getNotes();

    void saveNote(NoteData noteData, String title, String text, String deadlineStr);

    void deleteNoteById(long id);
}

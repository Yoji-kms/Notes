package com.yoji.notes;

import android.app.Application;

import com.yoji.notes.authentication.HashedKeystore;
import com.yoji.notes.authentication.Keystore;
import com.yoji.notes.notes.DatabaseNoteRepository;
import com.yoji.notes.notes.NoteRepository;

public class App extends Application {
    private static NoteRepository noteRepository;
    private static Keystore keystore;

    @Override
    public void onCreate() {
        super.onCreate();
        noteRepository = new DatabaseNoteRepository(this);
        keystore = new HashedKeystore(this);
    }

    public static NoteRepository getNoteRepository() {
        return noteRepository;
    }

    public static Keystore getKeystore() {
        return keystore;
    }
}

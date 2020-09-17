package com.yoji.notes;

import android.app.Application;

public class App extends Application {
//    private static NoteRepository noteRepository;
//    private static Keystore keystore;
    @Override
    public void onCreate() {
        super.onCreate();
//        noteRepository = new FileNoteRepository(this);
//        passwordStorage = new SimpleKeystore(this);
    }

//    public static NoteRepository getNoteRepository(){
//        return noteRepository;
//    }
//
//    public static Keystore getKeystore(){
//        return keystore;
//    }
}

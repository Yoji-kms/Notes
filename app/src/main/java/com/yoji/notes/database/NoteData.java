package com.yoji.notes.database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "notes_data")
public class NoteData {

    public NoteData (String title, String text, String deadline, String lastChangeTime){
        this.title = title;
        this.text = text;
        this.deadline = deadline;
        this.lastChangeTime = lastChangeTime;
    }

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id", index = true)
    private long noteId;

    public Long getNoteId() {
        return noteId;
    }

    public void setNoteId(Long noteId) {
        this.noteId = noteId;
    }

    @ColumnInfo(name = "title")
    private String title;

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    @ColumnInfo(name = "text")
    private String text;

    public void setText(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    @ColumnInfo(name = "deadline")
    private String deadline;

    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }

    public String getDeadline() {
        return deadline;
    }

    @ColumnInfo(name = "last_change_time")
    private String lastChangeTime;

    public void setLastChangeTime(String lastChangeTime) {
        this.lastChangeTime = lastChangeTime;
    }

    public String getLastChangeTime() {
        return lastChangeTime;
    }
}

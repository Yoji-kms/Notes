package com.yoji.notes.database;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverter;
import androidx.room.TypeConverters;

import java.sql.Date;

@Entity(tableName = "notes_data")
@TypeConverters(NoteData.DateConverter.class)
public class NoteData {

    public NoteData(){}

    public NoteData(String title, String text, Date deadline, Date lastChangeTime, boolean hasDeadline) {
        this.title = title;
        this.text = text;
        this.deadline = deadline;
        this.lastChangeTime = lastChangeTime;
        this.hasDeadline = hasDeadline;
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

    @ColumnInfo(name = "has_deadline")
    private boolean hasDeadline;

    public boolean getHasDeadline() {
        return hasDeadline;
    }

    public void setHasDeadline(boolean hasDeadline) {
        this.hasDeadline = hasDeadline;
    }

    @ColumnInfo(name = "deadline")
    private Date deadline;

    public void setDeadline(Date deadline) {
        this.deadline = deadline;
    }

    public Date getDeadline() {
        return deadline;
    }

    @ColumnInfo(name = "last_change_time")
    private Date lastChangeTime;

    public void setLastChangeTime(Date lastChangeTime) {
        this.lastChangeTime = lastChangeTime;
    }

    public Date getLastChangeTime() {
        return lastChangeTime;
    }

    public static class DateConverter{
        @TypeConverter
        public Date toDate (Long dateLong){
            return dateLong == null ? null : new Date(dateLong);
        }

        @TypeConverter
        public Long fromDate(Date date){
            return date == null ? null : date.getTime();
        }
    }
}

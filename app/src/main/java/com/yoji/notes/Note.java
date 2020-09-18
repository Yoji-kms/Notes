package com.yoji.notes;

public class Note {
    private String title;
    private String text;
    private String deadline;

    //Constructors
    public Note (){}

    public Note(String text){
        this.text = text;
    }

    public Note(String title, String text){
        this.title = title;
        this.text = text;
    }

    public Note (String title, String text, String deadline){
        this.title = title;
        this.text = text;
        this.deadline = deadline;
    }

    //Setters
    public void setTitle(String title) {
        this.title = title;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }

    //Getters
    public String getText() {
        return text;
    }

    public String getTitle() {
        return title;
    }

    public String getDeadline() {
        return deadline;
    }
}

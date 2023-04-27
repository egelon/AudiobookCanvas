package com.example.audiobookcanvas;

public class MetadataModel {
    private String projectTitle;
    private String bookTitle;
    private String author;
    private String language;
    private String description;

    public MetadataModel(String projectTitle, String bookTitle, String author, String language, String description) {
        this.projectTitle = projectTitle;
        this.bookTitle = bookTitle;
        this.author = author;
        this.language = language;
        this.description = description;
    }

    public String getProjectTitle() {
        return projectTitle;
    }

    public void setProjectTitle(String projectTitle) {
        this.projectTitle = projectTitle;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public void setBookTitle(String bookTitle) {
        this.bookTitle = bookTitle;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}

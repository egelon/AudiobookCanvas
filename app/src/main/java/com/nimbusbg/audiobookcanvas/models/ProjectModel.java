package com.nimbusbg.audiobookcanvas.models;

public class ProjectModel {
    private MetadataModel metadata;
    private ContentModel content;
    private String audiobookPath;

    public ProjectModel(MetadataModel metadata, ContentModel content, String audiobookPath) {
        this.metadata = metadata;
        this.content = content;
        this.audiobookPath = audiobookPath;
    }

    public MetadataModel getMetadata() {
        return metadata;
    }

    public void setMetadata(MetadataModel metadata) {
        this.metadata = metadata;
    }

    public ContentModel getContent() {
        return content;
    }

    public void setContent(ContentModel content) {
        this.content = content;
    }

    public String getAudiobookPath() {
        return audiobookPath;
    }

    public void setAudiobookPath(String audiobookPath) {
        this.audiobookPath = audiobookPath;
    }
}


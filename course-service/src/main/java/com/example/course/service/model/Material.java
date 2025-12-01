package com.example.course.service.model;

public class Material {
    private String id;
    private String chapterId;
    private String title;
    private String type;
    private String url;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getChapterId() { return chapterId; }
    public void setChapterId(String chapterId) { this.chapterId = chapterId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }
}

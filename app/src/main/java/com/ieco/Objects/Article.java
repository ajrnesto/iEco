package com.ieco.Objects;

public class Article {
    String id;
    String articleTitle;
    String content;
    String imageFileName;

    public Article() {
    }

    public Article(String id, String articleTitle, String content, String imageFileName) {
        this.id = id;
        this.articleTitle = articleTitle;
        this.content = content;
        this.imageFileName = imageFileName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getArticleTitle() {
        return articleTitle;
    }

    public void setArticleTitle(String articleTitle) {
        this.articleTitle = articleTitle;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getImageFileName() {
        return imageFileName;
    }

    public void setImageFileName(String imageFileName) {
        this.imageFileName = imageFileName;
    }
}

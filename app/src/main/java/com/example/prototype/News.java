package com.example.prototype;

import java.io.Serializable;
import java.util.Date;
import java.util.Vector;

public class News implements Serializable {
    String newsID;
    String title, content, publishTime, language, url, crawlTime, publisher, category;
    String image, video;
    Vector<String> keywords;

    public News() {
        title = "Default News Title";
        content = "Arma virumque cano.";
    }

    public News(String _newsID, String _title, String _content, String _publishTime, String _category, Vector<String> _keywords) {
        newsID = _newsID;
        title = _title;
        content = _content;
        publishTime = _publishTime;
        category = _category;
        keywords = _keywords;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

}

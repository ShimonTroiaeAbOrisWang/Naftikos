package com.example.prototype;

import java.util.Date;
import java.util.Vector;

public class News {
    String newsID;
    String title, content, publishTime, language, url, crawlTime, publisher, category;
    String image, video;
    Vector<String> keywords;

    public News() {
        title = "Default News Title";
        content = "Arma virumque cano.";
    }

    public News(String _newsID, String _title, String _content, String _publishTime, String _language, String _url, String _crawlTime, String _publisher, String _category, Vector<String> _keywords) {
        newsID = _newsID;
        title = _title;
        content = _content;
        publishTime = _publishTime;
        language = _language;
        url = _url;
        crawlTime = _crawlTime;
        publisher = _publisher;
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

package com.example.prototype;

import java.util.Date;

public class News {
    String title;
    // Date date;
    String content;
    // Object tags;

    public News () {
        title = "Default News Title";
        content = "Arma virumque cano.";
    }

    public News (String _title, String _content) {
        title = _title;
        content = _content;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

}

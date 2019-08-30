package com.java.wanghongjian_and_liuxiao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SQLiteDao {
    private String name;
    private SQLiteDatabase db;

    public SQLiteDao() {
        name = MainActivity.getContext().getExternalFilesDir("") + "/news.db";
        db = SQLiteDatabase.openOrCreateDatabase(name, null);
        db.execSQL("create table if not exists news (newsId varchar(44), category varchar(20), collection varchar(5), jsonData varchar(2000))");
        db.close();
    }

    public void add(News news){
        this.add(new RawNews(news));
    }

    public void add(RawNews rawNews){
        this.add(rawNews.newsId, rawNews.category, rawNews.collection, rawNews.jsonData);
    }

    public void add(String newsId, String category, String collection, String jsonData) {
        if (!find(newsId)) {
            db = SQLiteDatabase.openOrCreateDatabase(name, null);
            db.execSQL("insert into news (newsId,category,collection,jsonData) values (?,?,?,?)", new String[]{newsId, category, collection, jsonData});
            db.close();
        }
    }

    public boolean find(String newsId) {
        db = SQLiteDatabase.openOrCreateDatabase(name, null);
        Cursor cursor = db.rawQuery("select * from news where newsId=?", new String[]{newsId});
        boolean b = cursor.moveToNext();
        cursor.close();
        db.close();
        return b;
    }

    public void update(String newsId, String collection){
        db = SQLiteDatabase.openOrCreateDatabase(name, null);
        db.execSQL("update news set collection=? where newsId=?", new String[]{collection, newsId});
        db.close();
    }

    public void delete(String newsId){
        db = SQLiteDatabase.openOrCreateDatabase(name, null);
        db.execSQL("delete from news where newsId=?", new String[]{newsId});
        db.close();
    }

    public List<RawNews> findAllInCollection() {
        db = SQLiteDatabase.openOrCreateDatabase(name, null);
        List<RawNews> newsList = new ArrayList<>();
        Cursor cursor = db.rawQuery("select * from news where collection=?", new String[]{"1"});
        while(cursor.moveToNext()){
            String newsId = cursor.getString(cursor.getColumnIndex("newsId"));
            String category = cursor.getString(cursor.getColumnIndex("category"));
            String collection = cursor.getString(cursor.getColumnIndex("collection"));
            String jsonData = cursor.getString(cursor.getColumnIndex("jsonData"));
            RawNews news = new RawNews(newsId, category, collection, jsonData);
            newsList.add(news);
        }
        cursor.close();
        db.close();
        return newsList;
    }

    public List<RawNews> findAllInCategory(String category){
        db = SQLiteDatabase.openOrCreateDatabase(name, null);
        List<RawNews> newsList = new ArrayList<>();
        Cursor cursor = db.rawQuery("select * from news where category=?", new String[]{category});
        while(cursor.moveToNext()){
            String newsId = cursor.getString(cursor.getColumnIndex("newsId"));
            String collection = cursor.getString(cursor.getColumnIndex("collection"));
            String jsonData = cursor.getString(cursor.getColumnIndex("jsonData"));
            RawNews news = new RawNews(newsId, category, collection, jsonData);
            newsList.add(news);
        }
        cursor.close();
        db.close();
        return newsList;
    }

    public class RawNews {
        public String newsId;
        public String category;
        public String collection;
        public String jsonData;

        public RawNews() { }

        public RawNews(News n){
            newsId = n.newsID;
            category = n.category;
            collection = n.collection;
            JSONObject raw_json = new JSONObject();
            try{
                raw_json.put("video", n.video.toString());
                raw_json.put("content", n.content);
                raw_json.put("publishTime", n.publishTime);
                raw_json.put("language", n.language);
                raw_json.put("url", n.url);
                raw_json.put("crawlTime", n.crawlTime);
                raw_json.put("publisher", n.publisher);
                StringBuilder image = new StringBuilder();
                for (int i=0;i<n.image.size();i++) {
                    if (i > 0)
                        image.append(", ");
                    image.append(n.image.get(i).toString());
                }
                raw_json.put("image", image.toString());
                jsonData = raw_json.toString();
            }catch (JSONException e) {}
        }

        public RawNews(String newsId, String category, String collection, String jsonData) {
            this.newsId = newsId;
            this.category = category;
            this.collection = collection;
            this.jsonData = jsonData;
        }
    }

}

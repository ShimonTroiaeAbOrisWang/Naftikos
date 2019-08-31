package com.java.wanghongjian_and_liuxiao;

import android.text.format.DateUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.Vector;


public class NewsAPI {
    private final String rawAPI = "https://api2.newsminer.net/svc/news/queryNewsList?";
    private Vector<String> search_history = new Vector<>();
    private Integer size;
    private String words, categories;
    private Date endDate, startDate;
    private JSONObject last_json;
    private String last_request;
    private Calendar cal;
    private SQLiteDao db;
    public Vector<News> newsList;

    DateFormat df;

    NewsAPI() {
        TimeZone tz = TimeZone.getTimeZone("UTC");
        df = new SimpleDateFormat("yyyy-MM-dd%20HH:mm:ss");
        df.setTimeZone(tz);
        Date now = new Date();
        cal = Calendar.getInstance();
        cal.setTime(now);
        cal.add(Calendar.DATE, -100);
        endDate = cal.getTime();
        cal.add(Calendar.DATE, -10);
        startDate = cal.getTime();
        size = 50;
        db = new SQLiteDao();
    }

    public Vector<String> getSearchHistory() {
        return search_history;
    }

    public Vector<News> getNews(String keyword, String category, int mode) {
        //Thread connect = new Thread(this);
        Vector<News> news_list = new Vector<>();
        int iteration = 0;
        while (news_list.size() < 15 && iteration++ < 30) {
            last_request = formRequest(keyword, category, mode);
            parseJSON(last_request);
            JSONObject news = last_json;
            if (news == null)
                return news_list;
            try {
                JSONArray data = news.getJSONArray("data");
                for (int i = 0; i < data.length(); i++) {
                    News _news = parseNews(data.getJSONObject(i));
                    news_list.add(_news);
                    db.add(_news);
                }
            } catch (JSONException e) {
            }
        }
        newsList = news_list;
        return news_list;
    }

    public boolean getCoverImage(){
        for (News n: newsList){
            if (n.image.size() > 0)
                n.image.firstElement().getImage();
        }
        return true;
    }

    public Vector<News> testGetNews(String request) {
        Vector<News> news_list = new Vector<>();
        last_request = request;
        parseJSON(last_request);

        JSONObject news = last_json;

        if (news == null)
            return news_list;
        try {
            JSONArray data = news.getJSONArray("data");
            for (int i = 0; i < data.length(); i++) {
                JSONObject n = new JSONObject(data.get(i).toString());
                JSONArray keywords_json = n.getJSONArray("keywords");
                Vector<String> keywords = new Vector<>();
                for (int j = 0; j < keywords_json.length(); j++)
                    keywords.add(keywords_json.getJSONObject(j).getString("word"));
                News _news = new News(n.getString("newsID"), n.getString("title"), n.getString("content"), n.getString("publishTime"), n.getString("category"), keywords, n.getString("publisher"));
                if (n.getString("image") != null && !n.getString("image").equals("[]") && !n.getString("image").equals("")) {
                    String image = n.getString("image");
                    _news.setImage(image.substring(1, image.length() - 1));
                }
                if (n.getString("video") != null)
                    _news.setVideo(n.getString("video"));
                news_list.add(_news);
                db.add(_news);
            }
        } catch (JSONException e) {
        }
        return news_list;
    }

    private News parseNews(JSONObject n) {
        News _news = null;
        try {
            //JSONObject n = new JSONObject(string_form_news);
            JSONArray keywords_json = n.getJSONArray("keywords");
            Vector<String> keywords = new Vector<>();
            for (int j = 0; j < keywords_json.length(); j++)
                keywords.add(keywords_json.getJSONObject(j).getString("word"));
            _news = new News(n.getString("newsID"), n.getString("title"), n.getString("content"), n.getString("publishTime"), n.getString("category"), keywords, n.getString("publisher"));
            if (n.getString("image") != null && !n.getString("image").equals("[]") && !n.getString("image").equals("")) {
                String image = n.getString("image");
                _news.setImage(image.substring(1, image.length() - 1));
            }
            if (n.getString("video") != null && !n.getString("video").equals("") && !n.getString("video").equals("[]"))
                _news.setVideo(n.getString("video"));
        } catch (JSONException e) {
        }
        return _news;
    }

    private String formRequest(String keyword, String category, int mode) {
        StringBuilder request = new StringBuilder(rawAPI);
        if (size != 0)
            request.append("&size=" + size);
        if (mode == 1) {                                // 1 means to update news
            request.append("&startDate=" + df.format(endDate));
            cal.setTime(endDate);
            cal.add(Calendar.HOUR, +3);
            endDate = cal.getTime();
            request.append("&endDate=" + df.format(endDate));
        } else if (mode == 2) {                           // 2 means to load news before
            request.append("&endDate=" + df.format(startDate));
            cal.setTime(startDate);
            cal.add(Calendar.HOUR, -3);
            startDate = cal.getTime();
            request.append("&startDate=" + df.format(startDate));
        }
        if (keyword != null) {
            request.append("&words=" + keyword);
            search_history.add(keyword);
            if (search_history.size() > 20)
                search_history.remove(0);
            words = keyword;
        }
        if (category != null) {
            request.append("&categories=" + category);
            categories = category;
        }
        return request.toString();
    }

    private void parseJSON(String new_request) {
        URL url;
        BufferedReader in;
        JSONObject parsedNews;
        try {
            url = new URL(new_request);
            HttpURLConnection tc = (HttpURLConnection) url.openConnection();
            in = new BufferedReader(new InputStreamReader(tc.getInputStream(), "UTF-8"));
            String inputLine = in.readLine();
            parsedNews = new JSONObject(inputLine);
        } catch (MalformedURLException e) {
            parsedNews = null;
        } catch (IOException e) {
            parsedNews = null;
        } catch (JSONException e) {
            parsedNews = null;
        }
        last_json = parsedNews;
    }
}

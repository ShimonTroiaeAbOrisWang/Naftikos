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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.TimeZone;
import java.util.Vector;


public class NewsAPI {
    private final String rawAPI = "https://api2.newsminer.net/svc/news/queryNewsList?";
    private Vector<String> search_history = new Vector<>();
    private Integer size;
    private String words = null, categories = null;
    private Date endDate, startDate;
    private JSONObject last_json;
    private String last_request;
    private Calendar cal;
    private SQLiteDao db;
    public Vector<News> newsList;
    private HashSet<String> loadingHistory = new HashSet<>();

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
        cal.add(Calendar.DATE, -3);
        startDate = cal.getTime();
        size = 15;
        db = new SQLiteDao();
    }

    public Vector<String> getSearchHistory() {
        return search_history;
    }

    public Vector<News> getNews(String keyword, String category, int mode, boolean recommend) {
        //Thread connect = new Thread(this);
        Vector<News> news_list = new Vector<>();
        int iteration = 0;
        words = keyword;
        categories = category;
        while (news_list.size() < 15 && iteration++ < 60) {
            last_request = formRequest(words, categories, mode, recommend);
            parseJSON(last_request);
            JSONObject news = last_json;
            if (news == null)
                return news_list;
            try {
                JSONArray data = news.getJSONArray("data");
                for (int i = data.length() - 1; i >= 0 ; i--) {
                    News _news = parseNews(data.getJSONObject(i));
                    if (loadingHistory.contains(_news.newsID))
                        continue;
                    news_list.add(_news);
                    db.add(_news);
                    loadingHistory.add(_news.newsID);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        newsList = new Vector<>();//(Vector<News>) news_list.clone();
        for (int i = news_list.size() - 1; i >=0  ; i--)
            newsList.add(news_list.get(i));
        words = null;
        categories = null;
        return (Vector<News>) newsList.clone();
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
            if (n.getString("url") != null) {
                _news.url = n.getString("url");
            }
            if (n.getString("image") != null && !n.getString("image").equals("[]") && !n.getString("image").equals("")) {
                String image = n.getString("image");
                _news.setImage(image.substring(1, image.length() - 1));
            }
            if (n.getString("video") != null && !n.getString("video").equals("") && !n.getString("video").equals("[]"))
                _news.setVideo(n.getString("video"));
            News doc = db.findOne(_news.newsID);
            if (doc != null)
                _news.collection = doc.collection;
        } catch (JSONException e) {
        }
        return _news;
    }

    private String formRequest(String keyword, String category, int mode, boolean recommend) {
        StringBuilder request = new StringBuilder(rawAPI);
        if (size != 0)
            request.append("&size=" + size);
        if (mode == 1) {                                // 1 means to update news
            request.append("&startDate=" + df.format(endDate));
            cal.setTime(endDate);
            cal.add(Calendar.HOUR, +6);
            endDate = cal.getTime();
            request.append("&endDate=" + df.format(endDate));
        } else if (mode == 2) {                           // 2 means to load news before
            request.append("&endDate=" + df.format(startDate));
            cal.setTime(startDate);
            cal.add(Calendar.HOUR, -6);
            startDate = cal.getTime();
            request.append("&startDate=" + df.format(startDate));
        } else {
            cal.setTime(startDate);
            cal.add(Calendar.HOUR, +6);
            startDate = cal.getTime();
            request.append("&startDate=" + df.format(startDate));
            cal.setTime(endDate);
            cal.add(Calendar.HOUR, +6);
            endDate = cal.getTime();
            request.append("&endDate=" + df.format(endDate));
        }
        if (keyword != null && !keyword.equals("")) {
            request.append("&words=" + keyword);
            search_history.add(keyword);
            if (search_history.size() > 20)
                search_history.remove(0);
            words = keyword;
        }
        if (category != null && !category.equals("")) {
            request.append("&categories=" + category);
            categories = category;
        }
        if (recommend) {
            if (Math.random() > 0.4) {
                if (Recommender.getTopKeyword() != null) {
                    words = Recommender.getTopKeyword();
                    request.append("&words=" + Recommender.getTopKeyword());
                }
            } else {
                if (Recommender.getTopCateogry() != null) {
                    categories = Recommender.getTopCateogry();
                    request.append("&categories=" + Recommender.getTopCateogry());
                }
            }
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

    public ArrayList<News> getBookmarks() {
        return db.findAllInCollection();
    }

    public News getNewsById(String id) {
        return db.findOne(id);
    }
}

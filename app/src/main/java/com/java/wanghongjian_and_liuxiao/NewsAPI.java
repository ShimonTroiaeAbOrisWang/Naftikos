package com.java.wanghongjian_and_liuxiao;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.Vector;


public class NewsAPI {
    private final String rawAPI = "https://api2.newsminer.net/svc/news/queryNewsList?";
    private Vector<String> search_history = new Vector<>();
    private Integer size;
    private String endDate, startDate, words, categories;

    DateFormat df;

    NewsAPI() {
        TimeZone tz = TimeZone.getTimeZone("UTC");
        df = new SimpleDateFormat("yyyy-MM-dd%20HH:mm:ss");
        df.setTimeZone(tz);
        endDate = df.format(new Date());
        size = 1;
    }

    private String formRequest(String keyword, String category) {
        StringBuilder request = new StringBuilder(rawAPI);
        if (size != 0)
            request.append("&size=" + size);
        if (startDate != null)
            request.append("&startDate=" + startDate);
        endDate = df.format(new Date());
        request.append("&endDate=" + endDate);
        if (keyword != null)
            request.append("&words=" + keyword);
        if (category != null)
            request.append("&categories=" + category);

        startDate = endDate;
        return request.toString();
    }

    public Vector<News> getNews(String keyword, String category) {
        Vector<News> news_list = new Vector<>();
        String request = formRequest(keyword, category);

        JSONObject news = parseNews(request); // FIXME: 19.8.29 crash!


        if (news == null)
            return news_list;
        try {
            JSONArray data = news.getJSONArray("data");
            for (int i = 0; i < data.length(); i++) {
                JSONObject n = new JSONObject(data.get(i).toString());
                JSONArray keywords_json = n.getJSONArray("keywords");
                Vector<String> keywords = new Vector<>();
                for (int j = 0; j < keywords_json.length(); j++) {
                    keywords.add(keywords_json.get(j).toString());
                }
                news_list.add(new News(n.getString("newsID"), n.getString("title"), n.getString("content"), n.getString("publishTime"), n.getString("category"), keywords));
            }
        } catch (JSONException e) {
        }


        
        return news_list;
    }

    private JSONObject parseNews(String new_request) {
        URL url;
        BufferedReader in;
        JSONObject parsedNews = null;

        try {
            url = new URL(new_request);
            URLConnection tc = url.openConnection();
            //in = new BufferedReader(new InputStreamReader(tc.getInputStream(), "UTF-8"));
            //String inputLine = in.readLine();
            //parsedNews = new JSONObject(inputLine);
        } catch (MalformedURLException e) {
            parsedNews = null;
        } catch (IOException e) {
            parsedNews = null;
        } /*catch (JSONException e) {
            parsedNews = null;
        }
        */

        return parsedNews;
    }
}

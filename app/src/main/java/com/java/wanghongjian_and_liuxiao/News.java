package com.java.wanghongjian_and_liuxiao;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Vector;

public class News /*implements java.io.Serializable*/ {
    String newsID;
    String title, content, publishTime, language, url, crawlTime, publisher, category;
    String video;
    Vector<Image> image;
    Vector<String> keywords; // keywords are listed according to their relevance from 0 to the end
    String dir = Environment.getExternalStorageDirectory().getAbsolutePath() + "/wanghongjian_and_liuxiao/";

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

    public void setImage(String _url) {
        image = new Vector<>();
        String[] urls = _url.split(", ");
        for (String u: urls){
            Image img = new Image(u, newsID, dir);
            Thread connect = new Thread(img);
            connect.start();
            image.add(img);
        }
    }

    public void setVideo(String _url) {
        video = _url;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }
}

class Image implements Runnable{
    String imageURL, newsID, dir;
    Bitmap image;
    Image(String url, String _newsID, String _dir){
        imageURL = url;
        newsID = _newsID;
        dir = _dir;
    }

    @Override
    public void run() {
        try {
            if (imageURL.substring(0, 5).equals("http:"))
                imageURL = "https" + imageURL.substring(4);
            URL url = new URL(imageURL);
            HttpURLConnection tc = (HttpURLConnection) url.openConnection();
            InputStream in = tc.getInputStream();
            image = BitmapFactory.decodeStream(in);
            in.close();
            File file = new File(dir + newsID + ".png");
            FileOutputStream out = new FileOutputStream(file);
            image.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();
        } catch (MalformedURLException e) {
        } catch (IOException e) {
        }
    }
}

package com.java.wanghongjian_and_liuxiao;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;

import androidx.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Vector;

public class News implements java.io.Serializable {
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
        for (int i = 0; i < urls.length; i++) {
            Image img = new Image(urls[i], i, newsID, dir);
            if (i == 0)
                img.getImage();
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

class Image implements Runnable, java.io.Serializable {
    String imageURL, newsID, dir, file_dir=null;
    int index;
    Bitmap image;

    Image(String url, int _index, String _newsID, String _dir) {
        imageURL = url;
        newsID = _newsID;
        dir = _dir;
        index = _index;
        file_dir = dir + newsID + "_" + index + ".png";
    }

    @Override
    public void run() {
        try {
            if (file_dir != null)
                return;
            if (imageURL.substring(0, 5).equals("http:"))
                imageURL = "https" + imageURL.substring(4);
            URL url = new URL(imageURL);
            HttpURLConnection tc = (HttpURLConnection) url.openConnection();
            InputStream in = tc.getInputStream();
            image = BitmapFactory.decodeStream(in);
            in.close();
            File file = new File(file_dir);
            FileOutputStream out = new FileOutputStream(file);
            image.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();
        } catch (MalformedURLException e) {
        } catch (IOException e) {
        }
    }

    public Bitmap getImage(){
        File file = new File(file_dir);
        if (!file.exists()){
            Thread connect = new Thread(this);
            connect.start();
        }else{
            try {
                FileInputStream in = new FileInputStream(file);
                image = BitmapFactory.decodeStream(in);
            }catch (IOException e) {}
        }
        return image;
    }

    @NonNull
    @Override
    public String toString() {
        return file_dir;
    }
}

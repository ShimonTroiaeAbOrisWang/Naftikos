package com.java.wanghongjian_and_liuxiao;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.widget.LinearLayout;
import android.widget.VideoView;

import androidx.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Vector;

public class News implements java.io.Serializable {
    SQLiteDao sql = new SQLiteDao();
    String newsID;
    String title, content, publishTime, language, url, crawlTime, publisher, category;
    String collection;
    // Video video = new Video();
    String videoURL;
    // Vector<Image> image = new Vector<>();
    Vector<String> keywords; // keywords are listed according to their relevance from 0 to the end
    String dir = Environment.getExternalStorageDirectory().getAbsolutePath() + "/wanghongjian_and_liuxiao/";

    Vector<String> imageURLs = new Vector<>();

    public LinearLayout layout = null;

    public News() {
        title = "Default News Title";
        content = "Arma virumque cano.";
    }

    public News(String _newsID, String _title, String _content, String _publishTime, String _category, Vector<String> _keywords, String _publisher) {
        newsID = _newsID;
        title = _title;
        content = _content;
        publishTime = _publishTime;
        publisher = _publisher;
        category = _category;
        keywords = _keywords;
        collection = "0";
    }

    public News(SQLiteDao.RawNews n){
        newsID = n.newsId;
        category = n.category;
        collection = n.collection;
        try{
            JSONObject raw_json = new JSONObject(n.jsonData);
            title = raw_json.getString("title");
            content = raw_json.getString("content");
            publishTime = raw_json.getString("publishTime");

            publisher = raw_json.getString("publisher");
            // video = new Video(raw_json.getString("video"), newsID, dir);
            videoURL = raw_json.getString("video");


            if (raw_json.getString("image") != null && !raw_json.getString("image").equals("")){

                String[] strippedURLs = raw_json.getString("image").split(", ");
                for (String _url: strippedURLs) {
                    imageURLs.add(_url);
                }
            }

            language = raw_json.getString("language");
            url = raw_json.getString("url");
            crawlTime = raw_json.getString("crawlTime");

        } catch (JSONException e) {}
    }

    public String getCollectionStatus(){
        collection = sql.findOne(newsID).collection;
        return collection;
    }

    public void setCollection(){
        collection = "1";
        sql.update(newsID, "1");
        if (MongoDB.current_user != null)
            MongoDB.addCollection(this);
    }

    public void deleteCollection(){
        collection = "0";
        sql.update(newsID, "0");
        if (MongoDB.current_user != null)
            MongoDB.deleteCollection(this);
    }

    public void setImage(String _url) {
        String[] urls = _url.split(", ");
        for (int i = 0; i < urls.length; i++) {
            //Image img = new Image(urls[i], i, newsID, dir);
            //image.add(img);
            String processedUrl = urls[i];
            if (processedUrl == null) {
                continue;
            }
            if (processedUrl.length() > 6) {
                if (processedUrl.substring(0, 5).equals("http:")) {
                    processedUrl = "https" + processedUrl.substring(4);
                }
            }
            imageURLs.add(processedUrl);
        }
    }


    public void setVideo(String _url) {
        if (_url == null) {
            videoURL = "";
        } else if (_url.length() > 6) {
            if (_url.substring(0, 5).equals("http:")) {
                videoURL = "https" + _url.substring(4);
            } else {
                videoURL = _url;
            }
        } else {
            videoURL = "";
        }
    }


    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }
}
/*
class Image extends AsyncTask<String, Integer, Void> implements java.io.Serializable {

    String imageURL, newsID, dir, file_dir = null;
    int index;
    private Bitmap image;
    public boolean unsafeURL = false;

    Image(String url, int _index, String _newsID, String _dir) {
        imageURL = url;
        newsID = _newsID;
        dir = _dir;
        index = _index;
        file_dir = dir + newsID + "_" + index + ".png";
    }

    @Override
    protected Void doInBackground(String... strings) {
        try {
            if (imageURL.substring(0, 5).equals("http:"))
                imageURL = "https" + imageURL.substring(4);
            URL url = new URL(imageURL);
            HttpURLConnection tc = (HttpURLConnection) url.openConnection();
            tc.setConnectTimeout(200);
            tc.setReadTimeout(500);
            InputStream in = tc.getInputStream();
            int a = in.available();
            image = BitmapFactory.decodeStream(in);
            in.close();
            File file = new File(file_dir);
            FileOutputStream out = new FileOutputStream(file);
            image.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();
        } catch (MalformedURLException e) {
        } catch (IOException e) {
            unsafeURL = true;
        } catch (NullPointerException e){
            System.out.println("null pointer exception");
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
    }

    public Bitmap getImage() {
        if (image != null)
            return image;
        File file = new File(file_dir);
        if (!file.exists()) {
            this.execute();
        } else {
            try {
                FileInputStream in = new FileInputStream(file);
                image = BitmapFactory.decodeStream(in);
            } catch (IOException e) {
            }
        }
        return image;
    }

    public boolean hasImage () {
        return (image != null);
    }

    @NonNull
    @Override
    public String toString() {
        return imageURL;
    }
}
*/

/*
class Video extends AsyncTask<String, Integer, Void> implements java.io.Serializable {
    //ProgressDialog progressDialog;
    String videoURL, newsID, dir, file_dir = null;

    Video(){ }

    Video(String url, String _newsID, String _dir) {
        videoURL = url;
        newsID = _newsID;
        dir = _dir;
        file_dir = dir + newsID + ".mp4";
        this.execute(url);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        //progressDialog.show();
    }

    @Override
    protected Void doInBackground(String... strings) { //TODO: how to load the video into memory?
        File file = new File(file_dir);
        if (!file.exists()) {
            try {
                int count;
                if (videoURL.length() < 5)
                    return null;
                if (videoURL.substring(0, 5).equals("http:"))
                    videoURL = "https" + videoURL.substring(4);
                URL url = new URL(videoURL);
                HttpURLConnection tc = (HttpURLConnection) url.openConnection();
                tc.connect();
                int lenghtOfFile = tc.getContentLength();
                InputStream input = new BufferedInputStream(url.openStream(), 8192);
                OutputStream output = new FileOutputStream(file_dir);
                byte data[] = new byte[1024];
                while ((count = input.read()) != -1)
                    output.write(data, 0, count);
                output.flush();
                output.close();
            } catch (MalformedURLException e) {
            } catch (IOException e) {
            }
        }else{ // if video has already been downloaded into external memory
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        //
    }

    @NonNull
    @Override
    public String toString() {
        return videoURL;
    }
}

*/
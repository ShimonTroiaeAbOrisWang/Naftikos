package com.java.wanghongjian_and_liuxiao;

import android.os.AsyncTask;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.bson.Document;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ExecutionException;


public class MongoDB {
    static MongoConnection mc;
    static final int OFFLINE_OR_ERROR = 0, LOGIN_SUCESS = 1, REGISTER_SUCCESS = 2, WRONG_PASSWD = 3;
    static User current_user = null;
    static SQLiteDao sql = new SQLiteDao();

    MongoDB() {
    }

    static User register(String userName, String passwd) {
        User newUser = new User(userName, passwd);
        sql.updateCollection(newUser.collection);
        if (addUser(newUser))
            return newUser;
        else
            return null;
    }

    static int login(String userName, String passwd) {
        try {
            Document doc = null;
            mc = new MongoConnection("find", userName, null);
            int state = mc.execute().get();
            doc = mc.target;
            if (doc == null) {
                User newUser = register(userName, passwd);
                if (newUser != null)
                    return REGISTER_SUCCESS;
                else
                    return OFFLINE_OR_ERROR;
            } else if (!doc.getString("passwd").equals(passwd))
                return WRONG_PASSWD;
            else {
                current_user = new User(doc);
                return LOGIN_SUCESS;
            }
        } catch (ExecutionException e) {
            return OFFLINE_OR_ERROR;
        } catch (InterruptedException e) {
            return OFFLINE_OR_ERROR;
        }
    }

    static void logout() {
        sql.clearCollection();
        updateUser(current_user);
        current_user = null;
    }

    static void updateUser(User u) {
        List<String> history = new ArrayList<>(u.history), collection = new ArrayList<>();
        for (News n : u.collection)
            collection.add(n.newsID);
        Document doc = new Document("history", history).append("collection", collection).append("userName", u.userName).append("passwd", u.passwd);
        mc = new MongoConnection("updateOne", u.userName, doc);
        mc.execute();
        return;
    }

    static void addCollection(News n) {
        if (current_user._his.contains(n.newsID))
            return;
        current_user.collection.add(n);
        current_user._his.add(n.newsID);
        updateUser(current_user);
    }

    static void deleteCollection(News n) {
        for (int i=0;i<current_user.collection.size();i++){
            if (current_user.collection.get(i).newsID.equals(n.newsID)){
                current_user.collection.remove(i);
                break;
            }
        }
        current_user._his.remove(n.newsID);
        updateUser(current_user);
    }

    static void addHistory(String newsID){
        if (current_user != null) {
            current_user.history.add(newsID);
            updateUser(current_user);
        }
    }

    static void deleteHistory(String newsID){
        if (current_user != null){
            current_user.history.remove(newsID);
        }
    }

    static void updateHistory() {
        //TODO: don't know what to do
    }

    static boolean findUser(String userName) {
        boolean has_user = false;
        MongoConnection mc = new MongoConnection("find", userName, null);
        mc.execute();
        if (mc.target != null)
            has_user = true;
        return has_user;
    }

    static boolean addUser(User u) {
        try {
            Document doc = new Document();
            doc.append("userName", u.userName)
                    .append("passwd", u.passwd);
            mc = new MongoConnection("insertOne", null, doc);
            int state = mc.execute().get();
        } catch (Exception e) {
        }
        return true;
    }
}

class MongoConnection extends AsyncTask<String, Integer, Integer> {
    public String task, condition, _url;
    public Document source, target = null;

    MongoConnection(String _task, String _condition, Document _doc) {
        if (_task.equals("find"))
            _url = "http://166.111.7.106:5000/java/" + _condition;
        else if (_task.equals("insertOne"))
            _url = "http://166.111.7.106:5000/java/insert";
        else
            _url = "http://166.111.7.106:5000/java/update";
        task = _task;
        condition = _condition;
        source = _doc;
    }

    TrustManager[] trustAllCerts = new TrustManager[]{
            new X509TrustManager() {
                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return null;
                }

                public void checkClientTrusted(
                        java.security.cert.X509Certificate[] certs, String authType) {
                }

                public void checkServerTrusted(
                        java.security.cert.X509Certificate[] certs, String authType) {
                }
            }
    };

    @Override
    protected Integer doInBackground(String... strings) {
        try {
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            URL url = new URL(_url);
            HttpURLConnection tc = (HttpURLConnection) url.openConnection();
            tc.setConnectTimeout(5 * 1000);
            tc.setReadTimeout(10 * 1000);
            if (task.equals("find")) {
                tc.connect();
                BufferedReader in = new BufferedReader(new InputStreamReader(tc.getInputStream(), "UTF-8"));
                StringBuilder input = new StringBuilder();
                String inputLine;
                while ((inputLine = in.readLine()) != null)
                    input.append(inputLine);
                if (input.toString().equals("null"))
                    target = null;
                target = Document.parse(input.toString());
                return 1;
            }
            tc.setDoOutput(true);
            tc.setDoInput(true);
            tc.setUseCaches(false);
            tc.setRequestProperty("Content-Type", "application/json; utf-8");
            tc.setRequestMethod("POST");
            tc.setRequestProperty("Accept", "application/json");
            tc.connect();
            OutputStream out = tc.getOutputStream();
            JSONObject params = new JSONObject();
            params.put("userName", source.getString("userName"));
            params.put("passwd", source.getString("passwd"));
            if (task.equals("insertOne")) {
                byte[] input = params.toString().getBytes("utf-8");
                out.write(input, 0, input.length);
                out.flush();
                out.close();
            } else if (task.equals("updateOne")) {
                params.put("history", new JSONArray(source.get("history").toString()));
                params.put("collection", new JSONArray(source.get("collection").toString()));
                byte[] input = params.toString().getBytes("utf-8");
                out.write(input, 0, input.length);
                out.flush();
                out.close();
            }
            BufferedReader in = new BufferedReader(new InputStreamReader(tc.getInputStream(), "UTF-8"));
            StringBuilder input = new StringBuilder();
            String inputLine;
            while ((inputLine = in.readLine()) != null)
                input.append(inputLine);
            target = Document.parse(input.toString());
            if (target.get("result") == null)
                target = null;
        } catch (MalformedURLException e) {
        } catch (IOException e) {
            System.out.println(e);
        } catch (JSONException e) {
            System.out.println(e);
        } catch (NullPointerException e) {
            System.out.println("null pointer exception");
        } catch (Exception e) {
            System.out.println(e);
        }
        return 1;
    }
}

class User {
    public SQLiteDao sql = new SQLiteDao();
    public final String userName, passwd;
    public Vector<String> history;
    ArrayList<News> collection;
    HashSet<String> _his = new HashSet<>(), _col = new HashSet<>();

    User(Document userDoc) {
        userName = userDoc.getString("userName");
        passwd = userDoc.getString("passwd");
        history = new Vector<>((List<String>) userDoc.get("history"));
        collection = new ArrayList<>();
        for (String newsID : (List<String>) userDoc.get("collection")) {
            News n = sql.findOne(newsID);
            n.collection = "1";
            collection.add(n);
        }
        _his = new HashSet<>(history);
        _col = new HashSet<>((List<String>) userDoc.get("collection"));
        sql.updateCollection(collection);
        MainActivity.viewHistory = (Vector<String>) history.clone();
    }

    User(final String _userName, final String _passwd) {
        userName = _userName;
        passwd = _passwd;
        history = new Vector<>();
        collection = new ArrayList<>();
    }

    public void setHistory(Vector<String> _history) {
        history = _history;
    }

    public void setCollection(List<News> _collection) {
        collection = new ArrayList<>(_collection);
    }
}

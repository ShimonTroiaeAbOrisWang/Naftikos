package com.java.wanghongjian_and_liuxiao;

import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;

import org.bson.Document;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

public class MongoDB {
    static MongoCredential credential = MongoCredential.createCredential("kegger_bigsci", "aminerkg", "datiantian123!@#".toCharArray());
    static MongoClient client = new MongoClient(new ServerAddress("166.111.7.106", 30019), Arrays.asList(credential));;
    static MongoDatabase db = client.getDatabase("aminerkg");
    static MongoCollection<Document> col = db.getCollection("java");;
    // login mode
    static final int LOGIN_SUCESS = 1, NO_SUCH_USER = 2, WRONG_PASSWD = 3;
    static User current_user;

    MongoDB() { }

    static boolean register(String userName, String passwd){
        return addUser(new User(userName, passwd));
    }

    static int login(String userName, String passwd){
        Document doc = null;
        for (Document d: col.find(Filters.eq("userName", userName)))
            doc = d;
        if (doc == null)
            return NO_SUCH_USER;
        else if (!doc.getString("passwd").equals(passwd))
            return WRONG_PASSWD;
        else {
            current_user = new User(doc);
            return LOGIN_SUCESS;
        }
    }

    static void logout(){
        SQLiteDao.clearCollection();
        updateUser(current_user);
        current_user = null;
    }

    static void updateUser(User u){
        List<String> history = new ArrayList<>(u.history), collection = new ArrayList<>();
        for (News n: u.collection)
            collection.add(n.newsID);
        col.updateOne(Filters.eq("userName", u.userName), new Document("$set", new Document("history", history).append("collection", collection)));
    }

    static void addCollection(News n){
        current_user.collection.add(n);
        updateUser(current_user);
    }

    static void deleteCollection(News n){
        current_user.collection.remove(n);
        updateUser(current_user);
    }

    static void updateHistory(){
        //TODO: don't know what to do
    }

    static boolean findUser(String userName){
        boolean has_user = false;
        for (Document doc: col.find(Filters.eq("userName", userName)))
            has_user = true;
        return has_user;
    }

    static boolean addUser(User u){
        Document doc = new Document();
        for (Document d: col.find(Filters.eq("userName", u.userName)))
            return false;
        doc.append("userName", u.userName)
                .append("passwd", u.passwd);
        col.insertOne(doc);
        return true;
    }
}

class User {
    static SQLiteDao sql = new SQLiteDao();
    public final String userName, passwd;
    public Vector<String> history;
    ArrayList<News> collection;

    User(Document userDoc){
        userName = userDoc.getString("userName");
        passwd = userDoc.getString("passwd");
        history = new Vector<>((List<String>) userDoc.get("history"));
        for (String newsID: (List<String>) userDoc.get("collection")) {
            News n = sql.findOne(newsID);
            n.collection = "1";
            collection.add(n);
        }
        sql.updateCollection(collection);
    }

    User(final String _userName, final String _passwd) {
        userName = _userName;
        passwd = _passwd;
        history = new Vector<>();
        collection = new ArrayList<>();
    }

    public void setHistory(Vector<String> _history){
        history = _history;
    }

    public void setCollection(List<News> _collection){
        collection = new ArrayList<>(_collection);
    }
}

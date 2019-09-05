package com.java.wanghongjian_and_liuxiao;

import org.json.JSONException;
import org.junit.Test;

import java.util.Vector;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void testMongo() throws JSONException {
        User u = new User("abc", "123");
        u.history.add("a");
        u.history.add("b");
        u.history.add("c");
        MongoDB mongo = new MongoDB();
        mongo.addUser(u);
        mongo.updateUser(u);

        mongo.findUser("abc");
    }
}
package com.java.wanghongjian_and_liuxiao;

import org.json.JSONException;
import org.junit.Test;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void testNewsAPI() throws JSONException {
        NewsAPI api = new NewsAPI();
        api.getNews("教育", null, 3);
    }
}
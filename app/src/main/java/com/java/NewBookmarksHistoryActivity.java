package com.java;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.material.snackbar.Snackbar;
import com.java.wanghongjian_and_liuxiao.MainActivity;
import com.java.wanghongjian_and_liuxiao.News;
import com.java.wanghongjian_and_liuxiao.NewsPage;
import com.java.wanghongjian_and_liuxiao.R;
import com.nex3z.togglebuttongroup.SingleSelectToggleGroup;
import com.r0adkll.slidr.Slidr;

import java.util.ArrayList;
import java.util.Vector;

public class NewBookmarksHistoryActivity extends AppCompatActivity {

    private ListView listView;
    private SingleSelectToggleGroup single;
    Animation rotateOnce;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_bookmarks_history);
        Slidr.attach(this);

        rotateOnce = AnimationUtils.loadAnimation(this, R.anim.rotate_once);

        listView = findViewById(R.id.bookmarks_or_history_list);
        single = findViewById(R.id.group_choices);
        loadBookmarks();

        Intent intent = getIntent();

        single.setOnCheckedChangeListener(new SingleSelectToggleGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SingleSelectToggleGroup group, int checkedId) {
                if (checkedId == R.id.choice_bookmarks) {
                    loadBookmarks();
                } else {
                    loadHistory();
                }
            }
        });

        if (intent.hasExtra(MainActivity.EXTRA_HISTORY)) {
            /* load history page */
            single.check(R.id.choice_history);
        }


    }

    private void loadBookmarks () {
        final ArrayList<News> bookmarks = MainActivity.api.getBookmarks();
        final Vector<String> bookmarks_titles = new Vector<>();

        for (News news: bookmarks) {
            bookmarks_titles.add(news.getTitle());
        }

        final ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, bookmarks_titles);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MainActivity.newsToDisplay = bookmarks.get(position);
                Intent intent = new Intent(MainActivity.getContext(), NewsPage.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left); // transition
            }
        });
        findViewById(R.id.resync_button).startAnimation(rotateOnce);
    }

    private void loadHistory () {

        final ArrayList<News> history = new ArrayList<>();
        final Vector<String> history_titles = new Vector<>();
        for (int j = 0; j < MainActivity.viewHistory.size(); j += 1) {

            News news = MainActivity.api.getNewsById(MainActivity.viewHistory.elementAt(MainActivity.viewHistory.size() - j - 1));
            if (news == null) {
                continue;
            }
            history.add(news);
            history_titles.add(news.getTitle());

        }

        final ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, history_titles);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MainActivity.newsToDisplay = history.get(position);
                Intent intent = new Intent(MainActivity.getContext(), NewsPage.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left); // transition
            }
        });

        findViewById(R.id.resync_button).startAnimation(rotateOnce);

    }




    public void goBack (View view) {
        finish();
    }

    public void refresh (View view) {

        if (single.getCheckedId() == R.id.choice_bookmarks) {
            loadBookmarks();
        } else {
            loadHistory();
        }
        Snackbar.make(view, "Synchronised.", Snackbar.LENGTH_SHORT)
                .setAction("Action", null).show();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}

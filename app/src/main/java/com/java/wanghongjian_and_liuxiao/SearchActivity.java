package com.java.wanghongjian_and_liuxiao;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Layout;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Vector;


/* ADVANCED search page */


/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class SearchActivity extends AppCompatActivity {

    public int screen_width;
    Typeface ubuntuMidItalic;
    boolean showingHistory = false;
    LinearLayoutCompat historyLayout;
    Vector<String> searchHistory;

    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_search);
        historyLayout = findViewById(R.id.search_history_content);

        LinearLayoutCompat.LayoutParams params = new LinearLayoutCompat.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);


        /* *******    *
            obsolete!
          *    ******* */


        searchHistory = MainActivity.searchHistory;
        searchHistory.add("Also");
        searchHistory.add("Further");
        searchHistory.add("Another piece");
        for (int j = 0; j < searchHistory.size() && j < 5; j += 1) {
            String searchKey = searchHistory.elementAt(searchHistory.size() - j - 1);
            if (searchKey.length() == 0) {
                continue;
            }
            MaterialCardView card = new MaterialCardView(this);
            card.setBackgroundColor(getColor(R.color.colorSecondary));
            params.setMargins(18, 8, 18, 9);
            card.setLayoutParams(params);

            TextView text = new TextView(this);
            params.setMargins(12, 6, 12, 6);
            text.setLayoutParams(params);
            text.setText(searchKey);
            text.setTextColor(Color.WHITE);
            card.addView(text);
            historyLayout.addView(card);
        }

        ubuntuMidItalic = Typeface.createFromAsset(getAssets(), "Ubuntu-MediumItalic.ttf");

        TextView title = findViewById(R.id.search_title);
        title.setTypeface(ubuntuMidItalic);

        /* hide bar */
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }

        /* get screen resolution; this might be useful */
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        screen_width = displayMetrics.widthPixels;
    }


    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

    }


    public void returnButtonClick (View view) {
        finish(); // which is equivalent to pressing the return button on your phone
    }

    public void searchButtonClick (View view) {
        Intent intent = new Intent (this, MainActivity.class);

        TextInputEditText searchWordsInput = (TextInputEditText) findViewById(R.id.search_words_text_input);
        String words = searchWordsInput.getText().toString();
        /* transfer the content in the search bar to the home activity via putExtra (%key, %value) */
        intent.putExtra (MainActivity.EXTRA_SEARCH_WORDS, words);
        startActivity(intent);
    }

    public void historyButtonClick (View view) {
        showingHistory = !showingHistory;
        Button button = findViewById(R.id.search_history);
        button.setText(showingHistory ? "Hide History": "Show History");
        historyLayout.setVisibility(showingHistory? View.VISIBLE: View.GONE);
    }

}

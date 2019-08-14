package com.example.prototype;

import android.annotation.SuppressLint;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;


/* ADVANCED search page */


/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class SearchActivity extends AppCompatActivity {

    public int screen_width;
    Typeface ubuntuMidItalic;

    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_search);

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

}

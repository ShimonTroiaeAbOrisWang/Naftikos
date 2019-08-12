package com.example.prototype;

import android.content.ClipData;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.internal.NavigationMenu;
import com.google.android.material.internal.NavigationMenuItemView;
import com.google.android.material.internal.NavigationMenuView;
import com.google.android.material.snackbar.Snackbar;

import android.view.View;

import androidx.core.view.GravityCompat;
import androidx.appcompat.app.ActionBarDrawerToggle;

import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    /* constants strings as keys in key-value pairs */
    public static final String EXTRA_SEARCH_WORDS = "com.naftikos.SEARCH_WORDS";

    TextView title;
    String homePageMode;
    Typeface ubuntuMidItalic;
    boolean isAfterSearch;
    String searchString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);

        /*
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Work in progress.", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        */

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        title = findViewById(R.id.home_title);
        /* add fonts */
        ubuntuMidItalic = Typeface.createFromAsset(getAssets(), "Ubuntu-MediumItalic.ttf");
        title.setTypeface(ubuntuMidItalic);

        Intent intent = getIntent();
        if (intent.hasExtra(EXTRA_SEARCH_WORDS)) {
            // this means this page is created after search
            isAfterSearch = true;
            searchString = intent.getStringExtra(EXTRA_SEARCH_WORDS);
            homePageMode = "";
            title.setText ("Search: " + searchString + ' ');

            // TODO: 19.8.12 dynamically change the size of the title bar w.r.t the length of the title string

        } else {
            isAfterSearch = false;
            searchString = "";
            homePageMode = "Latest";
            title.setText (homePageMode + ' ');
        }

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        } else if (id == R.id.action_about) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.nav_customization) {
            /* customise navigation (clicking "+" icon) */
            NavigationView v = findViewById(R.id.nav_view);
            Menu nav_menu = v.getMenu();

            /* the following lines are only a demo */
            MenuItem sport_item = nav_menu.findItem(R.id.nav_sports);
            sport_item.setVisible(!sport_item.isVisible());
            MenuItem tech_item = nav_menu.findItem(R.id.nav_technology);
            tech_item.setVisible(!tech_item.isVisible());
            MenuItem autos_item = nav_menu.findItem(R.id.nav_autos);
            autos_item.setVisible(!autos_item.isVisible());

            // TODO: 19.8.12 redesign the function of navigation menu customization

        } else {
            if (id == R.id.nav_latest) {
                homePageMode = "Latest";
            } else if (id == R.id.nav_random) {
                homePageMode = "Random";
            } else if (id == R.id.nav_personal) {
                homePageMode = "Personal Feeds";
            } else if (id == R.id.nav_finance) {
                homePageMode = "Finance";
            } else if (id == R.id.nav_education) {
                homePageMode = "Education";
            } else if (id == R.id.nav_entertainment) {
                homePageMode = "Entertainment";
            } else if (id == R.id.nav_sports) {
                homePageMode = "Sports";
            } else if (id == R.id.nav_technology) {
                homePageMode = "Technology";
            } else if (id == R.id.nav_autos) {
                homePageMode = "Autos";
            }

            /* change home page according to mode */
            TextView title = findViewById(R.id.home_title);
            title.setText (homePageMode + ' ');

            DrawerLayout drawer = findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
        }

        return true;
    }

    public void showSearchPage (View view) {
        /* search button pressed */
        Intent intent = new Intent (this, SearchActivity.class);
        startActivity(intent);
    }
}

package com.java.wanghongjian_and_liuxiao;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.nfc.tech.TagTechnology;
import android.os.AsyncTask;
import android.os.Bundle;

import com.java.wanghongjian_and_liuxiao.ui.login.LoginActivity;
import com.google.android.material.card.MaterialCardView;

import android.os.Environment;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.core.view.GravityCompat;
import androidx.appcompat.app.ActionBarDrawerToggle;

import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputEditText;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.Menu;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.io.File;
import java.util.Random;
import java.util.Vector;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    /* constants strings as keys in key-value pairs */
    public static final String EXTRA_SEARCH_WORDS = "com.naftikos.SEARCH_WORDS";
    public static final int UPDATE_NEWS = 1, LOAD_NEWS_BEFORE = 2, OTHERS = 3;

    /* constants for home page mode */
    private static final int HOME_SEARCH = -1;
    private static final int HOME_LATEST = 0;
    private static final int HOME_RAND = 1;
    private static final int HOME_PERSONAL = 2;
    private static final int HOME_FINANCE = 3;
    private static final int HOME_EDUCATION = 4;
    private static final int HOME_ENT = 5;
    private static final int HOME_SPORTS = 6;
    private static final int HOME_TECH = 7;
    private static final int HOME_AUTOS = 8;
    private static final int HOME_MILITARY = 9;
    private static final int HOME_CULTURE = 10;
    private static final int HOME_SOCIETY = 11;
    private static final int HOME_HEALTH = 12;

    private static final String[] homepageModeTitles = {"Latest", "Random", "Personal Feeds", "Finance", "Education", "Entertainment", "Sports", "Technology", "Autos", "Military", "Culture", "Society", "Health"};
    private static final int cardBackgroundColors [] = {Color.rgb(0xF0-2, 0xF0, 0xF0), Color.rgb(0xF3-2, 0xF0, 0xF0), Color.rgb(0xF0-2, 0xF3, 0xF0), Color.rgb(0xF0-2, 0xF0, 0xF3),
            Color.rgb(0xEE-2, 0xF0, 0xF0), Color.rgb(0xF0-2, 0xEE, 0xF0), Color.rgb(0xF0-2, 0xF0, 0xEE),
            Color.rgb(0xF2-2, 0xF2, 0xF0), Color.rgb(0xF2-2, 0xF0, 0xF2), Color.rgb(0xF0-2, 0xF2, 0xF2)};

    private static final int numberOfCardBackground = 10;

    public static Context context;
    TextView title;
    int homePageMode;
    Typeface ubuntuMidItalic;
    boolean isAfterSearch = false;
    String searchString;
    AlertDialog searchDialog;
    TextInputEditText searchDialogText;
    MaterialCardView newsCardModelFirst, newsCardModel;

    Vector<News> newsList;
    static News newsToDisplay;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /*
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Work in progress.", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        */

        File external_dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/wanghongjian_and_liuxiao/");
        if (!external_dir.exists())
            external_dir.mkdirs();

        newsList = new Vector<News>();
        context = this;

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
            homePageMode = HOME_SEARCH;
            title.setText("Search: " + searchString + ' ');

            // TODO: 19.8.12 dynamically change the size of the title bar w.r.t the length of the title string

        } else {
            isAfterSearch = false;
            searchString = "";
            homePageMode = HOME_LATEST;
            title.setText(homepageModeTitles[homePageMode] + ' ');
        }


        MaterialCardView titleCard = findViewById(R.id.title_material_card);

        titleCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View _v) {
                /* when title is clicked, open the navigation drawer */
                DrawerLayout _drawer = findViewById(R.id.drawer_layout);
                _drawer.openDrawer(GravityCompat.START);
            }
        });

        /* build search dialog */

        AlertDialog.Builder searchDialogBuilder = new AlertDialog.Builder(this);
        searchDialogBuilder.setTitle("News Search");

        searchDialogText = new TextInputEditText(this);
        searchDialogText.setHint("Find news with...");

        FrameLayout searchContainer = new FrameLayout(this);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(30, 30, 30, 20);
        searchDialogText.setSingleLine();
        searchDialogText.setLayoutParams(params);
        searchContainer.addView(searchDialogText);

        searchDialogBuilder.setView(searchContainer);
        searchDialogBuilder.setPositiveButton("Search", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                /* search */
                searchString = searchDialogText.getText().toString();
                if (searchString.isEmpty()) {
                    return;
                }
                isAfterSearch = true;
                homePageMode = HOME_SEARCH;
                title.setText("Search: " + searchString + ' ');
                getNewsFromServer();
                // loadNews();
            }
        });

        searchDialogBuilder.setNegativeButton("Advanced", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                advancedSearch();
            }
        });

        searchDialog = searchDialogBuilder.create();

        /* set the style of a news card for future creation */
        newsCardModelFirst = findViewById(R.id.news_card_01);
        newsCardModel = findViewById(R.id.news_card_02);

        // swipe to refresh
        SwipeRefreshLayout refreshLayout = findViewById(R.id.news_refresh);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getNewsFromServer(UPDATE_NEWS);
            }
        });



        getNewsFromServer();
        // loadNews();  // no longer needed
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
            MenuItem vItem = nav_menu.findItem(R.id.nav_sports);
            vItem.setVisible(!vItem.isVisible());
            vItem = nav_menu.findItem(R.id.nav_technology);
            vItem.setVisible(!vItem.isVisible());
            vItem = nav_menu.findItem(R.id.nav_autos);
            vItem.setVisible(!vItem.isVisible());
            vItem = nav_menu.findItem(R.id.nav_military);
            vItem.setVisible(!vItem.isVisible());
            vItem = nav_menu.findItem(R.id.nav_society);
            vItem.setVisible(!vItem.isVisible());
            vItem = nav_menu.findItem(R.id.nav_culture);
            vItem.setVisible(!vItem.isVisible());
            vItem = nav_menu.findItem(R.id.nav_health);
            vItem.setVisible(!vItem.isVisible());

            // TODO: 19.8.12 redesign the function of navigation menu customization

        } else if (id == R.id.nav_preferences) {
            /* preferences page */
        } else if (id == R.id.nav_about) {
            /* about page */
        } else {
            if (id == R.id.nav_latest) {
                homePageMode = HOME_LATEST;
            } else if (id == R.id.nav_random) {
                homePageMode = HOME_RAND;
            } else if (id == R.id.nav_personal) {
                homePageMode = HOME_PERSONAL;
            } else if (id == R.id.nav_finance) {
                homePageMode = HOME_FINANCE;
            } else if (id == R.id.nav_education) {
                homePageMode = HOME_EDUCATION;
            } else if (id == R.id.nav_entertainment) {
                homePageMode = HOME_ENT;
            } else if (id == R.id.nav_sports) {
                homePageMode = HOME_SPORTS;
            } else if (id == R.id.nav_technology) {
                homePageMode = HOME_TECH;
            } else if (id == R.id.nav_autos) {
                homePageMode = HOME_AUTOS;
            } else if (id == R.id.nav_military) {
                homePageMode = HOME_MILITARY;
            } else if (id == R.id.nav_culture) {
                homePageMode = HOME_CULTURE;
            } else if (id == R.id.nav_society) {
                homePageMode = HOME_SOCIETY;
            } else if (id == R.id.nav_health) {
                homePageMode = HOME_HEALTH;
            }

            /* change home page according to mode */
            TextView title = findViewById(R.id.home_title);
            title.setText(homepageModeTitles[homePageMode] + ' ');

            DrawerLayout drawer = findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);

            isAfterSearch = false;

            getNewsFromServer();
            // loadNews();  // no longer needed
        }

        return true;
    }

    public void showSearchPage(View view) {
        /* search button pressed */
        searchDialog.show();
        return;
    }

    public void advancedSearch() {
        Intent intent = new Intent(this, SearchActivity.class);
        startActivity(intent);
    }

    public void showLogin(View view) {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    public void getNewsFromServer () {getNewsFromServer(OTHERS);}

    public void getNewsFromServer(int getMode) {
        newsList.clear();
        AsyncNewsRetriever retriever = new AsyncNewsRetriever();
        if (getMode == UPDATE_NEWS) {
            retriever.hasDialog = false;
        }
        // FIXME: 19.8.30 getMode = UPDATE_NEWES does not work!
        retriever.mode = getMode;
        retriever.execute("");
    }

    public void loadNews() {
        /* load news into home screen */
        LinearLayout newsContainer = findViewById(R.id.home_news_container);
        newsContainer.removeAllViews();

        ScrollView mainScroll = findViewById(R.id.main_scroll_view);
        mainScroll.scrollTo(0, 0);

        int newsCounter = 0;
        for (News newsItem : newsList) {
            newsContainer.addView(generateHomeNewsCard(newsCounter, newsItem));
            newsCounter += 1;
            // TODO: 19.8.15 specify params for news cards
        }

        /* end the refresh process on finish */
        SwipeRefreshLayout refreshLayout = findViewById(R.id.news_refresh);
        refreshLayout.setRefreshing(false);

    }

    private MaterialCardView generateHomeNewsCard(final int newsCounter, News newsItem) {
        MaterialCardView newCard = new MaterialCardView(this);
        // modelCard: pre-drawn news card (created with XML but removed in run time)
        MaterialCardView modelCard = (newsCounter == 0) ? newsCardModelFirst : newsCardModel;

        /* it is fucking stupid that Android view does not support cloning! */
        newCard.setLayoutParams(modelCard.getLayoutParams());
        newCard.setCardElevation(modelCard.getCardElevation());
        //newCard.setCardBackgroundColor(modelCard.getCardBackgroundColor());
        //newCard.setBackground(modelCard.getBackground());
        Random random = new Random();
        newCard.setBackgroundColor(cardBackgroundColors[random.nextInt(numberOfCardBackground)]);
        newCard.setForegroundGravity(modelCard.getForegroundGravity());

        LinearLayout inCardLayout = new LinearLayout(this);
        inCardLayout.setOrientation(LinearLayout.VERTICAL);
        inCardLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        TextView textInCard = new TextView(this);
        // TODO: 19.8.15 put content, image, etc. to the card
        textInCard.setText(newsItem.getTitle());
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(40, 40, 40, 20);
        textInCard.setLayoutParams(params);
        textInCard.setTextColor(Color.rgb(0x27, 0x27, 0x27));
        textInCard.setTextSize(17);

        inCardLayout.addView(textInCard);

        TextView postscriptText = new TextView(this);
        params.setMargins(40, 20, 40, 40);
        postscriptText.setLayoutParams(params);
        postscriptText.setText("1 hr ago        " + newsItem.category); // TODO: 19.8.30 specify time
        postscriptText.setTextSize(12);
        postscriptText.setTextColor(Color.rgb(0x90, 0x90, 0x90));
        inCardLayout.addView(postscriptText);

        newCard.addView(inCardLayout);

        newCard.setClickable(true);
        newCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openNewsByNumber(newsCounter, view);
            }
        });
        return newCard;
    }

    /* open news page according to the number of the news (in newsList) */
    private void openNewsByNumber(int newsNumber, View view) {
        Intent intent = new Intent(this, NewsPage.class);
        newsToDisplay = newsList.elementAt(newsNumber);
        startActivity(intent);
    }


    /* load news in background to improve UI smoothness */
    private class AsyncNewsRetriever extends AsyncTask<String, String, Vector<News>> {

        private String resp;
        ProgressDialog progressDialog;
        public int mode = OTHERS;
        public boolean hasDialog = true;

        @Override
        protected Vector<News> doInBackground(String... params) {
            publishProgress("Loading..."); // Calls onProgressUpdate()
            try {
                NewsAPI api = new NewsAPI();
                if (isAfterSearch) {
                    return api.getNews(searchString, null, OTHERS);
                } else {
                    String[] categories = {"", "", "", "财经", "教育", "娱乐", "体育", "科技", "汽车", "军事", "文化", "社会", "健康"};
                    //return api.testGetNews("https://api2.newsminer.net/svc/news/queryNewsList?words=野熊&size=1&startDate=2018-08-15&endDate=2018-08-21");
                    return api.getNews("", categories[homePageMode], mode);     // refer to the top for modes
                }
            } catch (Exception e) {
                e.printStackTrace();
                resp = e.getMessage();
            }
            return null;
        }


        protected void onPostExecute(Vector<News> v) {
            // execution of result of Long time consuming operation
            if (hasDialog) {
                progressDialog.dismiss();
            }
            newsList = v;
            loadNews();
        }


        @Override
        protected void onPreExecute() {
            if (hasDialog) {
                progressDialog = ProgressDialog.show(MainActivity.this, "Loading", "Ναυτικός is updating news...");
            }
        }
    }


    public static Context getContext() {
        return context;
    }

}

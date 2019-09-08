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

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.snackbar.Snackbar;
import com.java.NewBookmarksHistoryActivity;
import com.java.wanghongjian_and_liuxiao.ui.login.LoginActivity;
import com.google.android.material.card.MaterialCardView;

import android.os.Environment;
import android.provider.ContactsContract;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.appcompat.app.ActionBarDrawerToggle;

import android.view.MenuItem;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.textfield.TextInputEditText;
import com.nex3z.togglebuttongroup.MultiSelectToggleGroup;
import com.nex3z.togglebuttongroup.button.LabelToggle;
import com.nex3z.togglebuttongroup.button.ToggleButton;
import com.snatik.storage.Storage;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.Menu;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import org.apache.commons.lang3.SerializationUtils;
import org.w3c.dom.Text;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;
import java.util.Vector;
import java.util.HashSet;

import static android.provider.ContactsContract.CommonDataKinds.Website.URL;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    /* constants strings as keys in key-value pairs */
    public static final String EXTRA_SEARCH_WORDS = "com.naftikos.SEARCH_WORDS";
    public static final String EXTRA_PREFERRED = "com.naftikos.PREFERRED";
    public static final String EXTRA_PREFERRED_II = "com.naftikos.PREFERRED_II";
    public static final String EXTRA_BOOKMARKS = "com.naftikos.BOOKMARKS";
    public static final String EXTRA_HISTORY = "com.naftikos.HISTORY";
    public static final String EXTRA_VIDEO_URL = "com.naftikos.VIDEO_URL";

    static final int CATEGORIES_REQUEST = 0xD000;
    static final int LOGIN_REQUEST = 0xD001;
    static final int CARD_ID_OFFSET = 0xA83000;
    static final int NEWSTITLE_ID_OFFSET = 0xA9B000;
    static final int PUBLISHER_ID_OFFSET = 0xAC2000;
    public static final int NAVIGATE_NEWS = 0, UPDATE_NEWS = 1, LOAD_NEWS_BEFORE = 2, OTHERS = 3;

    /* constants for home page mode */
    static final int HOME_SEARCH = -1;
    static final int HOME_LATEST = 0;
    static final int HOME_RAND = 1;
    static final int HOME_PERSONAL = 2;
    static final int HOME_FINANCE = 3;
    static final int HOME_EDUCATION = 4;
    static final int HOME_ENT = 5;
    static final int HOME_SPORTS = 6;
    static final int HOME_TECH = 7;
    static final int HOME_AUTOS = 8;
    static final int HOME_MILITARY = 9;
    static final int HOME_CULTURE = 10;
    static final int HOME_SOCIETY = 11;
    static final int HOME_HEALTH = 12;

    private static final String[] homepageModeTitles = {"Latest", "Random", "Personal Feeds", "Finance", "Education", "Entertainment", "Sports", "Technology", "Autos", "Military", "Culture", "Society", "Health"};
    static final String[] categories = {"", "", "", "财经", "教育", "娱乐", "体育", "科技", "汽车", "军事", "文化", "社会", "健康"};
    private boolean[] isPreferredTopic = { true, true, true, true, false, true, true, true, false, false, false, true, false };
    private MenuItem[] navItems = { null, null, null, null, null, null, null, null, null, null, null, null, null };
    private static final int DIM_TITLE_COLOR = Color.rgb(0x6A, 0x6A, 0x6A);
    private static final int TITLE_COLOR = Color.rgb(0x23, 0x23, 0x23);
    private static final int TITLE_DARK_COLOR = Color.rgb(0xA2, 0xA2, 0xA2);
    private static final int CARD_DARK = Color.rgb(0x0B, 0x0B, 0x19);
    private static final int CARD_LIGHT = Color.rgb(0xFF, 0xFF, 0xFF);
    private static final int PUB_CARD_LIGHT = Color.rgb(0xEE, 0xEE, 0xF2);
    private static final int PUB_CARD_DARK = Color.rgb(0x16, 0x16, 0x2A);

    public static Context context;
    TextView title;
    int homePageMode;
    static Typeface ubuntuMidItalic;
    boolean isAfterSearch = false;
    static boolean nightShift = false;
    String searchString;
    //static FrameLayout dimmer;
    AlertDialog searchDialog;
    TextInputEditText searchDialogText;
    AlertDialog searchHistoryDialog;

    MaterialCardView newsCardModelFirst, newsCardModel;
    int maxScroll = 0;
    TextView textSwipeMore;
    int newsCountBeforeSwipeMore = 0;

    float titleOriginalSize;

    static NavigationView navigationView;
    static Menu nav_menu;

    Vector<News> newsList;
    Vector<News> newsToAdd;
    static public News newsToDisplay;
    static public NewsAPI api;
    HashSet<String> viewedNewsSet;
    HashSet<String> imageDisplayedSet;
    static Vector<String> searchHistory;
    static public Vector<String> viewHistory;
    static public HashSet<String> keywordsBlacklist; // TODO: 19.9.8 this set contains all the key words to hide 
    SQLiteDao sql;

    Storage storage;
    String externPath;

    public static String email;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        File external_dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/wanghongjian_and_liuxiao/");
        if (!external_dir.exists()) {
            external_dir.mkdirs();
        } // TODO: 19.9.5 I suggest that we use the following way for file I/O:
        // init
        storage = new Storage(getApplicationContext());
        // get external storage
        externPath = storage.getExternalStorageDirectory() + File.separator + "Naftikos";
        // new dir
        if (!storage.isDirectoryExists(externPath)) {
            storage.createDirectory(externPath);
        }

        if (storage.isFileExist(externPath + File.separator + "config.ini")) {
            byte[] content = storage.readFile(externPath + File.separator + "config.ini");
            try {
                isPreferredTopic = SerializationUtils.deserialize(content);
            } catch (Exception e) { }
        }

        viewHistory = new Vector<>();
        if (storage.isFileExist(externPath + File.separator + "view.history")) {
            byte[] content = storage.readFile(externPath + File.separator + "view.history");
            try {
                viewHistory = SerializationUtils.deserialize(content);
            } catch (Exception e) { }
        }

        searchHistory = new Vector<>();
        if (storage.isFileExist(externPath + File.separator + "search.history")) {
            byte[] content = storage.readFile(externPath + File.separator + "search.history");
            try {
                searchHistory = SerializationUtils.deserialize(content);
            } catch (Exception e) { }
        }


        newsList = new Vector<>();
        newsToAdd = new Vector<>();
        keywordsBlacklist = new HashSet<>();
        context = this;

        api = new NewsAPI();

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        nav_menu = navigationView.getMenu();

        navItems[HOME_FINANCE] = nav_menu.findItem(R.id.nav_finance);
        navItems[HOME_EDUCATION] = nav_menu.findItem(R.id.nav_education);
        navItems[HOME_ENT]= nav_menu.findItem(R.id.nav_entertainment);
        navItems[HOME_SPORTS] = nav_menu.findItem(R.id.nav_sports);
        navItems[HOME_TECH] = nav_menu.findItem(R.id.nav_technology);
        navItems[HOME_AUTOS] = nav_menu.findItem(R.id.nav_autos);
        navItems[HOME_MILITARY] = nav_menu.findItem(R.id.nav_military);
        navItems[HOME_CULTURE] = nav_menu.findItem(R.id.nav_culture);
        navItems[HOME_SOCIETY] = nav_menu.findItem(R.id.nav_society);
        navItems[HOME_HEALTH] = nav_menu.findItem(R.id.nav_health);

        for (int j = HOME_FINANCE; j <= HOME_HEALTH; j += 1) {
            navItems[j].setVisible(isPreferredTopic[j]);
        }


        title = findViewById(R.id.home_title);
        titleOriginalSize = title.getTextSize();

        /* add fonts */
        ubuntuMidItalic = Typeface.createFromAsset(getAssets(), "Ubuntu-MediumItalic.ttf");
        title.setTypeface(ubuntuMidItalic);

        Intent intent = getIntent();
        if (intent.hasExtra(EXTRA_SEARCH_WORDS)) {
            // this means this page is created after search
            // obsolete
            isAfterSearch = true;
            searchString = intent.getStringExtra(EXTRA_SEARCH_WORDS);
            homePageMode = HOME_SEARCH;
            title.setText("Search: " + searchString + ' ');

            if (searchString.length() < 5) {
                title.setTextSize(TypedValue.COMPLEX_UNIT_PX, titleOriginalSize);
            } else if (searchString.length() < 8){
                title.setTextSize(TypedValue.COMPLEX_UNIT_PX, titleOriginalSize * 0.7f);
            } else {
                title.setTextSize(TypedValue.COMPLEX_UNIT_PX, titleOriginalSize * 0.5f);
            }

            // TODO: 19.8.12 dynamically change the size of the title bar w.r.t the length of the title string

        } else {
            isAfterSearch = false;
            searchString = "";
            homePageMode = HOME_LATEST;
            title.setText(homepageModeTitles[homePageMode] + ' ');
            title.setTextSize(TypedValue.COMPLEX_UNIT_PX, titleOriginalSize);

        }

        //dimmer = findViewById(R.id.dimmer);

        MaterialCardView titleCard = findViewById(R.id.title_material_card);

        titleCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View _v) {
                /* when title is clicked, open the navigation drawer */
                DrawerLayout _drawer = findViewById(R.id.drawer_layout);
                _drawer.openDrawer(GravityCompat.START);
            }
        });

        /* swipe down to load more news! */
        final ScrollView mainScroll = findViewById(R.id.main_scroll_view);
        mainScroll.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == android.view.MotionEvent.ACTION_UP) {
                    maxScroll = mainScroll.getChildAt(0).getHeight() - mainScroll.getHeight() + mainScroll.getPaddingBottom() + mainScroll.getPaddingTop();
                    if (mainScroll.getScrollY() >= maxScroll) {
                        getNewsFromServer(LOAD_NEWS_BEFORE);
                    }
                }
                return false;
            }
        });

        /* build search dialog & search history dialog */
        final AlertDialog.Builder historyDialogBuilder = new AlertDialog.Builder(this);
        historyDialogBuilder.setTitle("Search History");
        historyDialogBuilder.setNegativeButton("Return", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                searchDialog.show();
            }
        });

        final AlertDialog.Builder searchDialogBuilder = new AlertDialog.Builder(this);
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
                if (searchString.length() < 5) {
                    title.setTextSize(TypedValue.COMPLEX_UNIT_PX, titleOriginalSize);
                } else if (searchString.length() < 8){
                    title.setTextSize(TypedValue.COMPLEX_UNIT_PX, titleOriginalSize * 0.7f);
                } else {
                    title.setTextSize(TypedValue.COMPLEX_UNIT_PX, titleOriginalSize * 0.5f);
                }

                getNewsFromServer();
            }
        });

        searchDialogBuilder.setNegativeButton("Show History", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // searchHistory = api.getSearchHistory();
                Vector<String> searchHistorySet = new Vector<>();
                for (int j = 0; j < searchHistory.size(); j += 1) {
                    String s = searchHistory.elementAt(searchHistory.size() - 1 - j);
                    if (s.length() > 0 && !searchHistorySet.contains(s)) {
                        searchHistorySet.add(s);
                    }
                    if (searchHistorySet.size() == 9) {
                        break;
                    }
                }

                ListView historyListView= new ListView(getContext());
                final ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),android.R.layout.simple_list_item_1, searchHistorySet);
                historyListView.setAdapter(adapter);

                historyListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        /* search */
                        searchString = adapter.getItem(position);
                        searchHistoryDialog.dismiss();
                        isAfterSearch = true;
                        homePageMode = HOME_SEARCH;
                        title.setText("Search: " + searchString + ' ');
                        if (searchString.length() < 5) {
                            title.setTextSize(TypedValue.COMPLEX_UNIT_PX, titleOriginalSize);
                        } else if (searchString.length() < 8){
                            title.setTextSize(TypedValue.COMPLEX_UNIT_PX, titleOriginalSize * 0.7f);
                        } else {
                            title.setTextSize(TypedValue.COMPLEX_UNIT_PX, titleOriginalSize * 0.5f);
                        }

                        getNewsFromServer();
                    }
                });

                historyDialogBuilder.setView(historyListView);

                searchHistoryDialog = historyDialogBuilder.create();
                searchHistoryDialog.show();
                //advancedSearch();
            }
        });


        searchDialog = searchDialogBuilder.create();



        /* set the style of a news card for future creation */
        newsCardModelFirst = findViewById(R.id.news_card_01);
        newsCardModel = findViewById(R.id.news_card_02);
        newsCardModelFirst.setVisibility(View.INVISIBLE);
        newsCardModel.setVisibility(View.INVISIBLE);



        // swipe to refresh
        SwipeRefreshLayout refreshLayout = findViewById(R.id.news_refresh);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getNewsFromServer(UPDATE_NEWS);
            }
        });


        viewedNewsSet = new HashSet<>();
        for (String id: viewHistory) {
            viewedNewsSet.add(id);
        }
        imageDisplayedSet = new HashSet<>();

        getNewsFromServer();

        if (!isAfterSearch) {
            Snackbar.make(title, "Welcome!", Snackbar.LENGTH_LONG).setAction("Action", null).setDuration(1500).show();
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
            categorySetting();
            return true;
        } else if (id == R.id.action_toggle_dark) {
            nightShift = !nightShift;

            LinearLayout linearLayout = findViewById(R.id.home_news_container);
            linearLayout.setBackgroundColor(nightShift ? getResources().getColor(android.R.color.black): getResources().getColor(R.color.colorPrimary));

            for (int i = 0; i < newsList.size(); i += 1) {
                MaterialCardView card = findViewById(CARD_ID_OFFSET + i);
                card.setBackgroundColor(nightShift ? CARD_DARK: CARD_LIGHT);

                TextView text = findViewById(NEWSTITLE_ID_OFFSET + i);
                if (viewedNewsSet.contains(newsList.elementAt(i).newsID)) {
                    text.setTextColor(DIM_TITLE_COLOR);
                } else {
                    text.setTextColor(nightShift ? TITLE_DARK_COLOR : TITLE_COLOR);
                }

                MaterialCardView pubCard = findViewById(PUBLISHER_ID_OFFSET + i);
                pubCard.setBackgroundColor(nightShift ? PUB_CARD_DARK: PUB_CARD_LIGHT);
            }


            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.nav_preferences) {
            /* customise navigation (clicking "+" icon) */
            NavigationView v = findViewById(R.id.nav_view);
            Menu nav_menu = v.getMenu();

            /* the following lines are only a demo */
            /*
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
            vItem.setVisible(!vItem.isVisible());*/

            // TODO: 19.8.12 redesign the function of navigation menu customization
            categorySetting();

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
            title = findViewById(R.id.home_title);
            title.setText(homepageModeTitles[homePageMode] + ' ');
            title.setTextSize(TypedValue.COMPLEX_UNIT_PX, titleOriginalSize);



            DrawerLayout drawer = findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);

            isAfterSearch = false;

            getNewsFromServer();
        }

        return true;
    }

    public void showSearchPage(View view) {
        /* search button pressed */
        searchDialog.show();
        return;
    }

    private void advancedSearch() {

        Intent intent = new Intent(this, SearchActivity.class);
        startActivity(intent);
    }

    private void categorySetting () {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        Intent intent = new Intent (this, CategorySettingActivity.class);
        intent.putExtra(EXTRA_PREFERRED, isPreferredTopic);
        startActivityForResult(intent, CATEGORIES_REQUEST);
        overridePendingTransition(R.anim.push_up_in, R.anim.push_down_out); // transition
        //dimmer.setVisibility(View.VISIBLE);
    }

    public void showLogin(View view) {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        Intent intent = new Intent(this, LoginActivity.class);
        startActivityForResult(intent, LOGIN_REQUEST);
        overridePendingTransition(R.anim.push_down_in, R.anim.push_up_out);
    }

    public void getNewsFromServer () { getNewsFromServer(isAfterSearch? OTHERS : NAVIGATE_NEWS); }

    public void getNewsFromServer(int getMode) {
        AsyncNewsRetriever retriever = new AsyncNewsRetriever();
        retriever.mode = getMode;
        retriever.execute("");
    }

    public void loadNews(boolean toAdd) {
        /* load news into home screen */
        LinearLayout newsContainer = findViewById(R.id.home_news_container);
        if (!toAdd) {
            newsContainer.removeAllViews();
        } else {
            newsContainer.removeView(textSwipeMore);
        }
        /* debug: load news with >= 2 images
        Vector<News> v = new Vector<>();
        for (News news: newsList) {
            if (news.image.size() > 1) {
                v.add(news);
            }
        }
        newsList = v;
         debug: load news with >= 2 images  */

        int newsCounter = 0;

        for (News newsItem : toAdd ? newsToAdd: newsList) {
            newsContainer.addView(generateHomeNewsCard(newsCounter, newsItem, toAdd));
            newsCounter += 1;
        }

        textSwipeMore = new TextView(this);
        textSwipeMore.setText("Swipe down to load more news.");
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 45, 0, 45);
        textSwipeMore.setLayoutParams(params);
        textSwipeMore.setGravity(Gravity.CENTER);
        textSwipeMore.setTextColor(Color.rgb(0x87, 0x87, 0x87));
        textSwipeMore.setTextSize(15);

        newsContainer.addView(textSwipeMore);

        /* end the refresh process on finish */
        SwipeRefreshLayout refreshLayout = findViewById(R.id.news_refresh);
        refreshLayout.setRefreshing(false);

    }

    private MaterialCardView generateHomeNewsCard(final int newsCounter, News newsItem, final boolean toAdd) {
        final int logicalNum = newsCounter + (toAdd ? newsCountBeforeSwipeMore: 0);

        MaterialCardView newCard = new MaterialCardView(this);
        // modelCard: pre-drawn news card (created with XML but removed in run time)
        MaterialCardView modelCard = (newsCounter == 0) ? newsCardModelFirst : newsCardModel;

        /* it is fucking stupid that Android view does not support cloning! */
        newCard.setLayoutParams(modelCard.getLayoutParams());


        LinearLayout inCardLayout = new LinearLayout(this);
        inCardLayout.setOrientation(LinearLayout.VERTICAL);
        inCardLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        MaterialCardView publisherCard = new MaterialCardView(this);
        publisherCard.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        publisherCard.setBackgroundColor(nightShift? PUB_CARD_DARK: PUB_CARD_LIGHT);
        publisherCard.setCardElevation(6);


        final TextView publisherText = new TextView(this);
        publisherText.setText(newsItem.publisher);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(40, 17, 40, 10);
        publisherText.setLayoutParams(params);
        publisherText.setTextSize(15);
        publisherText.setTextColor(Color.rgb(0x50, 0x68, 0x86));

        publisherCard.addView(publisherText);
        publisherCard.setId(PUBLISHER_ID_OFFSET + logicalNum);
        inCardLayout.addView(publisherCard);

        View lineView = new View (this);
        ViewGroup.LayoutParams lineParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1);
        lineView.setLayoutParams(lineParams);
        lineView.setBackgroundColor(Color.rgb(0xC0, 0xC0, 0xC0));

        inCardLayout.addView(lineView);

        final TextView textInCard = new TextView(this);

        textInCard.setText(newsItem.getTitle());
        FrameLayout.LayoutParams params2 = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        params2.setMargins(40, 24, 40, 12);
        textInCard.setLayoutParams(params2);
        if (viewedNewsSet.contains(newsItem.newsID)) {
            textInCard.setTextColor(DIM_TITLE_COLOR);
        } else {
            textInCard.setTextColor(nightShift ? TITLE_DARK_COLOR : TITLE_COLOR);
        }

        textInCard.setTextSize(17);
        textInCard.setId(NEWSTITLE_ID_OFFSET + logicalNum);
        inCardLayout.addView(textInCard);

        TextView postscriptText = new TextView(this);
        params.setMargins(40, 8, 40, 18);
        postscriptText.setLayoutParams(params);
        postscriptText.setText(newsItem.publishTime + "           " + newsItem.category); // TODO: 19.8.30 specify time
        postscriptText.setTextSize(12);
        postscriptText.setTextColor(Color.rgb(0x90, 0x90, 0x90));
        inCardLayout.addView(postscriptText);


        newsItem.layout = inCardLayout;

        newCard.addView(inCardLayout);
        newCard.setBackgroundColor(nightShift ? CARD_DARK: CARD_LIGHT);


        newCard.setClickable(true);
        newCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openNewsByNumber(logicalNum, view, textInCard);
            }
        });

        newCard.setId(CARD_ID_OFFSET + logicalNum);
        return newCard;
    }

    /* open news page according to the number of the news (in newsList) */
    private void openNewsByNumber(int newsNumber, View view, TextView textToDim) {
        Intent intent = new Intent(this, NewsPage.class);
        newsToDisplay = newsList.elementAt(newsNumber);
        Recommender.update(newsToDisplay);

        if (viewHistory.contains(newsToDisplay.newsID)) {
            viewHistory.remove(newsToDisplay.newsID);
            MongoDB.deleteHistory(newsToDisplay.newsID);
        }
        if (viewHistory.size() == 50) {
            viewHistory.remove(0);
            if (MongoDB.current_user != null)
                MongoDB.deleteHistory(newsToDisplay.newsID);
        }
        viewHistory.add(newsToDisplay.newsID);
        if (MongoDB.current_user != null)
            MongoDB.addHistory(newsToDisplay.newsID);
        storage.createFile(externPath + File.separator + "view.history", SerializationUtils.serialize(viewHistory));

        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left); // transition
        viewedNewsSet.add(newsToDisplay.newsID);
        textToDim.setTextColor(DIM_TITLE_COLOR); //dim text after open
    }


    /* load news in background to improve UI smoothness */
    private class AsyncNewsRetriever extends AsyncTask<String, String, Vector<News>> {

        ProgressDialog progressDialog;
        public int mode = isAfterSearch? OTHERS : NAVIGATE_NEWS;

        @Override
        protected Vector<News> doInBackground(String... params) {
            publishProgress("Loading..."); // Calls onProgressUpdate()
            try {
                if (isAfterSearch) {

                    if (searchHistory.size() == 15) {
                        viewHistory.remove(0);
                    }
                    searchHistory.add(searchString);
                    storage.createFile(externPath + File.separator + "search.history", SerializationUtils.serialize(searchHistory));

                    Vector<News> vGet = api.getNews(searchString, null, mode, homePageMode == HOME_PERSONAL);
                    if (vGet == null) {
                        return new Vector<>();
                    } else {
                        return vGet;
                    }
                } else {

                    //return api.testGetNews("https://api2.newsminer.net/svc/news/queryNewsList?words=野熊&size=1&startDate=2018-08-15&endDate=2018-08-21");
                    Vector<News> vGet = api.getNews("", categories[homePageMode], mode, homePageMode == HOME_PERSONAL);     // refer to the top for modes
                    if (vGet == null) {
                        return new Vector<>();
                    } else {
                        return vGet;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return new Vector<>();
        }


        protected void onPostExecute(Vector<News> v) {
            // execution of result of Long time consuming operation

            if (mode != UPDATE_NEWS) {
                progressDialog.dismiss();
            }

            if (mode == UPDATE_NEWS) {
                for (News news: newsList) {
                    v.add(news);
                    if (v.size() > 100) {
                        break;
                    }
                }
                newsList = v;
            } else if (mode == LOAD_NEWS_BEFORE) {
                newsCountBeforeSwipeMore = newsList.size();
                newsList.addAll(v);
                newsToAdd = v;
            } else {
                newsList = v;
            }
            loadNews(mode == LOAD_NEWS_BEFORE);
            ScrollView mainScroll = findViewById(R.id.main_scroll_view);
            if (mode == LOAD_NEWS_BEFORE) {
                mainScroll.scrollTo(0, maxScroll);
                if (v.isEmpty()) {
                    Snackbar.make(title, "No more news.", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                } else {
                    setCoverImagesViaGlide(v);
                }
            } else {
                mainScroll.scrollTo(0, 0);
                setCoverImagesViaGlide(newsList);
            }
        }

        @Override
        protected void onPreExecute() {
            if (mode != UPDATE_NEWS) {
                progressDialog = ProgressDialog.show(MainActivity.this, "Loading", (mode ==  LOAD_NEWS_BEFORE ? "Ναυτικός is loading more news...": "Ναυτικός is updating news..."));
            }
        }
    }

    private void setCoverImagesViaGlide (Vector<News> vector) {

        for (News news: vector) {
            if (!news.imageURLs.isEmpty()) {
                LinearLayout theLayout = news.layout;
                View lineView = new View (getContext());
                ViewGroup.LayoutParams lineParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1);
                lineView.setLayoutParams(lineParams);
                lineView.setBackgroundColor(Color.rgb(0xC0, 0xC0, 0xC0));
                theLayout.addView(lineView);

                ImageView img = new ImageView (getContext());
                RequestOptions myOptions = new RequestOptions().centerCrop().override(600, 450);
                Glide.with(getContext()).asBitmap().apply(myOptions).load (news.imageURLs.elementAt(0)).into(img);
                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);;
                params.setMargins(40, 18, 40, 18);
                img.setScaleType(ImageView.ScaleType.CENTER_CROP);
                img.setLayoutParams(params);
                theLayout.addView(img);
            }
        }
    }


    public void showBookmarks (View view) {
        Intent intent = new Intent(this, NewBookmarksHistoryActivity.class);
        intent.putExtra(EXTRA_BOOKMARKS, 0);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    public void showHistory (View view) {
        Intent intent = new Intent(this, NewBookmarksHistoryActivity.class);
        intent.putExtra(EXTRA_HISTORY, 0);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    public static Context getContext() {
        return context;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == CATEGORIES_REQUEST) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                boolean[] resultArray = data.getBooleanArrayExtra(EXTRA_PREFERRED_II);
                boolean refreshFlag = false;
                for (int j = HOME_FINANCE; j <= HOME_HEALTH; j += 1) {
                    if (isPreferredTopic[j] != resultArray[j]) {
                        refreshFlag = true;
                    }
                    isPreferredTopic[j] = resultArray[j];
                    navItems[j].setVisible(isPreferredTopic[j]);
                }
                /* store the config locally */
                storage.createFile(externPath + File.separator + "config.ini", SerializationUtils.serialize(isPreferredTopic));
                if (refreshFlag) { getNewsFromServer(); }
            }
        } else if (requestCode == LOGIN_REQUEST) {

            TextView emailText = findViewById(R.id.nav_header_email);
            if (MongoDB.current_user == null) {
                emailText.setText(R.string.please_log_in);
            } else {
                emailText.setText(MongoDB.current_user.userName);
            }

        }
    }

}

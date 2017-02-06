package com.solodroid.yourradioappsinglestation.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.solodroid.yourradioappsinglestation.Config;
import com.solodroid.yourradioappsinglestation.R;
import com.solodroid.yourradioappsinglestation.fragments.FragmentHome;
import com.solodroid.yourradioappsinglestation.notification.NotificationHandler;
import com.solodroid.yourradioappsinglestation.services.NotificationBuilder;
import com.solodroid.yourradioappsinglestation.utilities.Callscreen;

import co.mobiwise.library.radio.RadioListener;
import co.mobiwise.library.radio.RadioManager;

public class MainActivity extends AppCompatActivity implements RadioListener, NavigationView.OnNavigationItemSelectedListener {

    private final static String COLLAPSING_TOOLBAR_FRAGMENT_TAG = "collapsing_toolbar";
    private final static String SELECTED_TAG = "selected_index";
    private final static int TOOLBAR = 0;
    private static int selectedIndex;
    static final String TAG = "MainActivity";
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private RadioManager radioManager;
    private boolean runningOnOldConnection;
    private AdView adView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loadAdMobBannerAd();
        fireBaseAnalytics();

        radioManager = RadioManager.with(MainActivity.this);

        radioManager.registerListener(NotificationBuilder.getStaticNotificationUpdater(getBaseContext()));

        if (!radioManager.isConnected()) {
            radioManager.connect();
            runningOnOldConnection = false;
        } else {
            runningOnOldConnection = true;
        }

        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Animation fadeIn = new AlphaAnimation(0, 1);
                fadeIn.setInterpolator(new DecelerateInterpolator());

                drawerLayout.setVisibility(View.VISIBLE);
                drawerLayout.setAnimation(fadeIn);
            }
        }, 3000);

        if (savedInstanceState != null) {
            navigationView.getMenu().getItem(savedInstanceState.getInt(SELECTED_TAG)).setChecked(true);
            return;
        }

        selectedIndex = TOOLBAR;

        getSupportFragmentManager().beginTransaction().add(R.id.fragment_container,
                new FragmentHome(), COLLAPSING_TOOLBAR_FRAGMENT_TAG).commit();

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(SELECTED_TAG, selectedIndex);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {

            case R.id.menu_share:

                Intent sendInt = new Intent(Intent.ACTION_SEND);
                sendInt.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
                sendInt.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_text) + "\nhttps://play.google.com/store/apps/details?id=" + getPackageName());
                sendInt.setType("text/plain");
                startActivity(Intent.createChooser(sendInt, "Share"));

                return true;

            default:
                return super.onOptionsItemSelected(menuItem);
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {

        switch (menuItem.getItemId()) {
            case R.id.drawer_home:
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;

            case R.id.drawer_social:
                Intent social = new Intent(this, ActivitySocial.class);
                startActivity(social);

                return true;

            case R.id.drawer_rate:
                final String appName = getPackageName();
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appName)));
                } catch (android.content.ActivityNotFoundException anfe) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + appName)));
                }
                return true;

            case R.id.drawer_more:
                Callscreen.renderURL(this, getString(R.string.play_more_apps), "More");  //TODO Cambiar URL por la programacion de la radio y el titulo
                return true;

            case R.id.drawer_about:
                Intent about = new Intent(this, ActivityAbout.class);
                startActivity(about);
                return true;

            case R.id.drawer_programming:
                Callscreen.renderURL(this, "https://www.google.com", "Programming");  //TODO Cambiar URL por la programacion de la radio y l titulo
                return true;

            case R.id.drawer_about_us:
                Callscreen.renderURL(this,"https://www.google.com", "About us"); //TODO Cambiar URL por la info de la radio y el titulo
                return true;

            case R.id.drawer_exit:
                finish();
                return true;

        }

        return false;
    }


    public void setupNavigationDrawer(Toolbar toolbar) {
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }
        };
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            exitDialog();
        }
    }

    public void exitDialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setIcon(R.mipmap.ic_launcher);
        dialog.setTitle(R.string.app_name);
        dialog.setMessage(getResources().getString(R.string.message));
        dialog.setPositiveButton(getResources().getString(R.string.quit), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                radioManager.stopRadio();
                MainActivity.this.finish();
            }
        });

        dialog.setNegativeButton(getResources().getString(R.string.minimize), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                minimizeApp();
            }
        });

        dialog.setNeutralButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });
        dialog.show();
    }

    public void minimizeApp() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    public void onRadioLoading() {

    }

    @Override
    public void onRadioConnected() {

    }

    @Override
    public void onRadioStarted() {

    }

    @Override
    public void onRadioStopped() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                RadioManager.getService().cancelNotification();
            }
        });
    }

    @Override
    public void onMetaDataReceived(String s, String s2) {

    }

    @Override
    public void onAudioSessionId(int audioSessionId) {

    }

    @Override
    public void onError() {

    }

    private void loadAdMobBannerAd() {
        if (Config.ENABLE_ADMOB_ADS) {
            adView = (AdView) findViewById(R.id.adView);
            adView.loadAd(new AdRequest.Builder().build());
            adView.setAdListener(new AdListener() {

                @Override
                public void onAdClosed() {
                }

                @Override
                public void onAdFailedToLoad(int error) {
                    adView.setVisibility(View.GONE);
                }

                @Override
                public void onAdLeftApplication() {
                }

                @Override
                public void onAdOpened() {
                }

                @Override
                public void onAdLoaded() {
                    adView.setVisibility(View.VISIBLE);
                }
            });

        } else {
            Log.d("Log", "Admob Banner is Disabled");
        }
    }

    private void fireBaseAnalytics() {

        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "main_activity");
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "MainActivity");
        NotificationHandler.getFirebaseAnalytics().logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
        NotificationHandler.getFirebaseAnalytics().setAnalyticsCollectionEnabled(true);
        NotificationHandler.getFirebaseAnalytics().setMinimumSessionDuration(5000);
        NotificationHandler.getFirebaseAnalytics().setSessionTimeoutDuration(1000000);

    }

}

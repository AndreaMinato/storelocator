package it.kdevgroup.storelocator;

import android.support.v4.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import com.loopj.android.http.AsyncHttpResponseHandler;
import org.json.JSONException;
import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public interface StoresUpdater {
        void updateStores(ArrayList<Store> newStores);
    }

    private static final String TAG = "HomeActivity";
    private static final String SAVE = "onsaved";
    public static final String STORES_KEY_FOR_BUNDLE = "StoresListKeyForBundle";

    private PagerManager.PagerAdapter pagerAdapter;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private CouchbaseDB database;
    private boolean goSnack = true;
    private ArrayList<Store> stores;
    private FragmentManager fragManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        pagerAdapter = new PagerManager.PagerAdapter(getSupportFragmentManager(), this);

        fragManager = getSupportFragmentManager();

        database = new CouchbaseDB(getApplicationContext());

        if (savedInstanceState != null) {
            goSnack = savedInstanceState.getBoolean(SAVE);
            stores = savedInstanceState.getParcelableArrayList(STORES_KEY_FOR_BUNDLE);
        }
        if (stores == null)
            stores = new ArrayList<>();

        // Set up the ViewPager, attaching the adapter and setting up a listener for when the
        // user swipes between sections.
        viewPager = (ViewPager) findViewById(R.id.pager);
        assert viewPager != null;   //conferma che non è null
        viewPager.setAdapter(pagerAdapter);

        if (stores.size() == 0 && isNetworkAvailable()) {
            getStoresFromServer();
        }

        if (goSnack) {
            Snackbar.make(viewPager, "Benvenuto " + User.getInstance().getName(), Snackbar.LENGTH_LONG).show();
            goSnack = false;
        }

        tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        assert tabLayout != null;
        tabLayout.setupWithViewPager(viewPager);

        /**
         * da qua in poi drawer
         */
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        if (drawer != null) {
            drawer.addDrawerListener(toggle);
        }
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        if (navigationView != null) {
            navigationView.setNavigationItemSelectedListener(this);
        }
    }

    public ArrayList<Store> getStores(){
        return stores;
    }

    public void getStoresFromServer() {    //controlli già verificati prima della chiamata
        ApiManager.getInstance().getStores(User.getInstance().getSession(), new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                String[] error = null;
                String jsonBody = new String(responseBody);
                Log.i("onSuccess response:", jsonBody);

                // ottengo dei possibili errori
                try {
                    error = JsonParser.getInstance().getErrorInfoFromResponse(jsonBody);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                //se non ho trovato errori nella chiamata parso i negozi
                if (error == null) {
                    try {
                        stores = JsonParser.getInstance().parseStores(jsonBody);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if (stores != null && stores.size() > 0) {
                        //prendo il fragment corrente castandolo come interfaccia
                        //e gli dico di aver aggiornato i negozi e quindi di fare cose
                        StoresUpdater currentFragment = (StoresUpdater)fragManager.findFragmentByTag("android:switcher:" + R.id.pager + ":" + viewPager.getCurrentItem());
                        currentFragment.updateStores(stores);
                    }
                } else {
                    Snackbar.make(viewPager, error[0] + " " + error[1], Snackbar.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                if (responseBody != null) { //quando non c'è connessione non si connette al server e la risposta è null
                    String jsonBody = new String(responseBody);
                    Log.i("onFailure response:", jsonBody);
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
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

        if (id == R.id.nav_user) {
            Intent vInt=new Intent(this,DetailUser.class);
            startActivity(vInt);


        } else if (id == R.id.nav_preferiti) {

        } else if (id == R.id.nav_impostazioni) {

        } else if (id == R.id.nav_logout) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        assert drawer != null;
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    // Metodo che controlla la possibilità di accedere a internet
    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(HomeActivity.STORES_KEY_FOR_BUNDLE, stores);
        outState.putBoolean(SAVE, goSnack);
    }
}

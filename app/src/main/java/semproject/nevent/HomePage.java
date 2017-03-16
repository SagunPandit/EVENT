package semproject.nevent;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static semproject.nevent.ShowEvents.la;
import static semproject.nevent.ShowEvents.ln;

public class HomePage extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,ConnectivityReceiver.ConnectivityReceiverListener,SearchView.OnQueryTextListener {
    final String STRING_TAG= "HomePage";
    NavigationView navigationView=null;
    Toolbar toolbar=null;
    String username;
    Context context;
    int id;
    static EventRecyclerView staticeventRecyclerView = new EventRecyclerView();
    static EventRecyclerView.AllItemAdapter staticadapter=new EventRecyclerView.AllItemAdapter();
    static ShowEvents showEvents;


    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);


            Intent intent = getIntent();
            username = intent.getStringExtra("username");
            id=intent.getIntExtra("id",3);
            Button userbutton=(Button)findViewById(R.id.user_button);
            userbutton.setText(username);

            if (id==1)
            {
                Recent recent = new Recent();
                Bundle bundle = new Bundle();
                bundle.putString("username", username);
                bundle.putInt("id",id);
                android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container, recent);
                recent.setArguments(bundle);
                fragmentTransaction.commit();
            }
            else if(id==2)
            {
                NearByList nearByList = new NearByList();
                Bundle bundle = new Bundle();
                bundle.putString("username", username);
                android.support.v4.app.FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container, nearByList);
                nearByList.setArguments(bundle);
                fragmentTransaction.commit();
            }
            toolbar = (Toolbar) findViewById(R.id.toolbar);
             setSupportActionBar(toolbar);
             getSupportActionBar().setDisplayShowTitleEnabled(false);
             toolbar.setLogo(R.drawable.logo);



            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                    this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawer.setDrawerListener(toggle);
            toggle.syncState();

            navigationView = (NavigationView) findViewById(R.id.nav_view);
            navigationView.setNavigationItemSelectedListener(this);




    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed(); //use property of super class - Appcompat actvity
        }
    }

    @Override //http://stackoverflow.com/questions/31231609/creating-a-button-in-android-toolbar
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.homepage, menu);
        MenuItem menuItem=menu.findItem(R.id.action_search);
        SearchView searchView= (SearchView) MenuItemCompat.getActionView(menuItem);
        searchView.setOnQueryTextListener(this);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_location:
                showEvents=new ShowEvents();
                Intent intent=new Intent(this, ShowEvents.class);
                intent.putExtra("username",username);
                finish();
                startActivity(intent);

        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        Button all = (Button) findViewById(R.id.button4);


        Button user = (Button) findViewById(R.id.user_button);

        Button trend = (Button) findViewById(R.id.button5);

        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Bundle bundle = new Bundle();
        bundle.putString("username", username);
        if (id == R.id.nav_sports) {

            trend.setBackgroundResource(R.drawable.cdefault);
            all.setBackgroundResource(R.drawable.cdefault);
            user.setBackgroundResource(R.drawable.cdefault);


            Sports sports=new Sports();
            android.support.v4.app.FragmentTransaction fragmentTransaction=getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, sports);
            sports.setArguments(bundle);
            fragmentTransaction.commit();

            // Handle the camera action
        } else if (id == R.id.nav_parties) {
            trend.setBackgroundResource(R.drawable.cdefault);
            all.setBackgroundResource(R.drawable.cdefault);
            user.setBackgroundResource(R.drawable.cdefault);
            Parties parties=new Parties();
            android.support.v4.app.FragmentTransaction fragmentTransaction=getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, parties);
            parties.setArguments(bundle);
            fragmentTransaction.commit();


        } else if (id == R.id.nav_conferences) {
            trend.setBackgroundResource(R.drawable.cdefault);
            all.setBackgroundResource(R.drawable.cdefault);
            user.setBackgroundResource(R.drawable.cdefault);
            Conference conference=new Conference();
            android.support.v4.app.FragmentTransaction fragmentTransaction=getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, conference);
            conference.setArguments(bundle);
            fragmentTransaction.commit();


        } else if (id == R.id.nav_donations) {
            trend.setBackgroundResource(R.drawable.cdefault);
            all.setBackgroundResource(R.drawable.cdefault);
            user.setBackgroundResource(R.drawable.cdefault);
            Donations donations=new Donations();
            android.support.v4.app.FragmentTransaction fragmentTransaction=getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, donations);
            donations.setArguments(bundle);
            fragmentTransaction.commit();


        }

        else if (id == R.id.nav_others) {
            trend.setBackgroundResource(R.drawable.cdefault);
            all.setBackgroundResource(R.drawable.cdefault);
            user.setBackgroundResource(R.drawable.cdefault);
            Others others=new Others();
            android.support.v4.app.FragmentTransaction fragmentTransaction=getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, others);
            others.setArguments(bundle);
            fragmentTransaction.commit();


        }
        else if (id == R.id.nav_business) {
            trend.setBackgroundResource(R.drawable.cdefault);
            all.setBackgroundResource(R.drawable.cdefault);
            user.setBackgroundResource(R.drawable.cdefault);
            Business business=new Business();
            android.support.v4.app.FragmentTransaction fragmentTransaction=getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, business);
            business.setArguments(bundle);
            fragmentTransaction.commit();


        }
        else if (id == R.id.nav_concert) {
            trend.setBackgroundResource(R.drawable.cdefault);
            all.setBackgroundResource(R.drawable.cdefault);
            user.setBackgroundResource(R.drawable.cdefault);
            Concert concert=new Concert();
            android.support.v4.app.FragmentTransaction fragmentTransaction=getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, concert);
            concert.setArguments(bundle);
            fragmentTransaction.commit();


        }

        else if (id == R.id.nav_educational) {
            trend.setBackgroundResource(R.drawable.cdefault);
            all.setBackgroundResource(R.drawable.cdefault);
            user.setBackgroundResource(R.drawable.cdefault);
            Educational educational=new Educational();
            android.support.v4.app.FragmentTransaction fragmentTransaction=getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, educational);
            educational.setArguments(bundle);
            fragmentTransaction.commit();


        }
        else if (id == R.id.nav_exhibition) {
            trend.setBackgroundResource(R.drawable.cdefault);
            all.setBackgroundResource(R.drawable.cdefault);
            user.setBackgroundResource(R.drawable.cdefault);
            Exhibition exhibition=new Exhibition();
            android.support.v4.app.FragmentTransaction fragmentTransaction=getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, exhibition);
            exhibition.setArguments(bundle);
            fragmentTransaction.commit();


        }
        else if (id == R.id.nav_gaming) {
            trend.setBackgroundResource(R.drawable.cdefault);
            all.setBackgroundResource(R.drawable.cdefault);
            user.setBackgroundResource(R.drawable.cdefault);
            Gaming gaming=new Gaming();
            android.support.v4.app.FragmentTransaction fragmentTransaction=getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, gaming);
            gaming.setArguments(bundle);
            fragmentTransaction.commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private boolean checkConnection(Context context) {
        Log.e(STRING_TAG,"checkConnection");
        boolean isConnected = ConnectivityReceiver.isConnected(context);
        if(!isConnected){
            Intent intent= new Intent(this,InternetConnection.class);
            finish();
            startActivity(intent);
        }
        return isConnected;
    }

    public void trending(View view) {
        Button all = (Button) findViewById(R.id.button4);
        all.setBackgroundResource(R.drawable.cdefault);

        Button user = (Button) findViewById(R.id.user_button);
        user.setBackgroundResource(R.drawable.cdefault);

        Button trend = (Button) findViewById(R.id.button5);
        trend.setBackgroundResource(R.drawable.shape3);
        if(checkConnection(this)){
            Bundle bundle = new Bundle();
            bundle.putString("username", username);
            Trending trending=new Trending();
            android.support.v4.app.FragmentTransaction fragmentTransaction=getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, trending);
            trending.setArguments(bundle);
            fragmentTransaction.commit();
        }
    }

    public void recent(View view) {

        Button user = (Button) findViewById(R.id.user_button);
        user.setBackgroundResource(R.drawable.cdefault);

        Button trend = (Button) findViewById(R.id.button5);
        trend.setBackgroundResource(R.drawable.cdefault);


        Button all = (Button) findViewById(R.id.button4);
        all.setBackgroundResource(R.drawable.shape3);
        if(checkConnection(this)){
            Recent recent=new Recent();
            Bundle bundle = new Bundle();
            bundle.putString("username", username);
            android.support.v4.app.FragmentTransaction fragmentTransaction=getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, recent);
            recent.setArguments(bundle);
            fragmentTransaction.commit();
        }
    }

    public void addevents(View view) {
        if(checkConnection(this)){
            Intent i=new Intent(this, Upload.class);
            i.putExtra("username",username);
            finish();
            startActivity(i);
        }


    }

    public void userdetails(View view){



        Button all = (Button) findViewById(R.id.button4);
        all.setBackgroundResource(R.drawable.cdefault);

        Button trend = (Button) findViewById(R.id.button5);
        trend.setBackgroundResource(R.drawable.cdefault);

        Button user = (Button) findViewById(R.id.user_button);
        user.setBackgroundResource(R.drawable.shape3);

        if(checkConnection(this)){
            /*Intent intent = new Intent(this, UserDetails.class);
            intent.putExtra("username",username);
            startActivity(intent);
            finish();*/

            Bundle bundle = new Bundle();
            bundle.putString("username", username);
            Userdetail userDetails=new Userdetail();
            android.support.v4.app.FragmentTransaction fragmentTransaction=getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, userDetails);
            userDetails.setArguments(bundle);
            fragmentTransaction.commit();
        }
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        if(isConnected){
            Intent intent= new Intent(this,MainActivity.class);
            finish();
            startActivity(intent);
        }
        else{
            Intent intent= new Intent(this,InternetConnection.class);
            finish();
            startActivity(intent);

        }
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String searchEvent) {
        List<EventRecyclerView.Item> extractedItem=staticeventRecyclerView.getItem();
        final List<EventRecyclerView.Item> searchItem=new ArrayList<>();
        if(extractedItem.isEmpty())
            Log.e(STRING_TAG,"empty");
        else
            Log.e(STRING_TAG,"is not empty");
        for(EventRecyclerView.Item indevent: extractedItem){
            String eventname=indevent.eventLabel;
            Log.i(STRING_TAG+"Searching",searchEvent);
            if(eventname.contains(searchEvent)){
                searchItem.add(indevent);
                Log.i(STRING_TAG+"SearchProgress",searchEvent);
            }
        }
        staticadapter.setFilter(searchItem);
        return true;
    }
}

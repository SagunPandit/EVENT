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
import java.util.List;

public class HomePage extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    final String STRING_TAG= "HomePage";
    NavigationView navigationView=null;
    Toolbar toolbar=null;
    String username;
    Context context;
    private RecyclerView mRecyclerView;
    EventRecyclerView eventRecyclerView = new EventRecyclerView();
    List<String>eventList=new ArrayList<>();
    List<String>eventLocation=new ArrayList<>();
    List<String>eventDate=new ArrayList<>();
    List<String>eventCategory=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        Intent intent = getIntent();
        username = intent.getStringExtra("username");
        Button userbutton=(Button)findViewById(R.id.user_button);
        userbutton.setText(username);


        RecyclerView.LayoutManager mLayoutManager;
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        if (mRecyclerView != null) {
            mRecyclerView.setHasFixedSize(true);
        }
        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        listenerFunction(username);


        // Set the fragments
        Trending trending=new Trending();
        android.support.v4.app.FragmentTransaction fragmentTransaction=getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, trending);
        fragmentTransaction.commit();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/

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
        return true;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override

    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_sports) {
            Sports sports=new Sports();
            android.support.v4.app.FragmentTransaction fragmentTransaction=getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, sports);
            fragmentTransaction.commit();

            // Handle the camera action
        } else if (id == R.id.nav_parties) {
            Parties parties=new Parties();
            android.support.v4.app.FragmentTransaction fragmentTransaction=getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, parties);
            fragmentTransaction.commit();


        } else if (id == R.id.nav_conferences) {
            Conference conference=new Conference();
            android.support.v4.app.FragmentTransaction fragmentTransaction=getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, conference);
            fragmentTransaction.commit();


        } else if (id == R.id.nav_donations) {
            Donations donations=new Donations();
            android.support.v4.app.FragmentTransaction fragmentTransaction=getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, donations);
            fragmentTransaction.commit();


        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void gopage(View view) {
        Intent i=new Intent(this, HomePage.class);
        i.putExtra("username",username);
        startActivity(i);
        finish();
    }

    public void addevents(View view) {
        Intent i=new Intent(this, Upload.class);
        i.putExtra("username",username);
        startActivity(i);

    }

    public void userdetails(View view){
        Intent intent = new Intent(this, UserDetails.class);
        intent.putExtra("username",username);
        startActivity(intent);
        finish();
    }

    public void retreiveFromDatabase(EventRecyclerView eventRecyclerView,RecyclerView mRecyclerView,Context context){
        Log.e(STRING_TAG,"database");
        for (int i=0;i < eventList.size();i++)
        {
            Log.i("Value of element "+i,eventList.get(i));
            eventRecyclerView.initializeData(eventList.get(i),eventCategory.get(i),eventLocation.get(i),eventDate.get(i),username,context);
            RecyclerView.Adapter mAdapter = new EventRecyclerView.ItemAdapter(context, eventRecyclerView.getItem());
            mRecyclerView.setAdapter(mAdapter);
        }

    }


    public void listenerFunction(String username){
        Log.e(STRING_TAG,"insideListiner");
        Response.Listener<String> responseListener= new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    Log.e(STRING_TAG,"try");
                    JSONObject jsonObject=new JSONObject(response);
                    boolean success = jsonObject.getBoolean("success");
                    if(success){
                        Log.e(STRING_TAG,"insideSuccess");
                        JSONArray jsonArray = jsonObject.getJSONArray("event_name");
                        JSONArray jsonArray2 = jsonObject.getJSONArray("location_name");
                        JSONArray jsonArray3 = jsonObject.getJSONArray("event_date");
                        JSONArray jsonArray4 = jsonObject.getJSONArray("event_category");
                        if (jsonArray != null) {
                            int len = jsonArray.length();
                            Log.e(STRING_TAG,Integer.toString(len));
                            //for eventname
                            for (int i=0;i<len;i++){
                                eventList.add(jsonArray.get(i).toString());
                            }
                            //for eventlocation
                            for (int i=0;i<len;i++){
                                eventLocation.add(jsonArray2.get(i).toString());
                            }
                            //for eventdate
                            for (int i=0;i<len;i++){
                                eventDate.add(jsonArray3.get(i).toString());
                            }
                            //for eventcategory
                            for (int i=0;i<len;i++){
                                eventCategory.add(jsonArray4.get(i).toString());
                            }

                            retreiveFromDatabase(eventRecyclerView, mRecyclerView, HomePage.this);
                        }
                        else
                            Log.e(STRING_TAG,"insideNull");

                    }
                    else {
                        AlertDialog.Builder builder= new AlertDialog.Builder(HomePage.this);
                        builder.setMessage("Connection Failed")
                                .setNegativeButton("Retry",null)
                                .create()
                                .show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        RecyclerRequest recyclerRequest=new RecyclerRequest(username, responseListener);
        RequestQueue queue = Volley.newRequestQueue(HomePage.this);
        queue.add(recyclerRequest);
    }

}

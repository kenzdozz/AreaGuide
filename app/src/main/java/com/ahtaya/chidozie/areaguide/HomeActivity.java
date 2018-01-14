package com.ahtaya.chidozie.areaguide;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //setting toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ArrayList<String> typesDefault = new ArrayList<>(); //DefaultTypes of nearby places to get.
        ArrayList<String> types = new ArrayList<>();        //Types of nearby places to get.
        typesDefault.add("restaurant");
        typesDefault.add("bank");
        typesDefault.add("hospital");

        //Creating SharedPreferences
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        //Getting Types from SharedPreferences
        Set<String> type = sharedPreferences.getStringSet(getString(R.string.pref_types_key), null);
        //Checking if any types SharedPreferences exists
        if (type != null) {
            types.addAll(Arrays.asList(type.toArray(new String[]{})));
        } else {
            types = typesDefault;
        }

        //Create viewPager for the Fragments
        ViewPager pager = findViewById(R.id.view_pager);
        pager.setAdapter(new TypeFragmentAdapter(getSupportFragmentManager(), types));

        //Create TabLayout for Fragment titles
        TabLayout tabLayout = findViewById(R.id.my_tab);
        tabLayout.setupWithViewPager(pager);
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        //About
        findViewById(R.id.about_ico).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, AboutActivity.class);
                startActivity(intent);
            }
        });
    }

    /**
     * Inflating menu
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    //Disabling menu for Activing in favor of fragments
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle menu click
        switch (item.getItemId()) {
            case R.id.action_settings:
                Intent settingsIntent = new Intent(this, SettingsActivity.class);
                startActivity(settingsIntent);
                return false;
            default:
                return false;
        }
    }
}

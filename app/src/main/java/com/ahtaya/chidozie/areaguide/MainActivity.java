package com.ahtaya.chidozie.areaguide;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class MainActivity extends AppCompatActivity {

    TextView searchText;            //Text from Search TextEdit
    String placeText = "";     //String for Search TextEdit
    Gson gson;                      //Gson to parse JSON result
    StringRequest stringRequest;    //Volley String request
    private ProgressDialog dialog;  //Dialog to wait for request

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        searchText = findViewById(R.id.place_edit);
        gson = new GsonBuilder().setLenient().create();
        dialog = new ProgressDialog(this);

        //setting toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Hide Keyboard when SearchText loose focus
        searchText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    hideKeyboard(v);
                }
            }
        });

        //About
        findViewById(R.id.about_ico).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AboutActivity.class);
                startActivity(intent);
            }
        });

        //Search for Location typed on EditText when search button is clicked
        findViewById(R.id.search_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                placeText = searchText.getText().toString();
                if (!placeText.isEmpty()){
                    //start progress dialog
                    dialog.setMessage(getString(R.string.loading));
                    dialog.show();
                    GetLocation(placeText);
                }else {
                    Toast.makeText(MainActivity.this, R.string.enter_area, Toast.LENGTH_SHORT).show();
                }
            }
        });

        //Search for Current location when useMyLocation button is click
        findViewById(R.id.get_location).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Use GPSTracker to get current location
                GPSTracker gpsTracker = new GPSTracker(MainActivity.this);
                    String lat = Double.toString(gpsTracker.getLatitude());
                    String lng = Double.toString(gpsTracker.getLongitude());
                if (!lat.equals("0.0")) {
                    //If successful, create intent and start Activity
                    Intent i = new Intent(getBaseContext(), HomeActivity.class);
                    i.putExtra("lat", lat);
                    i.putExtra("lng", lng);
                    Log.v("Here", lat + lng);
                    startActivity(i);
                }else {
                    Toast.makeText(getBaseContext(), R.string.curr_loc_error, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * Remove dialog on press back button
     */
    @Override
    protected void onPostResume() {
        super.onPostResume();
        if (dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    /**
     * Getting Location of typed in search text
     */
    private void GetLocation(String mplaceText){

        //Google place Api
        String url = "https://maps.googleapis.com/maps/api/place/textsearch/json?query=" +
                mplaceText+"&key=AIzaSyC_2SXuauBwziVEyzXs07tStDXH81wsvM8";

        //Response Listener for Volley StringRequest
        Response.Listener<String> stringListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                PlaceData placeData = gson.fromJson(response, PlaceData.class);

                //Check if results exist
                if (placeData.status.equals("OK")) {
                    //parse results with PlaceData
                    for (PlaceData.Results results : placeData.results) {
                        PlaceData.Geometry geometry = results.geometry;
                        PlaceData.Location location = geometry.location;

                        String lat = location.lat;
                        String lng = location.lng;
                        //Create intent with extra data passed to start next Activity
                        Intent i = new Intent(getBaseContext(), HomeActivity.class);
                        i.putExtra("lat", lat);
                        i.putExtra("lng", lng);
                        startActivity(i);
                    }
                }else {
                    //Display results status on a toast and dismiss dialog
                    Toast.makeText(getBaseContext(), placeData.status, Toast.LENGTH_LONG).show();
                    if (dialog.isShowing()) {
                        dialog.dismiss();
                    }
                }
            }
        };

        //Create Response Error Listener for Volley StringRequest
        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Dismiss progress dialog
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
                //Show error on toast
                Toast.makeText(getBaseContext(), error.toString(), Toast.LENGTH_SHORT).show();
            }
        };

        //Create Volley request
        stringRequest = new StringRequest(Request.Method.GET, url, stringListener, errorListener);
        Volley.newRequestQueue(getBaseContext()).add(stringRequest);
    }

    /**
     * method to hide keyboard
     */
    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager =(InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (inputMethodManager != null) {
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}

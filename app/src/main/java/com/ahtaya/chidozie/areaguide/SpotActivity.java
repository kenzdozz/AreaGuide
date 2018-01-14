package com.ahtaya.chidozie.areaguide;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.util.LruCache;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class SpotActivity extends AppCompatActivity {

    //lat, lng, name, vicinity, rating, imageReference, openStatus from intent
    String lat, lng, name, vicinity, rating, imageReference, openStatus;
    TextView spotName, spotVicinity, openState, dDistance; //Used to set TextViews
    RatingBar spotRating; //To set rating bar
    NetworkImageView spotImage; //To set image
    String originLat, originLng; //current lat and lng
    String distance_url, distanceTo; //url to get distance

    Gson gson;                      //Gson to parse JSON
    StringRequest stringRequest;    //Volley String Request
    Response.Listener<String> stringRequestResponse;    //stringRequest Response
    Response.ErrorListener stringRequestError = null;   //stringRequest Error

    boolean isGPSEnabled, isNetworkEnabled = false; //Flag for GPS and Network
    LocationManager locationManager;            //Location manager

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spot);

        //setting toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Configure back arrow
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        //About
        findViewById(R.id.about_ico).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SpotActivity.this, AboutActivity.class);
                startActivity(intent);
            }
        });

        //get extras from intent, get @lat and @lng
        Intent i = getIntent();
        lat = i.getStringExtra("lat");
        lng = i.getStringExtra("lng");
        name = i.getStringExtra("name");
        vicinity = i.getStringExtra("vicinity");
        rating = i.getStringExtra("rating");
        imageReference = i.getStringExtra("imageReference");
        openStatus = i.getStringExtra("openStatus");
        if (openStatus.equals("true")) {
            openStatus = getString(R.string.open_opened);
        } else if (openStatus.equals("false")) {
            openStatus = getString(R.string.open_closed);
        }

        //Finding and setting TextViews and ratingBar
        spotName = findViewById(R.id.spot_name);
        spotName.setText(name);
        spotVicinity = findViewById(R.id.spot_vicinity);
        spotVicinity.setText(vicinity);
        spotRating = findViewById(R.id.rating_bar);
        spotRating.setRating(Float.parseFloat(rating));
        openState = findViewById(R.id.open_now);
        openState.setText(openStatus);
        spotImage = findViewById(R.id.spot_image);
        dDistance = findViewById(R.id.distance);

        spotImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 300, getResources().getDisplayMetrics());
                if (spotImage.getHeight() == height) {
                    spotImage.setLayoutParams(new LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                }else {
                    spotImage.setLayoutParams(new LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT, height));
                }
            }
        });

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        if (locationManager != null) {
            // getting GPS status
            isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            // getting network status
            isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        }

        //check if GPS is turned on
        if (isGPSEnabled || isNetworkEnabled) {
            //if success, check if permission is set.
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                //if success, Get location using GPSTracker and set origin.
                GPSTracker gpsTracker = new GPSTracker(SpotActivity.this);
                originLat = Double.toString(gpsTracker.getLatitude());
                originLng = Double.toString(gpsTracker.getLongitude());
            }
        }

        //API to get photo
        String photo_url = (imageReference.equals("EMPTY")) ? null :
                "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference=" +
                imageReference + "&key=AIzaSyC_2SXuauBwziVEyzXs07tStDXH81wsvM8";

        //creating Volley RequestQueue and  ImageLoader to load image
        RequestQueue mRequestQueue = Volley.newRequestQueue(this);
        ImageLoader mImageLoader = new ImageLoader(mRequestQueue, new ImageLoader.ImageCache() {
            private final LruCache<String, Bitmap> mCache = new LruCache<>(10);
            public void putBitmap(String url, Bitmap bitmap) {
                mCache.put(url, bitmap);
            }
            public Bitmap getBitmap(String url) {
                return mCache.get(url);
            }
        });
        //Set defaultImage and Loaded image
        spotImage.setImageUrl(photo_url, mImageLoader);
        spotImage.setDefaultImageResId(R.drawable.noimage);

        //API to get #restaurants around the @lat and @lng within the @radius
        distance_url = "https://maps.googleapis.com/maps/api/distancematrix/json?units=metric&origins=" +
                originLat + "," + originLng + "&destinations=" + lat + "," + lng + "&key=AIzaSyBWFD_1LQy3TnGc0U2hDQCXic0yegK8qMA";

        //Setting default DistanceTo
        distanceTo = getString(R.string.unknown_distance);

        //Creates a Response Listener for the Volley StringRequest
        stringRequestResponse = new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                //creates new GsonBuilder as gson
                gson = new GsonBuilder().setLenient().create();
                //parse String response of String request to the DistanceData class for mapping
                DistanceData distanceData = gson.fromJson(response, DistanceData.class);

                //if status = OK, there is result, else zero result or error
                if (distanceData.status.equals("OK")) {
                    // iterates over placeData results to create PlaceSpot objects
                    DistanceData.Rows rows = distanceData.rows[0];
                    DistanceData.Elements elements = rows.elements[0];
                    if (elements.status.equals("OK")) {
                        DistanceData.Distance distance = elements.distance;
                        DistanceData.Duration duration = elements.duration;
                        distanceTo = distance.text + "/" + duration.text + getString(R.string.away);
                    }else {
                        Toast.makeText(getBaseContext(), R.string.distance_error, Toast.LENGTH_SHORT).show();
                    }
                    //set DistanceTo, loaded value or default
                    dDistance.setText(distanceTo);
                }else {
                    Toast.makeText(getBaseContext(), R.string.distance_error, Toast.LENGTH_SHORT).show();
                }
            }
        };

        //Creates a Error Listener for the Volley StringRequest
        stringRequestError = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Toast error
                Toast.makeText(getBaseContext(), error.toString(), Toast.LENGTH_SHORT).show();
            }
        };

        //Create the Volley StringRequest
        stringRequest = new StringRequest(Request.Method.GET, distance_url, stringRequestResponse, stringRequestError);

        //add the stringRequest to Volley RequestQueue to start request
        Volley.newRequestQueue(this).add(stringRequest);

        //Reloading distance onclick of the distance text
        findViewById(R.id.distance).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //sets a loading text.
                dDistance.setText(R.string.loading);
                //get current loaction with GPSTracker
                GPSTracker gpsTracker = new GPSTracker(SpotActivity.this);
                originLat = Double.toString(gpsTracker.getLatitude());
                originLng = Double.toString(gpsTracker.getLongitude());
                //resets distance url
                distance_url = "https://maps.googleapis.com/maps/api/distancematrix/json?units=metric&origins=" +
                        originLat + "," + originLng + "&destinations=" + lat + "," + lng + "&key=AIzaSyBWFD_1LQy3TnGc0U2hDQCXic0yegK8qMA";
                //Create the Volley StringRequest
                stringRequest = new StringRequest(Request.Method.GET, distance_url, stringRequestResponse, stringRequestError);
                Volley.newRequestQueue(getBaseContext()).add(stringRequest);
            }
        });

        //opens Google map to locate the spot
        findViewById(R.id.open_map).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri gmmIntentUri = Uri.parse("geo:" + lat + "," + lng + "?q=" + name);
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                if (mapIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(mapIntent);
                }
            }
        });

    }

    /**
     * Prevents up button from recreating parent
     */
    @Nullable
    @Override
    public Intent getSupportParentActivityIntent() {
        finish();
        return null;
    }

    /**
     * Used in Gson to map results of JSON API
     */
    private class DistanceData {

        //creates rows of elements and status
        Rows[] rows;
        String status;

        class Rows {
            Elements[] elements;
        }

        class Elements {
            Distance distance;
            Duration duration;
            String status;
        }

        class Distance {
            String text;
        }

        class Duration {
            String text;
        }
    }
}

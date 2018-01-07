package com.ahtaya.chidozie.areaguide;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;

public class TypeFragment extends Fragment {

    /**
     * @param placeSpots creates arrayList of PlaceSpot object of PlaceSpot class.
     * @param recyclerView gets a recyclerView object that gets the recycler view from
     * activity_home xml with id 'recycle'.
     * @param spotAdapter creates a spotAdapter object to adapt placeSpots to recyclerView
     * @param shimmerAdapter creates a shimmerAdapter object to adapt shimmer to recyclerView
     * @param lat, lng stores the latitude and longitude passed from the MainActivity intent
     * @param radius stores the radius of place search
     * @param mtype stores the search data from arguments passed to the Fragment
     * @param linearLayout creates a linearLayout object that gets linearLayout view from
     * activity_home xml with id 'lin'.
     * @param gson creates a Gson object to parse the JSON data from volley StringRequest result
     */
    ArrayList<PlaceSpot> placeSpots;
    RecyclerView recyclerView;
    SpotAdapter spotAdapter;
    ShimmerAdapter shimmerAdapter;
    String lat, lng, radius, rankby, mtype;
    LinearLayout linearLayout;
    private Gson gson;

    //Creates Volley StringRequest, Response.Listener and Response.ErrorListener
    StringRequest stringRequest;
    Response.Listener<String> stringRequestResponse;
    Response.ErrorListener stringRequestError = null;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_type, container, false);

        //Activating menu for fragment
        setHasOptionsMenu(true);

        //Initialize PlaceSpot
        placeSpots = new ArrayList<>();
        //Checks if spotAdapter has been created to avoid duplicate list
        if (spotAdapter == null) {
            spotAdapter = new SpotAdapter(placeSpots);
        }
        //finding view to initialize and initializing shimmer
        recyclerView = view.findViewById(R.id.recycle);
        linearLayout = getActivity().findViewById(R.id.lin);
        shimmerAdapter = new ShimmerAdapter();

        if (!getArguments().isEmpty())
            //noinspection ConstantConditions
            mtype = getArguments().get("type").toString();

        // set recyclerView LayoutManager, ItemAnimator and Adapter
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        //setting recyclerView to shimmerAdapter and displaying awaiting spotAdapter to load objects
        recyclerView.setAdapter(shimmerAdapter);
        recyclerView.setVisibility(View.VISIBLE);

        //creates new GsonBuilder as gson
        gson = new GsonBuilder().setLenient().create();

        //get extras from intent, get @lat and @lng
        Intent i = getActivity().getIntent();
        lat = i.getStringExtra("lat");
        lng = i.getStringExtra("lng");

        //Initialize radius and rankby
        radius = "50000";
        rankby = "distance";

        //API to get #restaurants around the @lat and @lng within the @radius
        String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=" +
                lat + "," + lng + "&rankby=" + rankby + "&type=" + mtype + "&key=AIzaSyC_2SXuauBwziVEyzXs07tStDXH81wsvM8";

        //Creates a Response Listener for the Volley StringRequest
        stringRequestResponse = new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                //parse String response of String request to the PlaceData class for mapping
                PlaceData placeData = gson.fromJson(response, PlaceData.class);

                //if status = OK, there is result, else zero result or error
                if (placeData.status.equals("OK")) {

                    //captures photo_reference for PlaceSpot object
                    String photo_reference, open_now;

                    // iterates over placeData results to create PlaceSpot objects
                    for (PlaceData.Results results : placeData.results) {
                        PlaceData.Geometry geometry = results.geometry;
                        PlaceData.Location location = geometry.location;
                        PlaceData.OpeningHours opening_hours = results.opening_hours;
                        if (results.photos != null) {
                            PlaceData.Photos photos = results.photos[0];
                            photo_reference = photos.photo_reference;
                        } else {
                            photo_reference = "EMPTY";
                        }
                        if (opening_hours == null || opening_hours.open_now == null) {
                            open_now = getString(R.string.unknown);
                        } else {
                            open_now = opening_hours.open_now;
                        }
                        //creating placeSpots objects and notifying adapter for changes
                        placeSpots.add(new PlaceSpot(results.name, results.rating, open_now, results.vicinity, location.lat, location.lng, photo_reference));
                        spotAdapter.notifyDataSetChanged();

                        //resetting the recyclerView to spotAdapter
                        recyclerView.setAdapter(spotAdapter);
                    }
                } else {
                    //if no results, alert user to close activity
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setCancelable(false);
                    builder.setMessage(placeData.status);
                    builder.setPositiveButton(R.string.dial_close, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            Intent i = new Intent(getActivity(), MainActivity.class);
                            startActivity(i);
                            dialog.cancel();
                        }
                    }).show();
                }
            }
        };
        //Creates a Error Listener for the Volley StringRequest
        stringRequestError = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Connection error, alert user to close or retry;
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setCancelable(false);
                builder.setMessage(R.string.no_conn);
                builder.setPositiveButton(R.string.retry, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        Retry();
                    }
                });
                builder.setNegativeButton(R.string.close, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        Intent i = new Intent(getActivity(), MainActivity.class);
                        startActivity(i);
                        dialog.cancel();
                    }
                });
                builder.show();
            }
        };

        //Create the Volley StringRequest
        stringRequest = new StringRequest(Request.Method.GET, url, stringRequestResponse, stringRequestError);
        //add the stringRequest to Volley RequestQueue to start request
        Volley.newRequestQueue(getActivity()).add(stringRequest);

        return view;
    }

    //Disabling active menu
    @Override
    public void onPrepareOptionsMenu(Menu menu) {

        if (rankby.equals("distance")) {
            menu.getItem(0).setEnabled(false);
            menu.getItem(1).setEnabled(true);
        }else {
            menu.getItem(1).setEnabled(false);
            menu.getItem(0).setEnabled(true);
        }
    }

    //Handling menu click
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.distance:
                ChangeRankby("distance");
                return true;
            case R.id.prominence:
                ChangeRankby("prominence");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Method to retry on no connection
     */
    public void Retry() {
        //add the stringRequest to Volley RequestQueue to start request
        Volley.newRequestQueue(getActivity()).add(stringRequest);
    }

    /**
     * Changing rankby on menu click
     */
    public void ChangeRankby(String rank){
        if (rank.equals("prominence"))
            rankby = rank + "&radius=50000";
        else
            rankby = rank;
        //setting recyclerView to shimmerAdapter and displaying awaiting spotAdapter to load objects
        recyclerView.setAdapter(shimmerAdapter);
        placeSpots.clear();
        //API to get #restaurants around the @lat and @lng within the @radius
        String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=" +
                lat + "," + lng + "&rankby=" + rankby + "&type=" + mtype + "&key=AIzaSyC_2SXuauBwziVEyzXs07tStDXH81wsvM8";
        //Create the Volley StringRequest
        stringRequest = new StringRequest(Request.Method.GET, url, stringRequestResponse, stringRequestError);
        //add the stringRequest to Volley RequestQueue to start request
        Volley.newRequestQueue(getActivity()).add(stringRequest);
    }
}

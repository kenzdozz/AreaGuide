package com.ahtaya.chidozie.areaguide;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v4.util.LruCache;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;

import java.util.List;

/**
 * Created by CHIDOZIE on 12/30/2017.
 * The SpotAdapter class adapts the PlaceSpot object to the RecyclerView
 * It must override three methods: onCreateViewHolder, onBindViewHolder and getItemCount
 */

public class SpotAdapter extends RecyclerView.Adapter<SpotAdapter.SpotViewHolder> {

    /**
     * @param mplaceSpots creates a list of PlaceSpot objects
     * @param mImageLoader creates an imageLoader thet will load image with mRequestQueue
     */
    private List<PlaceSpot> mplaceSpots;
    private ImageLoader mImageLoader;

    /**
     * Class of RecyclerView.Adapter<> to adapt views to the holder
     */
    class SpotViewHolder extends RecyclerView.ViewHolder {

        /**
         * @param spotName gets the spot_name textView from layout file inflating the recyclerView
         * @param sportVicinity gets the spot_vicinity textView from layout file inflating the recyclerView
         * @param spotImage gets the spot_image NetworkImageView from layout file inflating the recyclerView
         */
        TextView spotName, sportVicinity;
        NetworkImageView spotImage;
        RatingBar spotRating;

        /**
         * the layout file inflating the RecyclerView is passed to SpotViewHolder as
         * @param itemView and the child views are found by id.
         */
        SpotViewHolder(final View itemView) {
            super(itemView);

            spotName = itemView.findViewById(R.id.spot_name);
            sportVicinity = itemView.findViewById(R.id.spot_vicinity);
            spotImage = itemView.findViewById(R.id.spot_image);
            spotRating = itemView.findViewById(R.id.rating_bar);

            //listens to click of each recyclerView item
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //gets texts of textViews on click of item.
                    String lat = spotName.getTag().toString();
                    String lng = sportVicinity.getTag().toString();
                    String name = spotName.getText().toString();
                    String vicinity = sportVicinity.getText().toString();
                    String rating = Float.toString(spotRating.getRating());
                    String imageUrl = spotImage.getTag().toString();
                    String openStatus = spotRating.getTag().toString();
                    //Creating intent and putting Extras to start next Activity
                    Intent i = new Intent(itemView.getContext(), SpotActivity.class);
                    i.putExtra("lat", lat);
                    i.putExtra("lng", lng);
                    i.putExtra("name", name);
                    i.putExtra("vicinity", vicinity);
                    i.putExtra("rating", rating);
                    i.putExtra("imageReference", imageUrl);
                    i.putExtra("openStatus", openStatus);
                    itemView.getContext().startActivity(i);
                }
            });
        }
    }

    /**
     * Constructor for SpotAdapter, List of placeSpot objects is passed to it, recieved as @param placeSpots
     */
    SpotAdapter(List<PlaceSpot> placeSpots) { mplaceSpots = placeSpots; }

    /**
     * onCreateViewHolder inflates the RecyclerView with list_card layout file
     */
    @Override
    public SpotViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_card, parent, false);

        //creating Volley RequestQueue and ImageLoader
        RequestQueue mRequestQueue = Volley.newRequestQueue(parent.getContext());
        mImageLoader = new ImageLoader(mRequestQueue, new ImageLoader.ImageCache() {
            private final LruCache<String, Bitmap> mCache = new LruCache<>(10);

            public void putBitmap(String url, Bitmap bitmap) {
                mCache.put(url, bitmap);
            }

            public Bitmap getBitmap(String url) {
                return mCache.get(url);
            }
        });

        return new SpotViewHolder(itemView);
    }

    /**
     * This binds the PlaceSpot objects to the RecyclerView, creating the view
     */
    @Override
    public void onBindViewHolder(SpotViewHolder holder, int position) {
        //gets position of items of placeSpot
        PlaceSpot placeSpot = mplaceSpots.get(position);
        //binds each data to the textView
        holder.spotName.setText(placeSpot.getmName());
        holder.spotName.setTag(placeSpot.getmLat());
        holder.sportVicinity.setText(placeSpot.getmVicinity());
        holder.sportVicinity.setTag(placeSpot.getmLng());
        holder.spotImage.setTag(placeSpot.getmPhotoReference());
        holder.spotRating.setTag(placeSpot.getmOpenNow());
        if (!placeSpot.getmRating().isEmpty()) {
            holder.spotRating.setRating(Float.parseFloat(placeSpot.getmRating()));
        }

        //Loading images
        String photo_url = (placeSpot.getmPhotoReference().equals("EMPTY")) ? null:
                "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference=" +
                placeSpot.getmPhotoReference() + "&key=AIzaSyC_2SXuauBwziVEyzXs07tStDXH81wsvM8";
        holder.spotImage.setDefaultImageResId(R.drawable.noimage);
        holder.spotImage.setImageUrl(photo_url, mImageLoader);
    }

    @Override
    public int getItemCount() { return mplaceSpots.size(); }

}

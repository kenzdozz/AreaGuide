package com.ahtaya.chidozie.areaguide;

/**
 * Created by CHIDOZIE on 12/30/2017.
 * This class creates spots of tour in an area
 */

class PlaceSpot {

    /**
     * @param mName - stores the name of the spot
     * @param mVicinity - stores the name of the spot
    * @param mLat - stores the latitude of the spot
    * @param mLng - stores the longitude of the spot
    * @param mPhotoReference - stores the photo reference of the spot
    */

    private String mName, mRating, mOpenNow, mVicinity, mLat, mLng, mPhotoReference;

    PlaceSpot(String name, String rating, String openNow, String vicinity, String lat, String lng, String photoReference){
        mVicinity = vicinity;
        mName = name;
        mRating = rating;
        mOpenNow = openNow;
        mLat = lat;
        mLng = lng;
        mPhotoReference = photoReference;
    }

    //used to get spot Name
    String getmName() { return mName; }

    //used to get spot Name
    String getmVicinity() { return mVicinity; }

    //used to get spot rating
    String getmRating() { return (mRating == null)?"":mRating; }

    //used to get open status
    String getmOpenNow() { return (mOpenNow == null)?"":mOpenNow;}

    //used to get spot Photo Reference
    String getmPhotoReference() { return mPhotoReference; }

    //used to get spot latitude
    String getmLat() { return mLat; }

    //used to get spot longitude
    String getmLng() { return mLng; }
}

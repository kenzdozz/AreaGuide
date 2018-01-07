package com.ahtaya.chidozie.areaguide;

/**
 * Created by CHIDOZIE on 12/29/2017.
 * This class is used to parse the JSON object with GSON
 */
class PlaceData {

    /**
     * @param results catches an array of 'results' object in the JSON data
     * @param status returns "OK" if there is a result from the API request
     */
    Results[] results;
    String status;

    //creates the @geometry @photos and @name data from each @results object
    class Results{
        /**
         * @param geometry catches a 'geometry' object from each @results object
         * @param name catches 'name' string from each @result object
         * @param vicinity catches 'vicinity' string from each @result object
         * @param photos catches an array of 'photos' object from each @result object
         * @param opening_hours catches 'opening_hours' object from each @result object
        */
        Geometry geometry;
        String name, rating;
        String vicinity;
        Photos[] photos;
        OpeningHours opening_hours;
    }

    //creates @location data from @geometry object
    class Geometry{
        /**
         * @param location catches 'location' object from @geometry object
        */
        Location location;
    }

    //creates @lat and @lng strings from @location object
    class Location{
        //@lat and @lng catches 'lat' and 'lng' respectively from @location object
        String lat, lng;
    }

    //creates @photo_reference string from each @results object
    class Photos{
        //@photo_reference catches 'photo_reference' string from each @photos object
        String photo_reference;
    }

    //creates @open_now string from each @results object
    class OpeningHours{
        //@open_now catches 'photo_reference' string from each @opening_hours object
        String open_now;
    }
}

package com.lydia.meet_me;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import android.os.Binder;
import static com.lydia.meet_me.MapsActivity.*;

public class DownloadFiles extends AsyncTask<Double, Void, String> {
    private Marker[] placeMarkers;
    private final int MAX_PLACES = 20;
    public static MarkerOptions[] places;
    public static boolean lol = false;
    private MapsActivity myMainActivity;


    public DownloadFiles( MapsActivity activity ) {
        myMainActivity = activity;
    }




    @Override
    protected String doInBackground(Double... placesURL) {
       Log.d("SIDESTART","START");

        //process search parameter string(s)
        String status="ZERO_RESULTS";
        StringBuilder placesBuilder=null;
        Double lat= placesURL[0];
        Double lon = placesURL[1];
        Double stype = placesURL[2];
        int rad=1000;


        while(status.equals("ZERO_RESULTS")) {
            String placesSearchStr = "https://maps.googleapis.com/maps/api/place/nearbysearch/" +
                    "json?location=" + lat + "," + lon +
                    "&radius=" + Integer.toString(rad) + "&sensor=true" + "&types=food%7Cbar" +
                    "&key=AIzaSyA_os0YpUO-LdmsyMjzZY5jFrqHjO8bpDc";
            Log.d("ZXCVB",placesSearchStr);
             placesBuilder = new StringBuilder();
            HttpClient placesClient = new DefaultHttpClient();
            try {
                HttpGet placesGet = new HttpGet(placesSearchStr);
                HttpResponse placesResponse = placesClient.execute(placesGet);
                StatusLine placeSearchStatus = placesResponse.getStatusLine();
                if (placeSearchStatus.getStatusCode() == 200) {
                    HttpEntity placesEntity = placesResponse.getEntity();
                    InputStream placesContent = placesEntity.getContent();
                    InputStreamReader placesInput = new InputStreamReader(placesContent);
                    BufferedReader placesReader = new BufferedReader(placesInput);
                    String lineIn;
                    while ((lineIn = placesReader.readLine()) != null) {
                        placesBuilder.append(lineIn);

                    }





//we have an OK response
                }
                JSONObject resultObject = new JSONObject(placesBuilder.toString());
                status="whut";
                status = resultObject.getString("status");
                Log.d("ASDFG",status);
                rad+=10000;

                //try to fetch the data
            } catch (Exception e) {
                e.printStackTrace();
                Log.d("NOPE", "NOPE");
            }
        }
        rad=1000;

//execute search



        Log.d("LOCATIONJSON", placesBuilder.toString());
        return placesBuilder.toString();
    }

    //fetch and parse place data
    @Override
    protected void onPostExecute(String result) {
        placeMarkers=myMainActivity.placeMarkers;
        if (placeMarkers != null) {
            for (int pm = 0; pm < placeMarkers.length; pm++) {
                if (placeMarkers[pm] != null)
                    placeMarkers[pm].remove();
            }
        }
        //parse place data returned from Google Places

        try {
            boolean missingValue = false;
            LatLng placeLL = null;
            String placeName = "";
            String vicinity = "";
            String icon="";

            //parse JSON
            JSONObject resultObject = new JSONObject(result);
            JSONArray placesArray = resultObject.getJSONArray("results");
            places = new MarkerOptions[placesArray.length()];
            //loop through places
            for (int p = 0; p < placesArray.length(); p++) {
                //parse each place
                Log.d("DOPEPOPE",placesArray.getJSONObject(p).toString());

                //attempt to retrieve place data values
                try {

                    missingValue = false;
                    JSONObject placeObject = placesArray.getJSONObject(p);
                    JSONObject loc = placeObject.getJSONObject("geometry").getJSONObject("location");
                    placeLL = new LatLng(
                            Double.valueOf(loc.getString("lat")),
                            Double.valueOf(loc.getString("lng")));
                    vicinity = placeObject.getString("vicinity");
                    placeName = placeObject.getString("name");

                    Log.d("BALLUP",placeName);
                } catch (JSONException jse) {
                    missingValue = true;
                    Log.d("ASDFGH","nope");
                    jse.printStackTrace();
                }
                if (missingValue){
                    places[p] = null;
                }
                else{
                    places[p] = new MarkerOptions()
                            .position(placeLL)
                            .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_archer))
                                    .title(placeName)
                                    .snippet(vicinity);
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        int x=0;
        if (places != null) {
            for (int p = 0; p < places.length; p++) {
                //will be null if a value was missing
                if (places[p] != null) {

                   placeMarkers[p]= myMainActivity.mMap.addMarker(places[p]);
                    Log.d("MARKERADD", "COMP");
                    x=p;
                }
            }
            myMainActivity.mMap.moveCamera(CameraUpdateFactory.newLatLng(places[x].getPosition()));
            myMainActivity.mMap.moveCamera(CameraUpdateFactory.zoomOut());
        }



    }
}

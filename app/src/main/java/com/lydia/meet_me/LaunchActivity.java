package com.lydia.meet_me;

import android.app.Activity;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.EditText;
import android.view.View;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import java.io.IOException;
import java.util.List;


public class LaunchActivity extends Activity {
    public final static String EXTRA_MESSAGE = "com.lydia.meet_me.MESSAGE";
    int REQUEST_PLACE_PICKER =1;
    LatLng latLng;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Remove title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);
        EditText editText1 = (EditText) findViewById(R.id.enterText);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_launch, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }




    public void getLocation(View view){
        EditText saveText = (EditText) findViewById(R.id.enterText);
        //Assign the text to a local message variable
        String location = saveText.getText().toString().trim();

        List<Address> addressList=null;
        if(location!=null|| !location.equals("")) {
            Geocoder geocoder = new Geocoder(this);
            try {
                addressList = geocoder.getFromLocationName(location, 1);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Address address = addressList.get(0);
             latLng = new LatLng(address.getLatitude(), address.getLongitude());
        }
        try {
            PlacePicker.IntentBuilder intentBuilder =
                    new PlacePicker.IntentBuilder().setLatLngBounds(new LatLngBounds(latLng,latLng));
            Intent intent = intentBuilder.build(this);

            // Start the intent by requesting a result,
            // identified by a request code.
            startActivityForResult(intent, REQUEST_PLACE_PICKER);

        } catch (GooglePlayServicesRepairableException e) {
            // ...
        } catch (GooglePlayServicesNotAvailableException e) {
            // ...
        }
        /*
        Intent intent = new Intent(this, MapsActivity.class);
        intent.putExtra(EXTRA_MESSAGE, location);
        //clear the input for more notes
        saveText.getText().clear();

        startActivity(intent);
        */
    }
    @Override
    protected void onActivityResult(int requestCode,
                                    int resultCode, Intent data) {

        if (requestCode == REQUEST_PLACE_PICKER
                && resultCode == Activity.RESULT_OK) {

            // The user has selected a place. Extract the name and address.
            final Place place = PlacePicker.getPlace(data, this);

            final String addr= place.getAddress().toString().trim();
            Log.d("SEARCH ADDRESS",addr);
            String attributions = PlacePicker.getAttributions(data);
            if (attributions == null) {
                attributions = "";
            }


            Intent intent = new Intent(this, MapsActivity.class);
            intent.putExtra(EXTRA_MESSAGE, addr);
            //clear the input for more notes
           // saveText.getText().clear();

            startActivity(intent);

        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }


}

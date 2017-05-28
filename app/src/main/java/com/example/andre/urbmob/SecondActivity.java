package com.example.andre.urbmob;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.plus.model.people.Person;

public class SecondActivity extends FragmentActivity{

    private GoogleApiClient mGoogleApiClient;

    private EditText editTextDe;
    private EditText editTextPara;

    Place place;

    int i = 0;

    Person person;

    static double latitudeDe;
    static double longitudeDe;

    static double latitudePara;
    static double longitudePara;

    PlaceAutocompleteFragment autocompleteFragment;

    int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        editTextDe = (EditText) findViewById(R.id.editTextDe);
        editTextPara = (EditText) findViewById(R.id.editTextPara);

        editTextDe.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b){
                    AutoCompletar();
                    i = 1;
                }
            }
        });

        editTextPara.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b){
                    AutoCompletar();
                    i = 2;
                }
            }
        });
    }

    public void AutoCompletar(){
        try {
            Intent intent =
                    new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                            .build(this);
            startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);

        } catch (GooglePlayServicesRepairableException e) {

        } catch (GooglePlayServicesNotAvailableException e) {

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                if(i == 1){
                    place = PlaceAutocomplete.getPlace(this, data);
                    editTextDe.setText(place.getName());
                    LatLng queriedLocation = place.getLatLng();

                    latitudeDe = queriedLocation.latitude;
                    longitudeDe = queriedLocation.longitude;

                }else if(i == 2){
                    place = PlaceAutocomplete.getPlace(this, data);
                    editTextPara.setText(place.getName());
                    LatLng queriedLocation = place.getLatLng();
                    latitudePara = queriedLocation.latitude;
                    longitudePara = queriedLocation.longitude;
                }
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                // TODO: Solucionar o erro.
                Log.i( "Place1", status.getStatusMessage());

            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }

    public void EnviarRota(View view){
        finish();

/*
        if(!validate())
            Toast.makeText(getBaseContext(), "Enter some data!", Toast.LENGTH_LONG).show();
        // call AsynTask to perform network operation on separate thread
        new HttpAsyncTask().execute("http://192.168.212.255:8080/UrbMob/trajeto");
*/
    }
}

package com.example.andre.urbmob;

import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import static com.example.andre.urbmob.R.string.app_name;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener,LocationListener{

    GoogleApiClient mGoogleApiClient;

    //Mapa
    private GoogleMap mMap;

    LocationRequest locationRequest;

    private LatLng loc = null;

    Location l;

    String[] permissoes = new String[]{
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION,
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        //Chama a classe de permissoes para validacao
        PermissionUtils.validate(this,0,permissoes);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,String[] permissions, int[] grantResults){
        super.onRequestPermissionsResult(requestCode,permissions,grantResults);

        for (int result : grantResults){
            if(result == PackageManager.PERMISSION_DENIED) {
                //Alguma permissao foi negada
                alertAndFinish();
                return;
            }
        }
    }

    //Se alguma permissao for negada vai vir para esse metodo
    private void alertAndFinish(){
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(app_name).setMessage("Para utilizar este aplicativo, voce precisa aceitar as permissoes.");
            //Adiciona os botoes
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    finish();
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }

    @Override
    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        stopLocationUpdates();
        super.onStop();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        try {
            l = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

            if (l == null){
              //  Toast.makeText(this, "Não foi possivel determinar sua localização", Toast.LENGTH_LONG).show();
                loc = new LatLng(-23.5631338 , -46.6543286);
            } else{ loc =
                    new LatLng(l.getLatitude() ,
                            l.getLongitude());
            }

            mMap.setMyLocationEnabled(true);
            mMap.addMarker(new MarkerOptions().position(loc).title("Sua localização atual"));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(loc,14));
        }catch (SecurityException e){

        }
    }

    public void AtualizarCameraDoMapa(View view){
        if(mMap != null && l == null){
            loc = new LatLng(-40,-48);//l.getLatitude(), l.getLongitude());
            CameraUpdate update = CameraUpdateFactory.newLatLngZoom(loc,14);
            mMap.animateCamera(update);
        }
    }

    protected void startLocationUpdates(){
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, locationRequest, this);
    }

    protected void stopLocationUpdates(){
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient,this);
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        startLocationUpdates(); //Inicia o GPS
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d("estado", "suspenso");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d("estado", "erro");
    }

    @Override
    public void onLocationChanged(Location location) {
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        loc = new LatLng(latitude,longitude);
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(loc,14);
        mMap.animateCamera(update);
    }
}

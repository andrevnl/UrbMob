package com.example.andre.urbmob;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

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


public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback,GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener,LocationListener{
    //Classe responsavel por conectar a API do Google
    private GoogleApiClient mGoogleApiClient;
    //Classe responsavel por criar o Mapa
    private GoogleMap mMap;
    //Classe responsavel por gerenciar o GPS
    LocationRequest locationRequest;
    //LatLng para pegar a latitude e a longitude da localização atual
    private LatLng loc = null;
    //Location com a localização atual
    Location currentLocation;
    //String com as permissões
    String[] permissoes = new String[]{
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION,
            android.Manifest.permission.ACCESS_NETWORK_STATE,
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        //Chama a classe de permissoes para validacao
        PermissionUtils.validate(this,0,permissoes);

        //Configuração da API LocationServices do Google
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        //Configuração da chamada do GPS
        locationRequest = new LocationRequest();
        locationRequest.setInterval(10000); //Intervalo entre as chamadas é de 10 segundos
        locationRequest.setFastestInterval(5000); //Não recebe uma nova solicitação do GPS se não passar ao menos 5 segundos
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        // Obtem um SupportMapFragment e avisa quando esta tudo pronto
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    protected void onStart() {
        //Conecta ao serviço LocationServices
        mGoogleApiClient.connect();
        super.onStart();
    }

    @Override
    protected void onStop() {
        //Desconecta do serviço LocationServices
        mGoogleApiClient.disconnect();
        //Desativa o GPS para não atualizar o local
        //stopLocationUpdates();
        super.onStop();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        try {
            currentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

            if (currentLocation == null){
               // Toast.makeText(this, "Não foi possivel determinar sua localização", Toast.LENGTH_LONG).show();
                loc = new LatLng(-23.5631338 , -46.6543286); //Seta ponto inicial no mapa
                mMap.addMarker(new MarkerOptions().position(loc).title("Avenida Paulista"));
            } else{
                loc = new LatLng(currentLocation.getLatitude() , currentLocation.getLongitude());
                mMap.addMarker(new MarkerOptions().position(loc).title("Sua localização atual"));
            }
            //Localização atual do proprio Google Maps
            mMap.setMyLocationEnabled(true);

            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(loc,14));

        }catch (SecurityException e){
            Toast.makeText(this, "Não foi possivel determinar sua localização", Toast.LENGTH_LONG).show();
            Log.d("Erro", "Exceção : "+ e );
        }
    }

    public void AdicionarRota(View view){
        Intent intent = new Intent(this,SecondActivity.class);
        startActivity(intent);

    }

    protected void startLocationUpdates(){
        try{
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, locationRequest, this);
        }catch (SecurityException e){
            Log.d("Erro", "Exceção : "+ e);
        }

    }

    protected void stopLocationUpdates(){
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient,this);
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        startLocationUpdates(); //Inicia o GPS quando conecta na API
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d("estado", "suspenso");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d("estado", "erro de conexão");
    }

    @Override
    public void onLocationChanged(Location location) {
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        loc = new LatLng(latitude,longitude);
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(loc,14);
        mMap.animateCamera(update);
        mMap.clear();
        mMap.addMarker(new MarkerOptions().position(loc).title("Sua localização atual"));
    }

    //O proprio Android tem que chamar esse metodo caso precise de alguma permissao
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
}

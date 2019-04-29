/**
 * Uriel Henrique Marques Farias Mishima RA: 81717300
 */

package br.usjt.ciclodevidagpsemapas;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.function.Consumer;

public class MainActivity extends AppCompatActivity {

    public static final int REQUEST_CODE_GPS = 1001;

    private LocalizacaoDAO localizacaoDAO;
    private LocationManager locationManager;
    private LocationListener locationListener;

    private double latitudeAtual;
    private double longitudeAtual;

    private RecyclerView weatherRecyclerView;
    private WeatherAdapter weatherAdapter;
    private List<Weather> previsoes;
    private RequestQueue requestQueue;

    private Gson gson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        weatherRecyclerView = findViewById(R.id.weatherRecyclerView);
        requestQueue = Volley.newRequestQueue(this);
        gson = new GsonBuilder().create(); //setDateFormat("EEEE").
        previsoes = new ArrayList<>();
        weatherAdapter = new WeatherAdapter(this, previsoes);
        weatherRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        weatherRecyclerView.setAdapter(weatherAdapter);

        localizacaoDAO = new LocalizacaoDAO(this);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ListaLocaisActivity.class);
                startActivity(intent);
            }
        });

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Localizacao localizacao = new Localizacao(location.getLatitude(), location.getLongitude(), new Date());
                obtemPrevisoesV5(localizacao.getLatitude(), localizacao.getLongitude());
                localizacaoDAO.insertLocalizacao(localizacao);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();

        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, APPConstants.REQUEST_LOCATION_UPDATE_MIN_TIME, APPConstants.REQUEST_LOCATION_UPDATE_MIN_DISTANCE, locationListener);
        } else {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_GPS);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        locationManager.removeUpdates(locationListener);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE_GPS) {
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, APPConstants.REQUEST_LOCATION_UPDATE_MIN_TIME, APPConstants.REQUEST_LOCATION_UPDATE_MIN_DISTANCE, locationListener);
                }
            } else {
                Toast.makeText(this, getString(R.string.no_gps_no_app), Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    public void obtemPrevisoesV5(Double lat, Double lon){
        String endereco = getString(
                R.string.web_service_url,
                lat.toString(),
                lon.toString(),
                getString(R.string.api_key)
        );
        JsonObjectRequest req = new JsonObjectRequest(
                Request.Method.GET,
                endereco,
                null,
                (response) -> {
                    lidaComJSON(response);
                },
                (error) -> {
                    Toast.makeText(
                            MainActivity.this,
                            getString(R.string.connect_error) + ": " + error.getLocalizedMessage(),
                            Toast.LENGTH_SHORT
                    ).show();
                }
        );
        requestQueue.add(req);
    }

    public void lidaComJSON(JSONObject resultado){
        JSONArray list = null;
        try {
        previsoes.clear();
        list = resultado.getJSONArray("list");
//        if(list.length() > 0){
//            previsoes = Arrays.asList(gson.fromJson(list.toString(), Weather[].class));
//        }
//            weatherAdapter.notifyDataSetChanged();

            for (int i = 0; i < list.length(); i++){
                JSONObject caraDaVez= list.getJSONObject(i);
                long dt = caraDaVez.getLong("dt");
                JSONObject main = caraDaVez.getJSONObject("main");
                double temp_min = main.getDouble("temp_min");
                double temp_max = main.getDouble("temp_max");
                double humidity = main.getDouble("humidity");
                String description = caraDaVez.getJSONArray("weather")
                        .getJSONObject(0)
                        .getString("description");
                String icon = caraDaVez.getJSONArray("weather")
                        .getJSONObject(0)
                        .getString("icon");
                Weather w = new Weather(dt, temp_min, temp_max, humidity, description, icon);
                previsoes.add(w);
                weatherAdapter.notifyDataSetChanged();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}

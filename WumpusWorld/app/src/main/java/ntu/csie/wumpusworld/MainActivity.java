package ntu.csie.wumpusworld;

import android.*;
import android.Manifest;
import android.app.Service;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import com.google.android.gms.maps.model.StreetViewPanoramaCamera;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.util.Vector;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener {

    private String hostname;

    private enum Action {
        INIT, RESUME, START, UPDATE, GO, SHOOT
    }

    private String nickname;

    private View progressView, gameView, homeView;
    private TextView nicknameView;

    private GoogleMap mMap;
    private LocationManager mLocationManager;
    private double latitude, longitude;
    private final long LOCATION_UPDATE_MIN_TIME = 0;
    private final float LOCATION_UPDATE_MIN_DISTANCE = 0;

    private Vector positions;
    private Position lastPosition;

    private final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;

    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        hostname = getString(R.string.hostname);

        progressView = findViewById(R.id.progress);
        gameView = findViewById(R.id.game);
        homeView = findViewById(R.id.home);

        nicknameView = (TextView) findViewById(R.id.nickname);

        mLocationManager = (LocationManager) this.getSystemService(Service.LOCATION_SERVICE);
        requestLocationUpdates();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Button resumeButton = (Button) findViewById(R.id.resume);
        resumeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resume();
            }
        });

        Button startButton = (Button) findViewById(R.id.start);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                start();
            }
        });
        Button endButton = (Button) findViewById(R.id.end);
        endButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void switchView(int viewnumber) {
        switch (viewnumber) {
            case 1:
                progressView.setVisibility(View.VISIBLE);
                gameView.setVisibility(View.GONE);
                homeView.setVisibility(View.GONE);
                break;
            case 2:
                progressView.setVisibility(View.GONE);
                gameView.setVisibility(View.VISIBLE);
                homeView.setVisibility(View.GONE);
                break;
            case 3:
                progressView.setVisibility(View.GONE);
                gameView.setVisibility(View.GONE);
                homeView.setVisibility(View.VISIBLE);
                break;
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }

        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setMapToolbarEnabled(false);

        mMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
                Log.v("Location", latitude + " " + longitude);
                return false;
            }
        });

        init();
        switchView(2);

        LatLng ntu = new LatLng(25.0173405, 121.5397518);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ntu, 15));
    }

    @Override
    public void onLocationChanged(Location location) {
        changeLocation(location);
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    private void requestLocationUpdates() {
        boolean isGPSEnbaled = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (isGPSEnbaled) {
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, LOCATION_UPDATE_MIN_TIME, LOCATION_UPDATE_MIN_DISTANCE, this);
            Location location = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (location != null)
                changeLocation(location);
        }
    }

    private void changeLocation(Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();

//        if (nickname == null)
//            return;

        new AsyncTask<Void, Void, JsonObject>() {
            @Override
            protected JsonObject doInBackground(Void... voids) {
                OkHttpClient client = new OkHttpClient();

                RequestBody requestBody = RequestBody.create(JSON, new Gson().toJson(new ActionPack(latitude, longitude)));
                Request request = new Request.Builder()
                        .url(hostname + "location/" + nickname)
                        .post(requestBody)
                        .build();

                try {
                    Response response = client.newCall(request).execute();
                    return new JsonParser().parse(response.body().string()).getAsJsonObject();
                } catch (IOException e) {
                    return null;
                }
            }

            @Override
            protected void onPostExecute(final JsonObject json) {
                if (json == null) {
                    return;
                }
                int status = json.get("status").getAsInt();

                switch (status) {
                    case 0:
                        if (positions != null) {
                            if (lastPosition != null)
                                lastPosition.setVisiable(false);
                            Position p = (Position) positions.get(json.get("data").getAsInt());
                            p.setVisiable(true);
                            lastPosition = p;
                        }

                        break;
                    case 1:
                        if (lastPosition != null)
                            lastPosition.setVisiable(false);
                        break;
                }
            }
        }.execute();
    }


    private void init() {
        new AsyncTask<Void, Void, JsonObject>() {
            @Override
            protected JsonObject doInBackground(Void... voids) {
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                                .url(hostname + "init")
                                .build();

                try {
                    Response response = client.newCall(request).execute();
                    return new JsonParser().parse(response.body().string()).getAsJsonObject();
                } catch (IOException e) {
                    return null;
                }
            }

            @Override
            protected void onPostExecute(final JsonObject json) {
                if (json == null) {
                    finish();
                    return;
                }
                int status = json.get("status").getAsInt();

                switch (status) {
                    case 0:
                        initPositions(json.getAsJsonArray("data"));
                        break;
                    case 1:
                        finish();
                        break;
                }
            }
        }.execute();
    }

    private void initPositions(JsonArray jarray) {
        positions = new Vector();

        for (JsonElement jelement: jarray) {
            positions.add(new Position(jelement.getAsJsonObject()));
        }
    }

    private void resume() {
        setNickname();

        if (isNicknameValid(nickname)) {
            switchView(1);
            new StartMenuTask(Action.RESUME).execute();
        } else {
            setNicknameViewError(getString(R.string.error_no_name));
        }
    }

    private void start() {
        setNickname();

        if (isNicknameValid(nickname)) {
            switchView(1);
            new StartMenuTask(Action.START).execute();
        } else {
            setNicknameViewError(getString(R.string.error_no_name));
        }
    }

    private void setNickname() {
        EditText nicknameText = (EditText) findViewById(R.id.nickname);
        nickname = nicknameText.getText().toString();
    }

    private void setNicknameViewError(String msg) {
        nicknameView.setError(msg);
        nicknameView.requestFocus();
    }

    private boolean isNicknameValid(String nickname) {
        return nickname.length() > 0;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                if (permissions.length == 1 && permissions[0] == Manifest.permission.ACCESS_FINE_LOCATION && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mMap.setMyLocationEnabled(true);
                }
                return;
            }
        }
    }

    public class StartMenuTask extends AsyncTask<Void, Void, JsonObject> {

        private final Action action;

        StartMenuTask(Action action) {
            this.action = action;
        }

        @Override
        protected JsonObject doInBackground(Void... params) {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            OkHttpClient client = new OkHttpClient();
            Request request = null;

            switch (action) {
                case RESUME:
                    request = new Request.Builder()
                            .url(hostname + "resume/" + nickname)
                            .build();
                    break;
                case START:
                    request = new Request.Builder()
                            .url(hostname + "start/" + nickname)
                            .build();
                    break;
            }

            try {
                Response response = client.newCall(request).execute();
                return new JsonParser().parse(response.body().string()).getAsJsonObject();
            } catch (IOException e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(final JsonObject json) {
            if (json == null) {
                switchView(3);
                setNicknameViewError(getString(R.string.error_try_again));
                return;
            }
            int status = json.get("status").getAsInt();

            switch (status) {
                case 0:
                    break;
                case 1:
                    switchView(3);
                    setNicknameViewError(getString(R.string.error_no_game));
                    break;
            }
        }

        @Override
        protected void onCancelled() {
            switchView(3);
        }
    }

    private class Position {

        private final String title;
        private final double latitude, longitude;
        private Marker marker;
        private Circle circle;

        Position(JsonObject json) {
            this.title = json.get("title").getAsString();
            this.latitude = json.get("latitude").getAsDouble();
            this.longitude = json.get("longitude").getAsDouble();

            this.addMarker();
            this.addCircle();
        }

        private void addMarker() {
            this.marker = mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(this.latitude, this.longitude))
                    .title(this.title)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.new_pos)));
        }

        private void addCircle() {
            this.circle = mMap.addCircle(new CircleOptions()
                    .center(new LatLng(this.latitude, this.longitude))
                    .radius(25) // In meters
                    .visible(false));
        }

        public void setVisiable(boolean isVisable) {
            circle.setVisible(isVisable);
        }
    }

    private class ActionPack {

        private final double latitude, longitude;

        ActionPack(double latitude, double longitude) {
            this.latitude = latitude;
            this.longitude = longitude;
        }
    }
}

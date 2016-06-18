package ntu.csie.wumpusworld;

import android.*;
import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
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

import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
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

    private String nickname;

    private View progressView, gameView, homeView;
    private TextView nicknameView;
    private int currentView = 3;

    private GoogleMap mMap;
    private LocationManager mLocationManager;
    private double latitude, longitude;
    private final long LOCATION_UPDATE_MIN_TIME = 0;
    private final float LOCATION_UPDATE_MIN_DISTANCE = 10;

    private Vector positions;
    private Position lastPosition;
    private int now_pos, shoot_pos;

    private int arrow = 0;

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

        switchView(currentView);

        nicknameView = (TextView) findViewById(R.id.nickname);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

        } else {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }

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

        Button goButton = (Button) findViewById(R.id.go);
        goButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goPosition();
            }
        });

        Button shootButton = (Button) findViewById(R.id.shoot);
        shootButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shootPosition();
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
                InputMethodManager mInputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                mInputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
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

        mLocationManager = (LocationManager) this.getSystemService(Service.LOCATION_SERVICE);
        requestLocationUpdates();

        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setMapToolbarEnabled(false);

        mMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
                return false;
            }
        });

        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                int id = Integer.parseInt(marker.getId().replace('m', ' ').trim());

                View v = getLayoutInflater().inflate(R.layout.infolayout, null);

                LatLng latLng = marker.getPosition();
                TextView title = (TextView) v.findViewById(R.id.info_title);
                title.setText(marker.getTitle());

                Position p = (Position) positions.get(id);
                if (p.is_wind())
                    v.findViewById(R.id.wind).setVisibility(View.VISIBLE);
                if (p.is_smell())
                    v.findViewById(R.id.smell).setVisibility(View.VISIBLE);
                if (p.is_shootable()) {
                    findViewById(R.id.shoot).setEnabled(true);
                    shoot_pos = id;
                } else {
                    findViewById(R.id.shoot).setEnabled(false);
                }

                return v;
            }
        });

        init();

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

        if (nickname == null)
            return;

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
                            now_pos = json.get("data").getAsInt();
                            Position p = (Position) positions.get(now_pos);
                            p.setVisiable(true);
                            p.showInfo();
                            if (p.is_goable())
                                findViewById(R.id.go).setEnabled(true);

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

    private void resume() {
        setNickname();

        if (isNicknameValid(nickname)) {
            currentView = 1;
            switchView(currentView);
            new AsyncTask<Void, Void, JsonObject>() {
                @Override
                protected JsonObject doInBackground(Void... voids) {
                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder()
                            .url(hostname + "resume/" + nickname)
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
                        currentView = 3;
                        switchView(currentView);
                        setNicknameViewError(getString(R.string.error_try_again));
                        return;
                    }

                    int status = json.get("status").getAsInt();
                    switch (status) {
                        case 0:
                            currentView = 2;
                            switchView(currentView);
                            redrawPositions(json.getAsJsonArray("data"));
                            setArrow(json.get("arrow").getAsInt());
                            break;
                        case 1:
                            currentView = 3;
                            switchView(currentView);
                            setNicknameViewError(getString(R.string.error_no_game));
                            break;
                    }
                }

                @Override
                protected void onCancelled() {
                    currentView = 3;
                    switchView(currentView);
                }
            }.execute();
        } else {
            setNicknameViewError(getString(R.string.error_no_name));
        }
    }

    private void start() {
        setNickname();

        if (isNicknameValid(nickname)) {
            currentView = 1;
            switchView(currentView);
            new AsyncTask<Void, Void, JsonObject>() {
                @Override
                protected JsonObject doInBackground(Void... voids) {
                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder()
                            .url(hostname + "start/" + nickname)
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
                        currentView = 3;
                        switchView(currentView);
                        setNicknameViewError(getString(R.string.error_try_again));
                        return;
                    }

                    int status = json.get("status").getAsInt();
                    switch (status) {
                        case 0:
                            currentView = 2;
                            switchView(currentView);
                            redrawPositions(json.get("data").getAsJsonArray());
                            setArrow(3);
                            break;
                        case 1:
                            currentView = 3;
                            switchView(currentView);
                            break;
                    }
                }

                @Override
                protected void onCancelled() {
                    currentView = 3;
                    switchView(currentView);
                }
            }.execute();
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

    private void goPosition() {
        new AsyncTask<Void, Void, JsonObject>() {
            @Override
            protected JsonObject doInBackground(Void... voids) {
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url(hostname + "go/" + nickname + "/" + now_pos)
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
                        redrawPositions(json.getAsJsonArray("data"));
                        break;
                    case 1:
                        int id = json.get("pos").getAsInt();
                        Position p = (Position) positions.get(id);
                        if (json.get("code").getAsInt() == 0) {
                            p.setWumpus();
                        } else {
                            p.setPit();
                        }

                        findViewById(R.id.go).setEnabled(false);
                        findViewById(R.id.shoot).setEnabled(false);
                        break;
                }
            }
        }.execute();
    }

    private void shootPosition() {
        new AsyncTask<Void, Void, JsonObject>() {
            @Override
            protected JsonObject doInBackground(Void... voids) {
                OkHttpClient client = new OkHttpClient();
                Request request = new Request.Builder()
                        .url(hostname + "shoot/" + nickname + "/" + shoot_pos)
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
                        redrawPositions(json.getAsJsonArray("data"));
                        setArrow(json.get("arrow").getAsInt());
                        break;
                    case 1:
                        setArrow(json.get("arrow").getAsInt());
                        break;
                }
            }
        }.execute();
    }

    private class Position {

        private final String title;
        private final double latitude, longitude;
        private boolean is_wind = false, is_smell = false, is_goable = false, is_shootable = false;
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

        public void addLine(JsonObject json) {
            for (JsonElement jelement: json.getAsJsonArray("connect")) {
                int num = jelement.getAsJsonObject().get("number").getAsInt();
                Position p = (Position) positions.get(num);

                mMap.addPolyline(new PolylineOptions()
                        .add(new LatLng(this.latitude, this.longitude))
                        .add(new LatLng(p.getLatitude(), p.getLongitude())));
            }
        }

        public boolean is_wind() {
            return this.is_wind;
        }

        public boolean is_smell() {
            return this.is_smell;
        }

        public boolean is_goable() {
            return this.is_goable;
        }

        public boolean is_shootable() {
            return this.is_shootable;
        }

        public double getLatitude() {
            return this.latitude;
        }

        public double getLongitude() {
            return this.longitude;
        }

        public void setVisiable(boolean isVisable) {
            this.circle.setVisible(isVisable);
        }

        public void showInfo() {
            this.marker.showInfoWindow();
        }

        public void setCurrent() {
            this.marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.current));
            this.marker.showInfoWindow();
        }

        public void setChecked() {
            this.marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.checked));
        }

        public void setPit() {
            this.marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.pit));
        }

        public void setWumpus() {
            this.marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.wumpus));
        }

        public void setWind() {
            this.is_wind = true;
        }

        public void setSmell() {
            this.is_smell = true;
        }

        public void setGoable() {
            this.is_goable = true;
        }

        public void setShootable(boolean value) {
            this.is_shootable = value;
        }
    }

    private void initPositions(JsonArray jarray) {
        positions = new Vector();

        for (JsonElement jelement: jarray) {
            positions.add(new Position(jelement.getAsJsonObject()));
        }

        int num = 0;
        for (JsonElement jelement: jarray) {
            Position p = (Position) positions.get(num);
            p.addLine(jelement.getAsJsonObject());
            num++;
        }
    }

    private void redrawPositions(JsonArray jarray) {
        int num = 0;
        for (JsonElement jelement : jarray) {
            JsonObject jobject = jelement.getAsJsonObject();

            Position p = (Position) positions.get(num);

            if (jobject.get("is_wind").getAsBoolean())
                p.setWind();
            if (jobject.get("is_smell").getAsBoolean())
                p.setSmell();
            if (jobject.get("can_go").getAsBoolean())
                p.setGoable();
            if (arrow > 0)
                p.setShootable(jobject.get("can_shoot").getAsBoolean());
            else
                p.setShootable(false);

            if (jobject.get("is_current").getAsBoolean())
                p.setCurrent();
            else if (jobject.get("is_checked").getAsBoolean())
                p.setChecked();

            num++;
        }
    }

    private class ActionPack {

        private final double latitude, longitude;

        ActionPack(double latitude, double longitude) {
            this.latitude = latitude;
            this.longitude = longitude;
        }
    }

    private void setArrow(int remain) {
        arrow = remain;
        Button b = (Button) findViewById(R.id.shoot);
        b.setText("Shoot (" + arrow + ")");
    }
}

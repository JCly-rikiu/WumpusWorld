package ntu.csie.wumpusworld;

import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import com.google.android.gms.maps.model.StreetViewPanoramaCamera;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends FragmentActivity implements OnMapReadyCallback {

    private String hostname;

    private enum Action {
        RESUME, START, UPDATE, GO, SHOOT
    }

    private String nickname;

    private View progressView, gameView, homeView;
    private TextView nicknameView;

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        hostname = getString(R.string.hostname);

        progressView = findViewById(R.id.progress);
        gameView = findViewById(R.id.game);
        homeView = findViewById(R.id.home);

        nicknameView = (TextView) findViewById(R.id.nickname);

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

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }

    private void resume() {
        setNickname();

        if (isNicknameValid(nickname)) {
            new StartMenuTask(Action.RESUME).execute();
        } else {
            setNicknameViewError(getString(R.string.error_no_name));
        }
    }

    private void start() {
        setNickname();

        if (isNicknameValid(nickname)) {
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

    public class StartMenuTask extends AsyncTask<Void, Void, JsonObject> {

        private final Action action;

        StartMenuTask(Action action) {
            this.action = action;
        }

        @Override
        protected JsonObject doInBackground(Void... params) {
            OkHttpClient client = new OkHttpClient();
            Request request = null;

            switch (action) {
                case RESUME:
                    request = new Request.Builder()
                            .url(hostname + "resume&" + nickname)
                            .build();
                    break;
                case START:
                    request = new Request.Builder()
                            .url(hostname + "start&" + nickname)
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
                setNicknameViewError(getString(R.string.error_try_again));
                return;
            }
            int status = json.get("status").getAsInt();

            switch (status) {
                case 0:
                    break;
                case 1:
                    nicknameView.setError(getString(R.string.error_no_game));
                    break;
            }
        }

        @Override
        protected void onCancelled() {
            //showProgress(false);
        }
    }
}

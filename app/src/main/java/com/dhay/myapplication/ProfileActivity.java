package com.dhay.myapplication;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;


public class ProfileActivity extends AppCompatActivity {
    private RelativeLayout.LayoutParams layoutParams;
    ImageView imageView;
    EditText nameET, phoneET, addressET;
    TextView locationTV;
    Button saveBTN, editBTN, getLocationBTN;
    private FusedLocationProviderClient mFusedLocationClient;
    int PERMISSION_ID = 44;
    SharedPreferences preferences ;
    SharedPreferences.Editor editor ;
    String message = "DragAndDrop";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        initUI();
        preferences = getSharedPreferences("UserProfile", MODE_PRIVATE);
        editor = preferences.edit();
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        editBTN.setOnClickListener(v -> {
            if (nameET.isEnabled()) {
                setEnabled(false);
            } else {
                setEnabled(true);
            }
        });
        getLocationBTN.setOnClickListener(v -> getLastLocation());

        saveBTN.setOnClickListener(v -> {
            if (
                    !nameET.getText().toString().isEmpty() &&
                            !phoneET.getText().toString().isEmpty() &&
                            !addressET.getText().toString().isEmpty() &&
                            !locationTV.getText().toString().isEmpty()
            ) {
                editor.putString("name", nameET.getText().toString());
                editor.putString("phone", phoneET.getText().toString());
                editor.putString("address", addressET.getText().toString());
                editor.putString("location", locationTV.getText().toString());
                editor.apply();
                setEnabled(false);
                Toast.makeText(this,"saved data", Toast.LENGTH_SHORT).show();

            }else {
                Toast.makeText(this,"Enter invalid Data", Toast.LENGTH_SHORT).show();
            }
        });

        setDragAndDrop();
        setData();
    }

    private void setDragAndDrop() {
        imageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                ClipData.Item item = new ClipData.Item((CharSequence) v.getTag());
                String[] mimeTypes = {ClipDescription.MIMETYPE_TEXT_PLAIN};

                ClipData dragData = new ClipData(v.getTag().toString(), mimeTypes, item);
                View.DragShadowBuilder myShadow = new View.DragShadowBuilder(imageView);

                v.startDrag(dragData, myShadow, null, 0);
                return true;
            }
        });

        imageView.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View view, DragEvent event) {
                switch (event.getAction()) {
                    case DragEvent.ACTION_DRAG_STARTED:
                        layoutParams = (RelativeLayout.LayoutParams) view.getLayoutParams();
                        Log.d(message, "The Action is DragEvent.ACTION_DRAG_STARTED");

                        break;

                    case DragEvent.ACTION_DRAG_ENTERED:
                        Log.d(message, "The Action is DragEvent.ACTION_DRAG_ENTERED");
                        int x_cord = (int) event.getX();
                        int y_cord = (int) event.getY();
                        break;

                    case DragEvent.ACTION_DRAG_EXITED:
                        Log.d(message, "The Action is DragEvent.ACTION_DRAG_EXITED");
                        x_cord = (int) event.getX();
                        y_cord = (int) event.getY();
                        layoutParams.leftMargin = x_cord;
                        layoutParams.topMargin = y_cord;
                        view.setLayoutParams(layoutParams);
                        break;

                    case DragEvent.ACTION_DRAG_LOCATION:
                        Log.d(message, "The Action is DragEvent.ACTION_DRAG_LOCATION");
                        x_cord = (int) event.getX();
                        y_cord = (int) event.getY();
                        break;
                    case DragEvent.ACTION_DRAG_ENDED:
                        Log.d(message, "The Action is DragEvent.ACTION_DRAG_ENDED");
                        break;

                    case DragEvent.ACTION_DROP:
                        Log.d(message, "The ACTION_DROP event");

                        break;
                    default:
                        break;
                }
                return true;
            }
        });

        imageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    ClipData data = ClipData.newPlainText("", "");
                    View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(imageView);

                    imageView.startDrag(data, shadowBuilder, imageView, 0);
                 //   imageView.setVisibility(View.INVISIBLE);
                    return true;
                } else {
                    return false;
                }
            }
        });

    }

    private void setData(){
        nameET.setText(preferences.getString("name",""));
        phoneET.setText(preferences.getString("phone",""));
        addressET.setText(preferences.getString("address",""));
        locationTV.setText(preferences.getString("location",""));
    }

    private void setEnabled(Boolean enabled) {
        nameET.setEnabled(enabled);
        phoneET.setEnabled(enabled);
        addressET.setEnabled(enabled);
    }

    private void initUI() {
        imageView = findViewById(R.id.logo_app_in_profile);
        nameET = findViewById(R.id.name_profile_et);
        phoneET = findViewById(R.id.phone_profile_et);
        addressET = findViewById(R.id.address_profile_et);
        locationTV = findViewById(R.id.location_text);
        saveBTN = findViewById(R.id.save_profile_btn);
        editBTN = findViewById(R.id.edit_profile_btn);
        getLocationBTN = findViewById(R.id.location_btn);
    }


    @SuppressLint("MissingPermission")
    private void getLastLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                mFusedLocationClient.getLastLocation().addOnCompleteListener(task -> {
                    Location location = task.getResult();
                    if (location == null)  requestNewLocationData();

                    else locationTV.setText(location.getLatitude() + ":" + location.getLongitude());
                });
            } else {
                Toast.makeText(this, "Please turn on" + " your location...", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        } else {
            requestPermissions();
        }
    }

    @SuppressLint("MissingPermission")
    private void requestNewLocationData() {

        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(5);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(5);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
    }

    private LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location mLastLocation = locationResult.getLastLocation();
            locationTV.setText(mLastLocation.getLatitude() + ":" + mLastLocation.getLongitude());

        }
    };

    private boolean checkPermissions() {
        return ActivityCompat.checkSelfPermission(
                this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }
    private void requestPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{
                android.Manifest.permission.ACCESS_COARSE_LOCATION,
                android.Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_ID);
    }

    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    @Override
    public void
    onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_ID) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (checkPermissions()) {
            getLastLocation();
        }
    }

}
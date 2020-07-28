package com.example.baitaptuan5;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import pub.devrel.easypermissions.EasyPermissions;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.baitaptuan5.databinding.ActivityMainBinding;
import com.google.android.gms.maps.model.LatLng;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    LatLng latLng;
    boolean isChecked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        if (!checkRequiredPermissions()) checkRequiredPermissions();

        final GPSLocation gpsLocation = new GPSLocation(this);

        if (gpsLocation.canGetLocation) {
            gpsLocation.getLocation();
            binding.tvText.setText("Lat" + gpsLocation.getLatitude() + "\nLon" + gpsLocation.getLongitude());
        } else {
            binding.tvText.setText("Unabletofind");
            System.out.println("Unable");
        }

        binding.btnCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isChecked = true;
                latLng = gpsLocation.getLocationFromAddress(getBaseContext(), binding.etInputText.getText().toString());
                if (latLng != null) {
                    binding.tvText.setText("Lat: " + latLng.latitude + "\nLon: " + latLng.longitude);
                } else {
                    binding.tvText.setText("Unable to find");
                }
            }
        });
        binding.btnGoogleMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(),MapsActivity.class);
                if (binding.etInputText.getText().toString().trim().length() !=0 && isChecked){
                      intent.putExtra("address",binding.etInputText.getText());
                    intent.putExtra("lat",latLng.latitude);
                    intent.putExtra("lon",latLng.longitude);
                }else {
                    intent.putExtra("address","My location");
                    intent.putExtra("lat",gpsLocation.getLatitude());
                    intent.putExtra("lon",gpsLocation.getLongitude());
                }
                startActivity(intent);
            }
        });
    }

    private boolean checkRequiredPermissions() {
        String[] perms = {Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_WIFI_STATE,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.CHANGE_NETWORK_STATE,
                Manifest.permission.WAKE_LOCK};
        if (!EasyPermissions.hasPermissions(this, perms)) {
            // Do not have permissions, request them now
            EasyPermissions.requestPermissions(this, getString(R.string.message_request_permission_read_phone_state),
                    20000, perms);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }
}
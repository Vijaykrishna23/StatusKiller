package com.hemanth.statuskiller;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.Arrays;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class MainActivity extends AppCompatActivity {


    public static final String SHARED_PREF_NAME = "SHARED PREF";
    public static final String IS_CHECKED = "IS CHECKED";
    private static final String[] PERMISSIONS = new String[]{READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE};

    private Switch startStopSwitch;
    private PendingIntent pendingIntent;
    private AlarmManager alarmManager;
    private TextView startStopText;

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startStopSwitch = findViewById(R.id.switch_start_stop);
        startStopText = findViewById(R.id.text_switch);
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        sharedPreferences = getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);


        Intent intent = new Intent(this, DeleteStatusesReceiver.class);

        pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0);

        boolean isChecked = sharedPreferences.getBoolean(IS_CHECKED, false);

        startStopSwitch.setChecked(isChecked);

        changeText(isChecked);


        checkPermissions();

        startStopSwitch.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                checkPermissions();

                alarmManager.setInexactRepeating(
                        AlarmManager.ELAPSED_REALTIME,
                        SystemClock.elapsedRealtime() + AlarmManager.INTERVAL_FIFTEEN_MINUTES,
                        AlarmManager.INTERVAL_FIFTEEN_MINUTES,
                        pendingIntent);

            } else {
                if (alarmManager != null) {
                    alarmManager.cancel(pendingIntent);
                }


            }

            sharedPreferences.edit()
                    .putBoolean(IS_CHECKED, b)
                    .apply();
            changeText(b);
        });


    }

    private void changeText(boolean isChecked) {
        startStopText.setText(isChecked ? "Kill is ON" : "Kill is OFF");
    }


    public void checkPermissions() {


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {

            String[] permissionsNotGranted = Arrays.stream(PERMISSIONS)
                    .filter((permission) -> ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED)
                    .toArray(String[]::new);


            if (permissionsNotGranted.length > 0)
                ActivityCompat.requestPermissions(this, permissionsNotGranted, 0);

        } else {
            if (ActivityCompat.checkSelfPermission(this, READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE}, 0);
                return;
            }
        }


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (!(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
            Toast.makeText(this, "Permission is not granted", Toast.LENGTH_SHORT).show();
        }

    }

    void deleteRecursive(File fileOrDirectory) {
        if (fileOrDirectory.isDirectory())
            for (File child : fileOrDirectory.listFiles())
                deleteRecursive(child);

        fileOrDirectory.delete();
    }


}

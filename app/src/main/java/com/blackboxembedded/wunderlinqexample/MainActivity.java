/*
WunderLINQ Client Application
Copyright (C) 2020  Keith Conger, Black Box Embedded, LLC

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/
package com.blackboxembedded.wunderlinqexample;

import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity {
    public final static String TAG = "MainActivity";

    private TextView tvOnKeyUpValue;
    private TextView tvOnKeyDownValue;
    private TextView tvValueOneValue;
    private TextView tvValueTwoValue;

    // WunderLINQ performance data broadcast action
    public static final String ACTION_PERFORMANCE_DATA_AVAILABLE = "com.blackboxembedded.wunderlinq.ACTION_PERFORMANCE_DATA_AVAILABLE";

    // Broadcast receiver for handling the broadcast
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Check if the received action matches the custom broadcast action
            if (intent.getAction().equals(ACTION_PERFORMANCE_DATA_AVAILABLE)) {
                // Retrieve the performance data from the intent, see documentation for current key values and meanings.
                double data1 = intent.getDoubleExtra("odometer", 0);
                double data2 = intent.getDoubleExtra("voltage", 0);

                // Display the received data
                Log.d(TAG,"Odometer: " + data1 + ", Voltage: " + data2);
                tvValueOneValue.setText(data1 + " km");
                tvValueTwoValue.setText(data2 + " V");
            }
        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // Read when button is pressed
        Log.d(TAG, "onKeyDown Keycode: " + keyCode);
        tvOnKeyDownValue.setText("Keycode: " + keyCode);
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        // Read when button is released
        Log.d(TAG, "onKeyUp Keycode: " + keyCode);
        tvOnKeyUpValue.setText("Keycode: " + keyCode);
        switch (keyCode) {
            case KeyEvent.KEYCODE_ENTER:
                SoundManager.playSound(this, R.raw.enter);
                return true;
            case KeyEvent.KEYCODE_ESCAPE:
                SoundManager.playSound(this, R.raw.enter);
                // Capture Escape Press to launch WunderLINQ app
                String wunderLINQApp = "wunderlinq://datagrid";
                Intent intent = new
                        Intent(android.content.Intent.ACTION_VIEW);
                intent.setData(Uri.parse(wunderLINQApp));
                try {
                    startActivity(intent);
                } catch ( ActivityNotFoundException ex  ) {
                }
                return true;
            default:
                SoundManager.playSound(this, R.raw.directional);
                return super.onKeyUp(keyCode, event);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvOnKeyUpValue = findViewById(R.id.tvOnKeyUpValue);
        tvOnKeyDownValue = findViewById(R.id.tvOnKeyDownValue);
        tvValueOneValue = findViewById(R.id.tvValueOneValue);
        tvValueTwoValue = findViewById(R.id.tvValueTwoValue);

        // Keep screen on
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        // Register the broadcast receiver with the intent filter
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_PERFORMANCE_DATA_AVAILABLE);
        ContextCompat.registerReceiver(this, broadcastReceiver, intentFilter, ContextCompat.RECEIVER_EXPORTED);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Unregister the broadcast receiver
        unregisterReceiver(broadcastReceiver);
    }
}

package com.blackboxembedded.wunderlinqexample;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    public final static String TAG = "MainActivity";

    private TextView tvOnKeyUpValue;
    private TextView tvOnKeyDownValue;
    private TextView tvValueOneValue;
    private TextView tvValueTwoValue;

    // Custom broadcast action
    public static final String CUSTOM_ACTION = "com.blackboxembedded.wunderlinq.ACTION_PERFORMANCE_DATA_AVAILABLE";

    // Broadcast receiver for handling the broadcast
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Check if the received action matches the custom broadcast action
            if (intent.getAction().equals(CUSTOM_ACTION)) {
                // Retrieve the data from the intent
                double data1 = intent.getDoubleExtra("odometer", 0);
                double data2 = intent.getDoubleExtra("voltage", 0);

                // Display the received data
                Log.d(TAG,"Data 1: " + data1 + "\nData 2: " + data2);
                tvValueOneValue.setText(data1 + " km");
                tvValueTwoValue.setText(data2 + " V");
            }
        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.d(TAG, "onKeyDown Keycode: " + keyCode);
        tvOnKeyDownValue.setText("Keycode: " + keyCode);
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        Log.d(TAG, "onKeyUp Keycode: " + keyCode);
        tvOnKeyUpValue.setText("Keycode: " + keyCode);
        return super.onKeyUp(keyCode, event);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvOnKeyUpValue = findViewById(R.id.tvOnKeyUpValue);
        tvOnKeyDownValue = findViewById(R.id.tvOnKeyDownValue);
        tvValueOneValue = findViewById(R.id.tvValueOneValue);
        tvValueTwoValue = findViewById(R.id.tvValueTwoValue);

        // Register the broadcast receiver with the intent filter
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(CUSTOM_ACTION);
        registerReceiver(broadcastReceiver, intentFilter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Unregister the broadcast receiver
        unregisterReceiver(broadcastReceiver);
    }
}

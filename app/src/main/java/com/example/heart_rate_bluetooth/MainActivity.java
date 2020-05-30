package com.example.heart_rate_bluetooth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;

public class MainActivity extends AppCompatActivity {

    private TextView tvDeceiveName, tvAddress, tvStatus;
    private TextView tvSpO2, tvHeartRate;
    private LineChart mpLineChart;

    private static final int STATE_LISTENING = 1;
    private static final int STATE_CONNECTING = 2;
    private static final int STATE_CONNECTED = 3;
    private static final int STATE_CONNECTION_FAILED = 4;
    private static final int STATE_MESSAGE_RECEIVED = 5;
    private static final int STATE_MESSAGE_WRITE = 6;
    private static final int STATE_MESSAGE_TOAST = 7;

    private static final int REQUEST_SCAN_DEVICE = 11;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initWidgets();
    }

    private void initWidgets() {

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.option_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mn_scan_device:
                Intent intent = new Intent(MainActivity.this, ScanDeceiveActivity.class);
                startActivityForResult(intent,REQUEST_SCAN_DEVICE);
                break;
            case R.id.mn_connect_to_deceive:
                Toast.makeText(this, "connect to deceive", Toast.LENGTH_SHORT).show();
                break;
            case R.id.mn_disconnect:
                break;
        }
        return true;
    }
}

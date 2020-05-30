package com.example.heart_rate_bluetooth;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Set;

public class ScanDeceiveActivity extends AppCompatActivity {

    private Button btnScan;
    private ListView lvListPaired, lvListScan;
    private static final int REQUEST_ENABLE_BLUETOOTH = 1;
    private static final int REQUEST_ACCESS_FIND_LOCATION = 2;
    public static final int RESULT_SCAN_OK = 3;
    public static final String DEVICE_NAME = "device name";

    private BluetoothAdapter bluetoothAdapter;
    private ArrayAdapter<String> listAdapter;
    private ArrayList<String> listDevice = new ArrayList<String>();
    private ArrayAdapter<String> listPaired;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_deceive);

        initWidgets();
        listAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        lvListScan.setAdapter(listAdapter);
        listPaired = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        lvListPaired.setAdapter(listPaired);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        checkBluetoothState();
        setEvenClick();

        checkCoarseLocationPermission();
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(devicesFoundReceiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));
        registerReceiver(devicesFoundReceiver, new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_STARTED));
        registerReceiver(devicesFoundReceiver, new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED));
    }

    @Override
    protected void onPause() {
        super.onPause();

        unregisterReceiver(devicesFoundReceiver);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_ENABLE_BLUETOOTH){
            checkBluetoothState();
        }
    }

    private void initWidgets() {
        btnScan = findViewById(R.id.btn_scan);
        lvListPaired = findViewById(R.id.lv_paired_deceive_list);
        lvListScan = findViewById(R.id.lv_scan_deceive_list);

    }

    private void setEvenClick() {
        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listAdapter.clear();
                listPaired.clear();

                Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
                if (pairedDevices.size() > 0) {
                    for (BluetoothDevice device : pairedDevices) {
                        listPaired.add(device.getName() + "\n" + device.getAddress());
                    }
                } else {
                    String noDevices = getResources().getText(R.string.none_paired).toString();
                    listPaired.add(noDevices);
                }
                listPaired.notifyDataSetChanged();

                if(checkCoarseLocationPermission()) {
                    if (bluetoothAdapter.isDiscovering()) {
                        bluetoothAdapter.cancelDiscovery();
                    }
                    bluetoothAdapter.startDiscovery();
                }
            }
        });

        lvListPaired.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });
        lvListScan.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });
    }

    private void checkBluetoothState() {
        if(bluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth is not supported in your device!", Toast.LENGTH_SHORT).show();
        }
        else {
            if(bluetoothAdapter.isEnabled()) {
                if(bluetoothAdapter.isDiscovering()) {
                    Toast.makeText(this, "Device discovering process...", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(this, "Bluetooth has been enabled. You should scan and connect to other device", Toast.LENGTH_SHORT).show();
                    btnScan.setEnabled(true);
                }
            }
            else {
                Toast.makeText(this, "You need to enable Bluetooth", Toast.LENGTH_SHORT).show();
                Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableIntent, REQUEST_ENABLE_BLUETOOTH);
            }
        }

    }

    private boolean checkCoarseLocationPermission() {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_ACCESS_FIND_LOCATION);
            return false;
        }
        else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case REQUEST_ACCESS_FIND_LOCATION :
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Access coarse location allowed. You can scan Bluetooth devices", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(this, "Access coarse location forbidden. You can't scan Bluetooth devices", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }

    private final BroadcastReceiver devicesFoundReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if(BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if(device != null) if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
                    String address = device.getAddress();
                    if(!listDevice.contains(address)) {
                        if(device.getName() != null){
                            String name = device.getName();
                            listAdapter.add(name + "\n" + address);
                        }
                        else{
                            listAdapter.add(address);
                        }
                        listDevice.add(address);
                        listAdapter.notifyDataSetChanged();
                    }
                }
            }
            else if(BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                Toast.makeText(context, "ACTION_DISCOVERY_FINISHED", Toast.LENGTH_SHORT).show();
                btnScan.setText("Scanning Bluetooth Devices");
                //Log.d("myTag", "This is my message");
            }
            else if(BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                Toast.makeText(context, "ACTION_DISCOVERY_STARTED", Toast.LENGTH_SHORT).show();
                btnScan.setText("Scanning in progress...");
            }
        }
    };
}

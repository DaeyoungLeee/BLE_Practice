package kr.co.aiotlab.ble_practice;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanRecord;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Vector;

public class MainActivity extends AppCompatActivity {

    Button startScan, stopScan, btn_gitBle, btn_gitBLE2;

    BluetoothAdapter mBluetoothAdapter;

    BluetoothLeScanner mBluetoothLeScanner;

    BluetoothLeAdvertiser mBluetoothLeAdvertiser;

    private static final int PERMISSIONS = 100;

    Vector<Beacon> beacon;

    BeaconAdapter beaconAdapter;

    ListView beaconListView;

    ScanSettings.Builder mScanSettings;

    List<ScanFilter> scanFilters;

    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss", Locale.KOREAN);

    BluetoothGatt bluetoothGatt;
    BluetoothDevice device;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startScan = findViewById(R.id.btn_start_scan);
        stopScan = findViewById(R.id.btn_stop_scan);
        btn_gitBle = findViewById(R.id.btn_ble);
        btn_gitBLE2 = findViewById(R.id.btn_ble2);

        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSIONS);
        beaconListView = (ListView) findViewById(R.id.beaconListView);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mBluetoothLeScanner = mBluetoothAdapter.getBluetoothLeScanner();
        mBluetoothLeAdvertiser = mBluetoothAdapter.getBluetoothLeAdvertiser();
        beacon = new Vector<>();
        mScanSettings = new ScanSettings.Builder();
        mScanSettings.setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY);
        // 얘는 스캔 주기를 2초로 줄여주는 Setting입니다.
        // 공식문서에는 위 설정을 사용할 때는 다른 설정을 하지 말고
        // 위 설정만 단독으로 사용하라고 되어 있네요 ^^
        // 위 설정이 없으면 테스트해 본 결과 약 10초 주기로 스캔을 합니다.

        /*ScanSettings scanSettings = mScanSettings.build();
        scanFilters = new Vector<>();
        ScanFilter.Builder scanFilter = new ScanFilter.Builder();
        scanFilter.setDeviceAddress("F5:EE:0A:AE:77:FB"); //ex) 00:00:00:00:00:00
        ScanFilter scan = scanFilter.build();
        scanFilters.add(scan);
        mBluetoothLeScanner.startScan(scanFilters, scanSettings, mScanCallback);*/

        // 장치검색
        startScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBluetoothLeScanner.startScan(mScanCallback);
            }
        });
        // 장치검색 중지
        stopScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBluetoothLeScanner.stopScan(mScanCallback);
            }
        });
        btn_gitBle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), GithubBLE_Activity.class);
                startActivity(intent);
            }
        });
        btn_gitBLE2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), GithubBLE2_Activity.class);
                startActivity(intent);
            }
        });


        beaconListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                // 주소 가져오기
                //Toast.makeText(MainActivity.this, beacon.get(i).getAddress(), Toast.LENGTH_SHORT).show();
                device = mBluetoothAdapter.getRemoteDevice(beacon.get(i).getAddress());

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    Toast.makeText(MainActivity.this, "연결 시도중", Toast.LENGTH_SHORT).show();
                    bluetoothGatt = device.connectGatt(getApplicationContext(), false, bluetoothGattCallback, BluetoothDevice.TRANSPORT_LE);
                    bluetoothGatt.connect();

                }

            }
        });

    }

    // 블루투스 LE 콜백함수
    ScanCallback mScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(final int callbackType, final ScanResult result) {
            super.onScanResult(callbackType, result);
            try {
                ScanRecord scanRecord = result.getScanRecord();
                Log.d("getTxPowerLevel()",scanRecord.getTxPowerLevel()+"");
                Log.d("onScanResult()", result.getDevice().getAddress() + "\n" + result.getRssi() + "\n" + result.getDevice().getName()
                        + "\n" + result.getDevice().getBondState() + "\n" + result.getDevice().getType());

                final ScanResult scanResult = result;
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                beacon.add(0, new Beacon(scanResult.getDevice().getAddress(), scanResult.getRssi(), simpleDateFormat.format(new Date())));
                                beaconAdapter = new BeaconAdapter(beacon, getLayoutInflater());
                                beaconListView.setAdapter(beaconAdapter);
                                beaconAdapter.notifyDataSetChanged();
                            }
                        });
                    }
                }).start();

            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            super.onBatchScanResults(results);
            Log.d("onBatchScanResults", results.size() + "");
        }

        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
            Log.d("onScanFailed()", errorCode+"");
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBluetoothLeScanner.stopScan(mScanCallback);
    }

    BluetoothGattCallback bluetoothGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                Toast.makeText(MainActivity.this, "연결되었습니다.", Toast.LENGTH_SHORT).show();
            }else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                Toast.makeText(MainActivity.this, "연결이 끊겼습니다.", Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(MainActivity.this, "Nothing", Toast.LENGTH_SHORT).show();
            }
        }
    };
}

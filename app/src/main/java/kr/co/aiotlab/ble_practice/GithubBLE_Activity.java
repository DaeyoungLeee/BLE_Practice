package kr.co.aiotlab.ble_practice;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.vise.baseble.ViseBle;
import com.vise.baseble.callback.IConnectCallback;
import com.vise.baseble.callback.scan.IScanCallback;
import com.vise.baseble.callback.scan.ScanCallback;
import com.vise.baseble.core.DeviceMirror;
import com.vise.baseble.exception.BleException;
import com.vise.baseble.model.BluetoothLeDevice;
import com.vise.baseble.model.BluetoothLeDeviceStore;

import java.util.ArrayList;
import java.util.UUID;

public class GithubBLE_Activity extends AppCompatActivity {

    Button startScan, stopScan;
    ListView listView;
    ViseBle viseBle;
    TextView txt_test;

    final ViseBle viseBleSet = viseBle.getInstance();

    final ArrayList<String> arrayList_address = new ArrayList<>();
    final ArrayList<String> arrayList_name = new ArrayList<>();
    final ArrayList<Integer> arrayList_rssi = new ArrayList<>();
    final ArrayList<BluetoothLeDevice> arrayList_device = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.github_ble_activity);

        listView = findViewById(R.id.listView_git);
        startScan = findViewById(R.id.btn_startScan_ble);
        stopScan = findViewById(R.id.btn_stopScan_ble);
        txt_test = findViewById(R.id.txt_test);

        // 초기화
        viseBle.config()
                .setScanTimeout(-1)
                .setConnectTimeout(10 * 1000)
                .setOperateTimeout(5 * 1000)
                .setConnectRetryCount(3)
                .setConnectRetryInterval(1000)
                .setConnectRetryCount(3)
                .setOperateRetryInterval(1000)
                .setMaxConnectCount(3);
        viseBleSet.init(this);

        startScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 검색(스캔)
                viseBleSet.startScan(scanCallback);
            }
        });

        stopScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 스캔 중지
                viseBleSet.stopScan(scanCallback);
            }
        });

        // 블루투스 리스트 클릭
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                BluetoothLeDevice device = arrayList_device.get(i);

                viseBleSet.connect(device, iConnectCallback);
                viseBleSet.connectByMac(arrayList_address.get(i), iConnectCallback);
                viseBleSet.connectByName(arrayList_name.get(i), iConnectCallback);
                if (viseBleSet.isConnect(device)) {
                    Toast.makeText(GithubBLE_Activity.this, "!!!", Toast.LENGTH_SHORT).show();
                }

                // Toast.makeText(GithubBLE_Activity.this, "Device : " + arrayList_name.get(i) + "MAC 주소 : " + arrayList_address.get(i) + " 장치 : ", Toast.LENGTH_SHORT).show();

            }
        });
    }

    // 스캔 콜백함수
    IScanCallback iScanCallback = new IScanCallback() {
        @Override
        public void onDeviceFound(BluetoothLeDevice bluetoothLeDevice) {
            arrayList_address.add(bluetoothLeDevice.getAddress());
            arrayList_name.add(bluetoothLeDevice.getName());
            arrayList_rssi.add(bluetoothLeDevice.getRssi());
            arrayList_device.add(bluetoothLeDevice);

            ArrayAdapter arrayAdapter = new ArrayAdapter(getApplicationContext(), R.layout.support_simple_spinner_dropdown_item, arrayList_name);
            listView.setAdapter(arrayAdapter);

        }

        @Override
        public void onScanFinish(BluetoothLeDeviceStore bluetoothLeDeviceStore) {

        }

        @Override
        public void onScanTimeout() {

        }
    };
    ScanCallback scanCallback = new ScanCallback(iScanCallback);

    // 연결 콜백함수
    IConnectCallback iConnectCallback = new IConnectCallback() {
        @Override
        public void onConnectSuccess(DeviceMirror deviceMirror) {
            Toast.makeText(GithubBLE_Activity.this, "연결되었습니다.", Toast.LENGTH_SHORT).show();

        }

        @Override
        public void onConnectFailure(BleException exception) {
            Toast.makeText(GithubBLE_Activity.this, "연결에 실패했습니다..", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onDisconnect(boolean isActive) {
            Toast.makeText(GithubBLE_Activity.this, "연결이 해지되었습니다.", Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        viseBleSet.disconnect();
    }
}

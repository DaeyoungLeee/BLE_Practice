package kr.co.aiotlab.ble_practice;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;

public class GithubBLE2_Activity extends AppCompatActivity {

    Button btn_StartScan, btn_StopScan;

    

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.github_ble2_activity);

        btn_StartScan = findViewById(R.id.btn_startScan_ble2);
        btn_StopScan = findViewById(R.id.btn_startScan_ble2);

    }
}

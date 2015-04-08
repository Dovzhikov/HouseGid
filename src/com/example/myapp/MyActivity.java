package com.example.myapp;

import android.app.Activity;
import android.net.wifi.ScanResult;
import android.os.Bundle;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MyActivity extends Activity
        implements OnCheckedChangeListener {
    private TextView text;
    private CheckBox cbEnable;
    private WifiManager manager;

    public BroadcastReceiver receiver = new BroadcastReceiver(){
        @Override
        public void onReceive(Context context, Intent intent) {
            int wifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE,
                    WifiManager.WIFI_STATE_UNKNOWN);
            switch(wifiState){
                case WifiManager.WIFI_STATE_ENABLING:
                    text.setText("Wi-Fi state enabling");
                    break;
                case WifiManager.WIFI_STATE_ENABLED:
                    text.setText("Wi-Fi state enabled");
                    startMonitoringRssi();
                    //WifiInfo in = manager.getConnectionInfo();
                    //text.append("\n"+in.getSSID());
                    break;
                case WifiManager.WIFI_STATE_DISABLING:
                    text.setText("Wi-Fi state disabling");
                    break;
                case WifiManager.WIFI_STATE_DISABLED:
                    text.setText("Wi-Fi state disabled");
                    stopMonitoringRssi();
                    break;
                case WifiManager.WIFI_STATE_UNKNOWN:
                    text.setText("Wi-Fi state unknown");
                    break;
            }
        } };

    public BroadcastReceiver rssiReceiver = new BroadcastReceiver(){
        @Override
        public void onReceive(Context context, Intent intent) {
            WifiInfo info = manager.getConnectionInfo();

            text.append("\nChange signal in " + info.getSSID());
            text.append("\n\tSignal level:\t" +
                    WifiManager.calculateSignalLevel(info.getRssi(), 5));
            text.append("\n\tLink speed:\t" + info.getLinkSpeed() +
                    " " + WifiInfo.LINK_SPEED_UNITS);
        }
    };
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        text = (TextView)findViewById(R.id.text);
        cbEnable = (CheckBox)findViewById(R.id.cdEnable);
        manager = (WifiManager)getSystemService(Context.WIFI_SERVICE);
        this.registerReceiver(this.receiver,
                new IntentFilter(WifiManager.WIFI_STATE_CHANGED_ACTION));
        cbEnable.setChecked(manager.isWifiEnabled());//
        //cbEnable.setChecked(false);
        cbEnable.setOnCheckedChangeListener(this);
    }
    @Override
    public void onCheckedChanged(CompoundButton view, boolean isChecked) {
        manager.setWifiEnabled(isChecked);
    }

    public void startMonitoringRssi() {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                manager.startScan();
                List<ScanResult> sr = manager.getScanResults();
                //manager.getScanResults();
                for(ScanResult i:sr){
                    text.append("\n"+i.SSID+"  "+i.level+"  "+i.BSSID);
                }
            }
        }, 28 , 28);

    }

    private void stopMonitoringRssi() {
        if (this.rssiReceiver.isInitialStickyBroadcast())
            this.unregisterReceiver(rssiReceiver);
    }
}

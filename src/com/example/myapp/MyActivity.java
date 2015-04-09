package com.example.myapp;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.wifi.ScanResult;
import android.os.Bundle;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;
import com.example.myapp.database.DataBase;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MyActivity extends Activity
        implements OnCheckedChangeListener {
    private TextView text;
    private CheckBox cbEnable;
    private WifiManager manager;


    private String ssid;
    private String bssid;
    private int level;

    public BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int wifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE,
                    WifiManager.WIFI_STATE_UNKNOWN);
            switch (wifiState) {
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
                    //stopMonitoringRssi();
                    break;
                case WifiManager.WIFI_STATE_UNKNOWN:
                    text.setText("Wi-Fi state unknown");
                    break;
            }
        }
    };

    //    public BroadcastReceiver rssiReceiver = new BroadcastReceiver(){
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            WifiInfo info = manager.getConnectionInfo();
//
//            text.append("\nChange signal in " + info.getSSID());
//            text.append("\n\tSignal level:\t" +
//                    WifiManager.calculateSignalLevel(info.getRssi(), 5));
//            text.append("\n\tLink speed:\t" + info.getLinkSpeed() +
//                    " " + WifiInfo.LINK_SPEED_UNITS);
//        }
//    };
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        text = (TextView) findViewById(R.id.text);
        cbEnable = (CheckBox) findViewById(R.id.cdEnable);
        manager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
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

    private DataBase db = new DataBase(this);


    public void addtodb(View v) {
        SQLiteDatabase sqdb = db.getWritableDatabase();
        String insertQuery = "INSERT INTO " +
                db.TABLE_NAME + " (" +
                db.SSID + ", " + db.BSSID + ", " + db.LEVEL + ") VALUES ('" +
                ssid + "' ,'" + bssid
                + "' ,'" + level + "')";
        sqdb.execSQL(insertQuery); //!!!
    }

    public void testselect(View view) {
        SQLiteDatabase sqdb = db.getWritableDatabase();
        String query = "SELECT * FROM " + db.TABLE_NAME;
        Cursor cursor = sqdb.rawQuery(query, null);
        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndex(db._ID));
            String ssid = cursor.getString(cursor.getColumnIndex(db.SSID));
            text.append("\n---\n" + ssid + "  " + bssid + "  " + level);
        }
    }

    public void deletedata(View view) {
        SQLiteDatabase sqdb = db.getWritableDatabase();
        String delete = "DELETE FROM " + db.TABLE_NAME;
        sqdb.execSQL(delete);
    }

    public void startMonitoringRssi() {
        //int j = 0;
//        Timer timer = new Timer();
//        timer.schedule(new TimerTask() {
//            @Override
//            public void run() {
        manager.startScan();
        List<ScanResult> sr = manager.getScanResults();

        //manager.getScanResults();
        for (ScanResult i : sr) {
            text.append("\n" + i.SSID + "  " + i.level + "  " + i.BSSID);
            ssid = i.SSID;
            level = i.level;
            bssid = i.BSSID;
        }
//            }
//        }, 280, 280);

    }

//    private void stopMonitoringRssi() {
//        if (this.rssiReceiver.isInitialStickyBroadcast())
//            this.unregisterReceiver(rssiReceiver);
//    }
}

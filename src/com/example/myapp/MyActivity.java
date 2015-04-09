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

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MyActivity extends Activity
        implements OnCheckedChangeListener {
    private TextView text;
    private CheckBox cbEnable;
    private WifiManager manager;


    //    private String ssid[];
//    private String bssid[];
//    private int level[];
    public ArrayList<String> ssid = new ArrayList<String>();
    public ArrayList<String> bssid = new ArrayList<String>();
    public ArrayList<Integer> level = new ArrayList<Integer>();


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


    public void addtodb(View v) { // кнопка добавления в бд списка точек
        String insertQuery;
        SQLiteDatabase sqdb = db.getWritableDatabase();
        for (int i = 0; i < ssid.size(); i++) {
            insertQuery = "INSERT INTO " +
                    db.TABLE_NAME + " (" /*+
                    db.SSID + ", "*/ + db.BSSID + ", " + db.LEVEL + ") VALUES ('" /*  +
                    ssid.get(i) + "' ,'"   */ + bssid.get(i)
                    + "' ,'" + level.get(i) + "')";
            System.out.println(insertQuery.toString());
            sqdb.execSQL(insertQuery); //!!!
        }
    }

    public void testselect(View view) {   // запрос и вывод добавленных точек
        SQLiteDatabase sqdb = db.getWritableDatabase();
        String query = "SELECT * FROM " + db.TABLE_NAME;
        Cursor cursor = sqdb.rawQuery(query, null);
        while (cursor.moveToNext()) {
            int id1 = cursor.getInt(cursor.getColumnIndex(db._ID));
            String ssid1 = cursor.getString(cursor.getColumnIndex(db.SSID));
            String bssid1 = cursor.getString(cursor.getColumnIndex(db.BSSID));
            String level1 = cursor.getString(cursor.getColumnIndex(db.LEVEL));
            text.append("\n---\n" + ssid1 + "  " + bssid1 + "  " + level1);
        }
    }

    public void deletedata(View view) { // очистка данных бд
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
            ssid.add(i.SSID);
            level.add(i.level);
            bssid.add(i.BSSID);
        }
//            }
//        }, 280, 280);

    }

//    private void stopMonitoringRssi() {
//        if (this.rssiReceiver.isInitialStickyBroadcast())
//            this.unregisterReceiver(rssiReceiver);
//    }
}

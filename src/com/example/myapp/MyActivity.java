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
import android.os.Message;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;
import com.example.myapp.database.DataBase;
import android.os.Handler;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.LogRecord;

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

    public String Repl(String s) {
        s.replaceAll(";", "':");
        return s.replaceAll("'", "''");

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        text = (TextView) findViewById(R.id.text);
        cbEnable = (CheckBox) findViewById(R.id.cdEnable);
        manager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        //manager = WifiManager();
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

    private Handler handler = new Handler() {
        int j = 0;
        @Override
        public void handleMessage(Message msg){
            text.setText("");
            if (manager.getScanResults() != null) {
                for (ScanResult i : manager.getScanResults()) {
                    text.append("\n" + i.SSID + "  " + i.level + "  " + i.BSSID);
                    ssid.add(i.SSID);
                    level.add(i.level);
                    bssid.add(i.BSSID);
                }
                text.append("\n Wi-Fi Count: " + manager.getScanResults().size());
            }
        }
    };

    public void addtodb(View v) { // ?????? ?????????? ? ?? ?????? ?????
        String insertQuery;
        SQLiteDatabase sqdb = db.getWritableDatabase();
        for (int i = 0; i < ssid.size(); i++) {
            insertQuery = "INSERT INTO " +
                    db.TABLE_NAME + " (" +
                    db.SSID + ", " + db.BSSID + ", " + db.LEVEL + ") VALUES ('" +
                    Repl(ssid.get(i)) + "' ,'" + bssid.get(i)
                    + "' ,'" + level.get(i) + "')";
            System.out.println(insertQuery.toString());
            sqdb.execSQL(insertQuery); //!!!
        }
    }

    public void testselect(View view) {   // запрос и вывод добавленных точек
        SQLiteDatabase sqdb = db.getWritableDatabase();
        String query = "SELECT * FROM " + db.TABLE_NAME;
        Cursor cursor = sqdb.rawQuery(query, null);
        int j = 1;
        while (cursor.moveToNext()) {
            int id1 = cursor.getInt(cursor.getColumnIndex(db._ID));
            String ssid1 = cursor.getString(cursor.getColumnIndex(db.SSID));
            String bssid1 = cursor.getString(cursor.getColumnIndex(db.BSSID));
            String level1 = cursor.getString(cursor.getColumnIndex(db.LEVEL));
            text.append("\n---\n" + j++ + "." + ssid1 + "  " + bssid1 + "  " + level1);

        }
    }

    public void deletedata(View view) { // очистка данных бд
        SQLiteDatabase sqdb = db.getWritableDatabase();
        String delete = "DELETE FROM " + db.TABLE_NAME;
        sqdb.execSQL(delete);
    }

    public void startMonitoringRssi() {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                while (cbEnable.isChecked()) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    manager.startScan();
                    handler.sendMessage(handler.obtainMessage());
                }
            }
        });
        t.start();
       /* manager.startScan();

        while (manager.getScanResults() == null){

        }
        if( manager.getScanResults() != null) {
            for (ScanResult i :manager.getScanResults()) {
                text.append("\n" + i.SSID + "  " + i.level + "  " + i.BSSID);
                ssid.add(i.SSID);
                level.add(i.level);
                bssid.add(i.BSSID);
            }
            text.append("\n Wi-Fi Count: "+manager.getScanResults().size());
        }*/

    }
}

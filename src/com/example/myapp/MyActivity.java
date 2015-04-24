package com.example.myapp;

import android.app.Activity;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.wifi.ScanResult;
import android.os.Bundle;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.os.Message;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;
import com.example.myapp.database.DataBase;
import android.os.Handler;

import java.util.ArrayList;
import java.util.List;

public class MyActivity extends Activity
        implements OnCheckedChangeListener {
    private TextView text;
    private CheckBox cbEnable;
    private WifiManager manager;
    public ArrayList<Point> pointList;
    private EditText editText;
    private TextView largeText;
    private Point p = null;

    private int lalka = 0;
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
                    break;
                case WifiManager.WIFI_STATE_DISABLING:
                    text.setText("Wi-Fi state disabling");
                    break;
                case WifiManager.WIFI_STATE_DISABLED:
                    text.setText("Wi-Fi state disabled");
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
        this.registerReceiver(this.receiver,
                new IntentFilter(WifiManager.WIFI_STATE_CHANGED_ACTION));
        cbEnable.setChecked(manager.isWifiEnabled());//
        cbEnable.setOnCheckedChangeListener(this);
        pointList = new ArrayList<>();
        editText = (EditText) findViewById(R.id.editText);
        largeText = (TextView) findViewById(R.id.textView);
        editText.setText("" + lalka++);
    }

    @Override
    public void onCheckedChanged(CompoundButton view, boolean isChecked) {
        manager.setWifiEnabled(isChecked);
    }

    private DataBase db = new DataBase(this);

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            text.setText("");
            if (manager.getScanResults() != null) {
                //    bssid.clear();
                //  ssid.clear();
                //level.clear();
                List<ScanResult> scanResultList = manager.getScanResults();
                if (p != null) {
                    for (ScanResult s : p.scanResults) {
                        text.setTextColor(Color.WHITE);
                        text.append("\n" + s.SSID + "  " + s.level + "  " + s.BSSID);
                        for (ScanResult l : scanResultList) {
                            if (s.BSSID.equals(l.BSSID)) {
                                text.setTextColor(Color.RED);
                                text.append(" " + l.level + " Name" + p.pointName);
                            }
                        }
                    }
                }
                else {
                    for (ScanResult i : scanResultList) {
                        text.append("\n" + i.SSID + "  " + i.level + "  " + i.BSSID);
                        //                   if (!bssid.contains(i.BSSID)) {
                        //                      ssid.add(i.SSID);
                        //                    level.add(i.level);
                        //                  bssid.add(i.BSSID);
                        //            }
                    }
                }
                //      text.append("\n Wi-Fi bssid Count: " + bssid.size());
                searchPoint(scanResultList);
            }
        }
    };


    public void openactivity(View v) {
        Intent intent = new Intent(MyActivity.this, PointView.class);
//        intent.putExtra("list",pointList);
        intent.putParcelableArrayListExtra("list", pointList);
        startActivityForResult(intent, 0);

    }

    public void addtodb(View v) { // ?????? ?????????? ? ?? ?????? ?????
        String insertQuery;
        SQLiteDatabase sqdb = db.getWritableDatabase();
        for (int i = 0; i < bssid.size(); i++) {
            insertQuery = "INSERT INTO " +
                    db.TABLE_NAME + " (" +
                    db.SSID + ", " + db.BSSID + ", " + db.LEVEL + ") VALUES ('" +
                    Repl(ssid.get(i)) + "' ,'" + bssid.get(i)
                    + "' ,'" + level.get(i) + "')";
            System.out.println(insertQuery.toString());
            sqdb.execSQL(insertQuery); //!!!
        }
    }

    public void addnewpoint(View v) {
        pointList.add(new Point(editText.getText().toString(), manager.getScanResults()));
        editText.setText("" + lalka++);
    }

    public void writePoint(View v) {
        for (Point p : pointList) {
            text.append("\n" + p.pointName);
            for (ScanResult i : p.scanResults) {
                text.append("\n" + i.SSID + "  " + i.level + "  " + i.BSSID);
            }
            text.append("\n" + "------------");
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            p = data.getParcelableExtra("result");
            //editText.setText("Result="+p.pointName);
        }
    }

    public void searchPoint(List<ScanResult> list) {
        for (Point p : pointList) {
            largeText.setText(p.Compare(list));
        }
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
    }
}
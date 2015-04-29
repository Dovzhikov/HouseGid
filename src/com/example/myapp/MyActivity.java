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
import android.os.Environment;
import android.os.Message;
import android.view.View;
import android.widget.*;
import android.widget.CompoundButton.OnCheckedChangeListener;
import com.example.myapp.database.DataBase;
import android.os.Handler;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MyActivity extends Activity implements OnCheckedChangeListener {
    private TextView text;
    private CheckBox cbEnable;
    private WifiManager manager;
    public ArrayList<Point> pointList;
    private TextView largeText;
    private Point p = null;
    private List<ScanResult> scanResultList;
    int Tmp = 0;
    int t = 1;

    private static final String DIRECTORY_DOCUMENTS = "/docs";
    private static final String FILE_EXT = ".txt";
    private EditText editText;
    private String dir;
    private StringBuffer temp;

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

        dir = Environment.getExternalStorageDirectory().toString() + DIRECTORY_DOCUMENTS;
        File folder = new File(dir);

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

    private void saveFile(String fileName) {
        try {
            if (fileName.endsWith(FILE_EXT)) {
                fileName += FILE_EXT;
            }
            File file = new File(dir, fileName);
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(temp.toString().getBytes());
            fos.close();
        }
        catch (Exception e) {
            Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show(); // ����� ������
        }
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
                scanResultList = manager.getScanResults();
                if (p != null) {
                    for (ScanResult s : p.scanResults) {
                        text.setTextColor(Color.WHITE);
                        text.append("\n" + s.SSID + "  " + s.level + "  " + s.BSSID);
                        for (ScanResult l : scanResultList) {
                            if (s.BSSID.equals(l.BSSID)) {
                                text.setTextColor(Color.RED);
                                text.append(" " + l.level + " Name " + p.pointName);
                            }
                        }
                    }
                } else {
                    for (ScanResult i : scanResultList) {

                     //   Tmp += i.level;
                     //   text.append(" Avg = "+Tmp/t++);
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

    public void addtodb(View v) {
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
        /*for (Point p : pointList) {
            text.append("\n" + p.pointName);
            for (ScanResult i : p.scanResults) {
                text.append("\n" + i.SSID + "  " + i.level + "  " + i.BSSID);
            }
            text.append("\n" + "------------");
        }*/

        for (Point p : pointList) {
            temp.append(p.pointName + "\n");
            for (ScanResult i : p.scanResults) {
                temp.append(i.SSID + "     |     " + i.BSSID + "     |     " + i.level);
            }
            temp.append("**********");
        }
        saveFile("testing");
    }


    public void testselect(View view) {   // ������ � ����� ����������� �����
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

    public void deletedata(View view) { // ������� ������ ��
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
            if(p.Compare(list)){
                largeText.setText(p.pointName);
                break;
            }
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
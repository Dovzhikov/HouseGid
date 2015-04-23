package com.example.myapp;

import android.net.wifi.ScanResult;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

/**
 * Created by User on 19.04.2015.
 */
public class Point {
    public ArrayList<String> ssid = new ArrayList<String>();
    public ArrayList<String> bssid = new ArrayList<String>();
    public ArrayList<Integer> level = new ArrayList<Integer>();
    public List<ScanResult> scanResults;
    public String pointName;
    private int count;

    public Point(String s, List<ScanResult> list) {
        this.pointName = s;
        scanResults = list;
        for (ScanResult i : list) {
            ssid.add(i.SSID);
            level.add(i.level);
            bssid.add(i.BSSID);
        }
        this.count = list.size();
        for(ListIterator<ScanResult> scanResultListIterator = scanResults.listIterator();scanResultListIterator.hasNext();){
            ScanResult result = scanResultListIterator.next();
            if(result.level<-85){
                scanResultListIterator.remove();
            }
        }
    }

    private boolean compareLevel(int pointLevel, int scanLevel) {
        return (scanLevel > pointLevel - 20 && scanLevel < pointLevel + 20);
    }

    public String Compare(List<ScanResult> list) {
        int res = 0;
        for (ScanResult s : list) {
            //for (int i = 0; i< count;i++) {
            //if (bssid.get(i).equals(s.BSSID) && compareLevel(level.get(i), s.level)) {
            for (ScanResult l : scanResults) {
                if (l.BSSID.equals(s.BSSID) && compareLevel(l.level, s.level)) {
                    res++;
                    break;
                }
            }
        }
        if (res == count) {
            return this.pointName;
        } else {
            return "Search...";
        }
    }


}

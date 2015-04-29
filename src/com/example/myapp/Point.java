package com.example.myapp;

import android.net.wifi.ScanResult;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.*;

/**
 * Created by User on 19.04.2015.
 */
public class Point implements Parcelable {
    //    public ArrayList<String> ssid = new ArrayList<String>();
//    public ArrayList<String> bssid = new ArrayList<String>();
//    public ArrayList<Integer> level = new ArrayList<Integer>();
    public List<ScanResult> scanResults;
    public String pointName;
    private int count;


    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(pointName);
        parcel.writeList(scanResults);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    private Point(Parcel parcel) {
        pointName = parcel.readString();
        scanResults = parcel.readArrayList(ClassLoader.getSystemClassLoader());
    }

    public Point(String s, List<ScanResult> list) {
        this.pointName = s;
        scanResults = list;
//        for (ScanResult i : list) {
//            ssid.add(i.SSID);
//            level.add(i.level);
//            bssid.add(i.BSSID);
//        }
        Collections.sort(list, new Comparator<ScanResult>() {
            @Override
            public int compare(ScanResult scanResult, ScanResult t1) {
                if (scanResult.level >= t1.level) return -1;
                else return 1;
            }
        });

//        int i = 0;
//        for(ListIterator<ScanResult> scanResultListIterator = scanResults.listIterator();scanResultListIterator.hasNext();){
//            ScanResult result = scanResultListIterator.next();
//            if(result.level<-85 || i > 3){
//                scanResultListIterator.remove();
//            }
//            i++;
//        }
        this.count = list.size();
    }

    private boolean compareLevel(int pointLevel, int scanLevel) {
        return (scanLevel > pointLevel - 10 && scanLevel < pointLevel + 10);
    }

    private void addnewhotspot(List<ScanResult> list) {
        for (ScanResult s : list) {
            boolean flag = true;
            for (ScanResult l : scanResults) {
                if (s.BSSID.equals(l.BSSID)) flag = false;
            }
            if (flag) {
                scanResults.add(s);
                flag = true;
            }
        }
    }

    @Override
    public String toString() {
        return pointName;
    }

    public boolean Compare(List<ScanResult> list) {
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
        if (res >= count)
            //return this.pointName;
            return true;
        else
            return false;
    }


    public static final Parcelable.Creator<Point> CREATOR = new Parcelable.Creator<Point>() {
        public Point createFromParcel(Parcel in) {
            return new Point(in);
        }

        public Point[] newArray(int size) {
            return new Point[size];
        }
    };
}
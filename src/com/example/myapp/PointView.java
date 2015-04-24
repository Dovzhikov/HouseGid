package com.example.myapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

import java.util.ArrayList;

/**
 * Created by User on 23.04.2015.
 */
public class PointView extends Activity {

    private ArrayList<Point> scanResultArrayList;
    private ListView listView;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pointview);
        Intent intent = getIntent();
        scanResultArrayList = intent.getParcelableArrayListExtra("list");
        listView = (ListView) findViewById(R.id.listView);
        print();
    }

    public void doing(){
    }

    public void print(){
        ListAdapter listAdapter = new ArrayAdapter<Point>(this,android.R.layout.simple_list_item_1,scanResultArrayList);
        listView.setAdapter(listAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent();

                intent.putExtra("result",(Point)listAdapter.getItem(i));
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }
}
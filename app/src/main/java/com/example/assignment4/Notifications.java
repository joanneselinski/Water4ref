package com.example.assignment4;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.HashSet;
import java.util.Set;

public class Notifications extends AppCompatActivity {

    SharedPreferences appData;
    Spinner spinner1;
    Spinner time1;
    Spinner time2;
    Spinner time3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);
        appData = getApplication().getSharedPreferences("data", Context.MODE_PRIVATE);

        Context context = getApplicationContext();
        CharSequence text = "Turn notifications on in Settings";
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);
        boolean notifsSet = appData.getBoolean("notifSettings",true);
        if(!notifsSet) {
            toast.show();
        }

        time1 = (Spinner) findViewById(R.id.time1);
        ArrayAdapter<String> myAdapter1 = new ArrayAdapter<String>(Notifications.this,
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.freq));
        myAdapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        time1.setAdapter(myAdapter1);

        time2 = (Spinner) findViewById(R.id.time2);
        ArrayAdapter<String> myAdapter2 = new ArrayAdapter<String>(Notifications.this,
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.time2));
        myAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        time2.setAdapter(myAdapter2);

        time3 = (Spinner) findViewById(R.id.time3);
        ArrayAdapter<String> myAdapter3 = new ArrayAdapter<String>(Notifications.this,
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.percent));
        myAdapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        time3.setAdapter(myAdapter3);

        spinner1 = (Spinner) findViewById(R.id.spinner1);
        ArrayAdapter<String> myAdapter4 = new ArrayAdapter<String>(Notifications.this,
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.time2));
        myAdapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner1.setAdapter(myAdapter4);
    }


    public void save(View view) {
        finish();
    }

    public void cancel(View view) {
        finish();
    }



}

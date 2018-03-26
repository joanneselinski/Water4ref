package com.example.assignment4;

import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;
import android.widget.ToggleButton;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class Settings extends AppCompatActivity  {

    EditText goal;
    Button goalButton;
    ToggleButton units;
    ToggleButton notifs;
    SharedPreferences appData;
    SharedPreferences.Editor appEditor;
    EditText prefTime;
    Toolbar myToolbar;
    Context context;
    int duration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent launcher = getIntent();
        setContentView(R.layout.activity_settings);
        context = getApplicationContext();
        duration = Toast.LENGTH_SHORT;

        myToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);

        myToolbar.inflateMenu(R.menu.settings);
        goalButton = findViewById(R.id.applyGoal);
        units = findViewById(R.id.units);
        notifs = findViewById(R.id.notifs);
        prefTime = (EditText) findViewById(R.id.prefTime);
        goal = (EditText) findViewById(R.id.editText);

        appData = getApplication().getSharedPreferences("data", Context.MODE_PRIVATE);
        appEditor = appData.edit();

        goal.setText(""+appData.getInt("goal", 64));

        //Set reset time of day
        int hour = appData.getInt("hour",0);
        int mins = appData.getInt("mins",0);
        prefTime.setText(String.format("%02d:%02d", hour, mins));

        //Set notifications ToggleButton
        if(appData.getBoolean("notifSettings",true)) {
            notifs.setChecked(true);
        } else {
            notifs.setChecked(false);
        }

        //Set units ToggleButton & Goal accordingly
        if(appData.getBoolean("unitSettings",true)) {
            units.setChecked(true);
        } else {
            units.setChecked(false);
        }

        final TimePickerDialog.OnTimeSetListener time = new TimePickerDialog.OnTimeSetListener() {

            @Override
            public void onTimeSet(TimePicker view, int hour, int mins) {
                Log.d("on time set", "CALLED");
                //Log.d("d",Integer.toString(hour));
                //Log.d("d",Integer.toString(minute));
                Calendar c = Calendar.getInstance();
                int chour = c.get(Calendar.HOUR_OF_DAY);
                int cmins = c.get(Calendar.MINUTE);
                // check if reset time < current time of day
                if (hour*60 + mins < chour*60 + cmins) { // reset next day
                    c.add(Calendar.DATE, 1);
                }
                c.set(Calendar.HOUR_OF_DAY, hour);
                c.set(Calendar.MINUTE, mins);
                c.set(Calendar.SECOND, 0);

                appEditor.putLong("reset", c.getTimeInMillis());
                appEditor.putInt("hour", hour);
                appEditor.putInt("mins", mins);
                prefTime.setText(String.format("%02d:%02d", hour, mins));
                Log.d("roll-over time", c.toString());
                appEditor.commit();

                CharSequence text = "Reset Hour Updated to " + prefTime.getText();
                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
            }
        };

        prefTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                TimePickerDialog tpick = new TimePickerDialog(Settings.this, time, 0, 0, true);
                tpick.show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
            if (id == R.id.close) {
                finish();
                return true;
            }
        return super.onOptionsItemSelected(item);
        }

    public void updateGoal(View view) {
        CharSequence error = "Must enter goal";

        String in = goal.getText().toString();
        if (in.equals("")) {
            Toast toast = Toast.makeText(context, error, duration);
            toast.show();
        }
        else {
            int newGoal = Integer.parseInt(in);
            appEditor.putInt("goal", newGoal);
            appEditor.commit();

            CharSequence text = "Goal Updated!";

            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        }
    }

    public void updateNotifs(View view) {
        appEditor.putBoolean("notifSettings", notifs.isChecked());
        appEditor.commit();
    }

    // oz is true and ml is false
    public void updateUnits(View view) {
        appEditor.putBoolean("unitSettings", units.isChecked());
        appEditor.commit();
        if (appData.getBoolean("unitSettings", units.isChecked())){
            this.mlToOz();
        }
        else {
            this.ozToMl();
        }
    }

    public void mlToOz() {
        int updatedG = (int) Math.round(appData.getInt("goal", 1893) / MainActivity.OZ2ML);
        int updatedI = (int) Math.round(appData.getInt("waterIntake", 0) / MainActivity.OZ2ML);
        goal.setText(""+updatedG);
        appEditor.putInt("goal", updatedG);
        appEditor.putInt("waterIntake", updatedI);
        appEditor.commit();

    }

    public void ozToMl() {
        int updatedG = (int) Math.round(appData.getInt("goal", 64) * MainActivity.OZ2ML);
        int updatedI = (int) Math.round(appData.getInt("waterIntake", 0) * MainActivity.OZ2ML);
        goal.setText(""+updatedG);
        appEditor.putInt("goal", updatedG);
        appEditor.putInt("waterIntake", updatedI);
        appEditor.commit();
    }

}

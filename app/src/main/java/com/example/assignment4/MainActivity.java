package com.example.assignment4;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    Button plus;
    Button minus;
    Button eight;
    Button twelve;
    Button seventeen;
    EditText numIn;
    TextView goal;
    TextView oz;
    ImageView glass;
    ImageView logo;
    int totalIntake = 0;
    int goalNum = 64;
    String units = "oz";
    SharedPreferences appData;
    SharedPreferences.Editor appEditor;
    public static final float OZ2ML = 29.5735f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
            this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        appData = getApplication().getSharedPreferences("data", Context.MODE_PRIVATE);
        appEditor = appData.edit();

        plus = (Button) findViewById(R.id.plus);
        minus = (Button) findViewById(R.id.minus);
        eight = (Button) findViewById(R.id.eight);
        twelve = (Button) findViewById(R.id.twelve);
        seventeen = (Button) findViewById(R.id.seventeen);
        numIn = (EditText) findViewById(R.id.numIn);
        goal = (TextView) findViewById(R.id.goal);
        glass = (ImageView) findViewById(R.id.glass);
        logo = (ImageView) findViewById(R.id.logo);
        oz = (TextView) findViewById(R.id.oz);
    }

    @Override
    public void onResume() {
        super.onResume();
        this.setUpValues();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(MainActivity.this, Settings.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_intake) {
            Intent intent = new Intent(MainActivity.this, MainActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_history) {
            Intent intent = new Intent(MainActivity.this, History.class);
            startActivity(intent);
        } else if (id == R.id.nav_notif) {
            Intent intent = new Intent(MainActivity.this, Notifications.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    public void setUpValues() {

        totalIntake = appData.getInt(getString(R.string.waterIntake), 0);
        this.checkResetIntake();

        goalNum = appData.getInt(getString(R.string.goal), 64);

        if (appData.getBoolean("unitSettings", true)) {
            oz.setText("oz");
            units = "oz";
        } else {
            oz.setText("ml");
            units = "ml";
        }

        goal.setText("You drank " + totalIntake + " / " + goalNum + " " + units +"\nof water today!");
        this.checkGoal();
    }

    public void checkResetIntake() {
        int resetHr = appData.getInt("hour",0);
        int resetMn = appData.getInt("mins",0);
        Calendar c = Calendar.getInstance(); // now
        c.set(Calendar.SECOND, 0);
        appEditor.putLong("lastUse", c.getTimeInMillis());
        long now = c.getTimeInMillis();
        long resetWhen = appData.getLong("reset", now);
        if (now >= resetWhen) {
            // reset daily total
            totalIntake = 0;
            appEditor.putInt("waterIntake", 0);
            // reset next roll-over date/time
            c.add(Calendar.DATE, 1);
            c.set(Calendar.HOUR_OF_DAY, resetHr);
            c.set(Calendar.MINUTE, resetMn);
            c.set(Calendar.SECOND, 0);
            appEditor.putLong("reset", c.getTimeInMillis());
            Log.d("roll-over time", c.toString());
        }
        appEditor.commit();
    }

    private int getNum() {
        int newAmnt = 0;
        String amnt = numIn.getText().toString();
        if (!amnt.equals("")) {
            newAmnt = Integer.parseInt(amnt);
        }
        return newAmnt;
    }

    public void decrease(View view) {
        this.setUpValues();
        int subAmnt = this.getNum();
        totalIntake -= subAmnt;
        goal.setText("You drank " + totalIntake + " / " + goalNum + " " + units +"\nof water today!");
        this.checkGoal();
    }

    public void increase(View view) {
        this.setUpValues();
        int addAmnt = this.getNum();
        totalIntake += addAmnt;
        goal.setText("You drank " + totalIntake + " / " + goalNum + " " + units +"\nof water today!");
        this.checkGoal();
    }

    public void eightButton(View view) {
        if (appData.getBoolean("unitSettings", true)) {
            numIn.setText("8");
        }
        else {
            numIn.setText("250");
        }
    }

    public void twelveButton(View view) {

        if (appData.getBoolean("unitSettings", true)) {
            numIn.setText("12");
        }
        else {
            numIn.setText("350");
        }
    }

    public void seventeenButton(View view) {
        if (appData.getBoolean("unitSettings", true)) {
            numIn.setText("17");
        }
        else {
            numIn.setText("500");
        }
    }

    public void checkGoal() {
        appEditor.putInt("waterIntake", totalIntake);
        appEditor.commit();
        if ( totalIntake < goalNum/4) {
            this.changeImage(1);
        }
        else if (totalIntake < goalNum/2) {
            this.changeImage(2);
        }
        else if ( totalIntake < goalNum*3/4) {
            this.changeImage(3);
        }
        else if (totalIntake < goalNum) {
            this.changeImage(4);
        }
        else if (totalIntake >= goalNum) {
            this.changeImage(5);
        }
    }
    public void changeImage(int num) {
        Drawable dr;
        switch (num) {
            case 1:
                dr = getResources().getDrawable(R.drawable.empty);
                glass.setImageDrawable(dr);
                break;
            case 2:
                dr = getResources().getDrawable(R.drawable.fourth);
                glass.setImageDrawable(dr);
                break;
            case 3:
                dr = getResources().getDrawable(R.drawable.half);
                glass.setImageDrawable(dr);
                break;
            case 4:
                dr = getResources().getDrawable(R.drawable.tq);
                glass.setImageDrawable(dr);
                break;
            case 5:
                dr = getResources().getDrawable(R.drawable.full);
                glass.setImageDrawable(dr);
                break;
            default:
                break;
        }
    }
}

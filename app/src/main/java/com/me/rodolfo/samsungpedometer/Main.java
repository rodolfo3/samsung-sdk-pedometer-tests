package com.me.rodolfo.samsungpedometer;

import com.samsung.android.sdk.SsdkUnsupportedException;
import com.samsung.android.sdk.motion.Smotion;
import com.samsung.android.sdk.motion.SmotionPedometer;

import android.app.Activity;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;


public class Main extends Activity implements SmotionPedometer.ChangeListener {

    protected Smotion motionSensor;
    protected SmotionPedometer pedometer;
    protected TextView calories;
    protected TextView steps;
    protected TextView runningSteps;
    protected TextView speed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (!initializeSensor()) {
            Toast.makeText(this, R.string.deviceNotSupported, Toast.LENGTH_LONG).show();
        } else {
            initializeView();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (pedometer != null) {
            pedometer.stop();
        }
    }

    protected void initializeView() {
        calories = (TextView) findViewById(R.id.caloriesCounter);
        steps = (TextView) findViewById(R.id.stepCounter);
        runningSteps = (TextView) findViewById(R.id.stepsRunningCounter);
        speed = (TextView) findViewById(R.id.speed);

        pedometer = new SmotionPedometer(Looper.getMainLooper(), motionSensor);
        pedometer.start(this);
    }

    protected boolean initializeSensor() {
        motionSensor = new Smotion();

        try {
            motionSensor.initialize(this);
        } catch (SsdkUnsupportedException e) {
            e.printStackTrace();
            return false;
        }

        return motionSensor.isFeatureEnabled(Smotion.TYPE_PEDOMETER) && motionSensor.isFeatureEnabled(Smotion.TYPE_PEDOMETER_WITH_UPDOWN_STEP);
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
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onChanged(SmotionPedometer.Info info) {
        long totalCount = info.getCount(SmotionPedometer.Info.COUNT_WALK_FLAT);
        calories.setText(Double.toString(info.getCalorie()));
        steps.setText(Long.toString(totalCount));
        runningSteps.setText(Long.toString(info.getCount(SmotionPedometer.Info.COUNT_RUN_FLAT)));
        speed.setText(Double.toString(info.getSpeed()));

        Log.v("Main_onChange", info.toString());
    }
}


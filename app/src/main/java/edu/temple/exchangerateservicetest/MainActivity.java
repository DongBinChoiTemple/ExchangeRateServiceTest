package edu.temple.exchangerateservicetest;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;


public class MainActivity extends Activity {

    boolean connected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ((Button) findViewById(R.id.button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v("MainActivity", "Connecting");
                if (connected) {
                    myService.getExchangeRate(serviceHandler);
                }
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        Intent serviceIntent = new Intent(this, ExchangeRateService.class);
        bindService(serviceIntent, myConnection, Context.BIND_AUTO_CREATE);
    }

    ExchangeRateService myService;




    Handler serviceHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            final Handler serviceHandler = new Handler(new Handler.Callback(){
                public boolean handleMessage(Message msg){
                    try {
                        JSONObject exRateObject = new JSONObject((String)msg.obj);
                        int dollarsPerBitcoin = exRateObject.getJSONObject("USD").getInt("last");
                        ((TextView)findViewById(R.id.textView)).setText(String.valueOf(dollarsPerBitcoin));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    return false;
                }
            });
            return false;
        }
    });


    ServiceConnection myConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            ExchangeRateService.TestBinder binder = (ExchangeRateService.TestBinder) service;
            myService = binder.getService();

            connected = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            connected = false;
        }
    };

    @Override
    public void onStop() {
        super.onStop();
        unbindService(myConnection);
    }

    /*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    */
}
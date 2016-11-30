package edu.temple.exchangerateservicetest;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

public class ExchangeRateService extends Service {
    public ExchangeRateService() {
    }
    Handler handler;
    IBinder mBinder = new TestBinder();

    @Override
    public IBinder onBind(Intent intent) {

        Notification.Builder n;

        Intent i = new Intent(this, MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.setAction("SOME_ACTION");
        PendingIntent pIntent = PendingIntent.getActivity(this, 0, i, 0);
        n  = new Notification.Builder(this)
                .setContentTitle(getString(R.string.app_name))
                .setContentText("Your service is running")
                .setContentIntent(pIntent)
                .setAutoCancel(false);

        return mBinder;
    }

    public class TestBinder extends Binder {
        ExchangeRateService getService (){
            return ExchangeRateService.this;
        }
    }

    public void getExchangeRate(final Handler handler) {
        this.handler = handler;

        Thread thd = new Thread() {
            public void run() {
                try {
                    URL url = new URL("http://blockchain.info/ticker");
                    BufferedReader reader = new BufferedReader(
                            new InputStreamReader(
                                    url.openStream()));
                    String response = reader.readLine();
                    Message msg = Message.obtain();
                    msg.obj = response;
                    Log.v("Downloaded data", response);
                    handler.sendMessage(msg);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
    }
}

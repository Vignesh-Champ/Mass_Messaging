package com.Aarohan.massmessaging;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    public class SmsDeliveredReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent arg1) {
            switch (getResultCode()) {
                case Activity.RESULT_OK: {
                    Toast.makeText(context, "SMS delivered", Toast.LENGTH_SHORT).show();
                    break;
                }
                case Activity.RESULT_CANCELED: {
                    Toast.makeText(context, "SMS not delivered", Toast.LENGTH_SHORT).show();
                    break;
                }
            }
        }
    }

    public class SmsSentReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent arg1) {
            switch (getResultCode()) {

                case Activity.RESULT_OK: {
                    Toast.makeText(context, "SMS Sent", Toast.LENGTH_SHORT).show();
                    break;
                }
                case SmsManager.RESULT_ERROR_GENERIC_FAILURE: {
                    Toast.makeText(context, "SMS generic failure", Toast.LENGTH_SHORT)
                            .show();

                    break;
                }
                case SmsManager.RESULT_ERROR_NO_SERVICE: {
                    Toast.makeText(context, "SMS no service", Toast.LENGTH_SHORT).show();
                    break;

                }
                case SmsManager.RESULT_ERROR_NULL_PDU:
                    Toast.makeText(context, "SMS null PDU", Toast.LENGTH_SHORT).show();
                    break;
                case SmsManager.RESULT_ERROR_RADIO_OFF: {
                    Toast.makeText(context, "SMS radio off", Toast.LENGTH_SHORT).show();
                    break;
                }
            }
        }
    }

    private void sendSMS(String phoneNumber, String message) {
        PendingIntent sentPI = PendingIntent.getBroadcast(MainActivity.this, 0,
                new Intent(MainActivity.this, SmsSentReceiver.class), 0);

        PendingIntent deliveredPI = PendingIntent.getBroadcast(MainActivity.this, 1,
                new Intent(MainActivity.this, SmsDeliveredReceiver.class), 0);
        try {

            SmsManager sms = SmsManager.getDefault();
            sms.sendTextMessage(phoneNumber, null, message, sentPI, deliveredPI);

        } catch (Exception e) {

            e.printStackTrace();
            Toast.makeText(getBaseContext(), "SMS sending failed...", Toast.LENGTH_SHORT).show();
        }

    }

    String delimited;

    public String delimiting(String whole, char delimiter) {
        int index = whole.indexOf(delimiter);
        delimited = whole.substring(0, index);
        return whole.substring(index + 1);
    }

    @Override
    protected void onPause() {
        super.onPause();


    }
    //@param permissions
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_SEND_SMS: {
                if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getBaseContext(), "The App would close now", Toast.LENGTH_SHORT).show();
                    try {
                        this.wait(500000000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    //    finish();
                }
            }
        }
    }

    final int MY_PERMISSIONS_REQUEST_SEND_SMS = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button SendBut = findViewById(R.id.Send);
        final EditText Number = findViewById(R.id.Num);
        final EditText Messages = findViewById(R.id.message);
        SendBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Numbers = Number.getText().toString();
                String Message = Messages.getText().toString();
                ArrayList<String> Numbs = new ArrayList<>();
                if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.SEND_SMS}, MY_PERMISSIONS_REQUEST_SEND_SMS);
                }
                while (Numbers.indexOf('\n') != -1) {
                    Numbers = delimiting(Numbers, '\n');
                    Log.i("MainActivity", "The new number found is " + delimited);
                    Numbs.add(delimited);
                }
                Numbs.add(Numbers);
                Log.i("MainActivity", "The last number found is " + Numbers);

                if (Numbs.size() > 30 || Numbs.get(0).equals("") || Message.equals("")) {

                    if (Numbs.size() > 30) {
                        Toast.makeText(getBaseContext(), "Please send to max 30 recipients", Toast.LENGTH_SHORT).show();
                    } else if (Numbs.get(0).equals("")) {
                        Toast.makeText(getBaseContext(), "No recipients, Please paste some numbers", Toast.LENGTH_SHORT).show();
                    } else if (Message.equals("")) {
                        Toast.makeText(getBaseContext(), "No Message to be sent !!", Toast.LENGTH_SHORT).show();
                    }
                    Numbs.clear();
                } else {
                    for (int i = 0; i < Numbs.size(); i++) {
                        sendSMS(Numbs.get(i), Message);
                    }
                    Numbs.clear();
                    Message = "";
                }
            }
        });
    }
}
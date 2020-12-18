package com.example.ghkdw.lotterycheck;

import android.content.Intent;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String ssaid = Settings.Secure.getString(SplashActivity.this.getContentResolver(), Settings.Secure.ANDROID_ID);
        
        int status = NetworkStatus.getConnectivityStatus(SplashActivity.this);
        
        if(status == NetworkStatus.TYPE_MOBILE || status == NetworkStatus.TYPE_WIFI) {
            //Toast.makeText(this, "Network connected", Toast.LENGTH_SHORT).show();
            try {
                RegisterTask task = new RegisterTask();
                String result = task.execute("auth", ssaid).get();
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                if(result.contains("success")) {
                    String[] rArray = result.split("/");
                    intent.putExtra("session", rArray[rArray.length -1]);
                    intent.putExtra("ssaid", ssaid);
                }
                else {
                    intent.putExtra("session", "no");
                    intent.putExtra("ssaid", ssaid);
                }
                startActivity(intent);
                finish();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else {
            //Toast.makeText(this, "Network disconnected", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
            intent.putExtra("session", "disconnected");
            intent.putExtra("ssaid", ssaid);
            startActivity(intent);
        }
    }
}
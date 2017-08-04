package com.example.ghost.RemotePrototype;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    NsdHelper mNsdHelper;
    private int mPort = mNsdHelper.getmLocalPort();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent =  new Intent(MainActivity.this, ScanCentre.class);
        startActivity(intent);
    }

    @Override
    protected void onPause() {
        if (mNsdHelper != null) {
            mNsdHelper.tearDown();
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mNsdHelper != null) {
            mNsdHelper.initializeNsd();
            mNsdHelper.registerService(mPort);
            mNsdHelper.discoverServices();
        }
    }

    @Override
    protected void onDestroy() {
        mNsdHelper.tearDown();
        //mConnection.tearDown();
        super.onDestroy();
    }
}

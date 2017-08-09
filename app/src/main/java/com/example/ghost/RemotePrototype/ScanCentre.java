package com.example.ghost.RemotePrototype;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import java.net.InetAddress;

/**
 * This will scan the available server
 */

public class ScanCentre extends AppCompatActivity {

    Button connect;
    NsdHelper mNsdHelper;
    int mServerPort;
    InetAddress mServerHost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scan_centre);

        connect = (Button) findViewById(R.id.connectButton);
        connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mNsdHelper.initializeServerSocket();
                int mPort = mNsdHelper.getmLocalPort();
                mNsdHelper.initializeNsd();
                mNsdHelper.registerService(mPort);
                mNsdHelper.discoverServices();
                mServerPort = mNsdHelper.getInternetPort();
                mServerHost = mNsdHelper.getInternetHost();

                /*ConnectPhoneTask connectPhoneTask = new ConnectPhoneTask();
                connectPhoneTask.execute(Constants.SERVER_IP);
                Intent intent = new Intent(ScanCentre.this, CommandCentre.class);
                startActivity(intent);*/
            }
        });
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
            mNsdHelper.registerService(mNsdHelper.getmLocalPort());
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
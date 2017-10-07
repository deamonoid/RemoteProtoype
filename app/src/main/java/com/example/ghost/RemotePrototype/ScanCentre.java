package com.example.ghost.RemotePrototype;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;

/**
 * This will scan the available server
 */

public class ScanCentre extends AppCompatActivity {

    private Button scanbtn;
    private ListView listViewIp;

    ArrayList<String> ipList;
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scan_centre);

        listViewIp = (ListView) findViewById(R.id.listviewip);

        ipList = new ArrayList();
        adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, ipList);
        listViewIp.setAdapter(adapter);


        scanbtn = (Button) findViewById(R.id.scanButton);
        scanbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new ScanIpTask().execute();
            }
        });

        listViewIp.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String selectedFromList = (listViewIp.getItemAtPosition(i).toString());
                /*Intent intent = new Intent(ScanCentre.this, CommandCentre.class);
                intent.putExtra(EXTRA_MESSAGE, selectedFromList);
                startActivity(intent);*/
                ConnectPhoneTask connectPhoneTask = new ConnectPhoneTask(getApplicationContext());
                connectPhoneTask.execute(selectedFromList);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.scan_menu, menu);
        return true;
    }

    private class ScanIpTask extends AsyncTask<Void, String, Void> {

        /*
        Scan IP 192.168.1.100~192.168.1.110
        you should try different timeout for your network/devices
         */
        static final String subnet = "192.168.1.";
        static final int lower = 100;
        static final int upper = 110;
        static final int timeout = 5000;

        @Override
        protected void onPreExecute() {
            ipList.clear();
            adapter.notifyDataSetInvalidated();
            Toast.makeText(ScanCentre.this, "Scan IP...", Toast.LENGTH_LONG).show();
        }

        @Override
        protected Void doInBackground(Void... params) {

            for (int i = lower; i <= upper; i++) {
                String host = subnet + i;

                try {
                    InetAddress inetAddress = InetAddress.getByName(host);
                    if (inetAddress.isReachable(timeout)) {
                        publishProgress(inetAddress.toString());
                    }

                } catch (UnknownHostException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            ipList.add(values[0]);
            adapter.notifyDataSetInvalidated();
            Toast.makeText(ScanCentre.this, values[0], Toast.LENGTH_LONG).show();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Toast.makeText(ScanCentre.this, "Done", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.changePort:
                Intent intent = new Intent(this, ChangePort.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
package com.example.ghost.RemotePrototype;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

/**
 * Is used to connect to server
 */

public class ConnectPhoneTask extends AsyncTask<String,Void,Boolean> {

    private Context context;
    private Socket socket;
    private PrintWriter out;
    private boolean isConnected = false;

    protected Boolean doInBackground(String... strings) {
        boolean result = true;
        try {
            InetAddress serverAddr = InetAddress.getByName(strings[0]);
            socket = new Socket(serverAddr, Constants.SERVER_PORT);
        } catch (IOException e) {
            Log.e("Remote", "Error while connecting", e);
            result = false;
        }
        return result;
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        isConnected = aBoolean;
        //Toast.makeText(context,isConnected?"Connected to server!":"Error while connecting",Toast.LENGTH_LONG).show();
        try {
            if(isConnected) {
                out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket
                        .getOutputStream())), true);
            }
        }catch (IOException e){
            Log.e("Remote", "Error while creating OutWriter", e);
            Toast.makeText(context,"Error while connecting",Toast.LENGTH_LONG).show();
        }
    }

    public boolean getIsConnected(){
        return isConnected;
    }

    public PrintWriter getOut(){
        return out;
    }

    public Socket getSocket(){
        return socket;
    }

    public Context getContext(){
        return context;
    }
}
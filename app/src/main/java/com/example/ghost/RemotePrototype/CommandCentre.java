package com.example.ghost.RemotePrototype;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class CommandCentre extends AppCompatActivity implements View.OnClickListener {

    ImageButton homeButton;
    ImageButton menuButton;
    ImageButton backButton;
    ImageButton micButton;
    TextView mousepad;
    View slideText;
    View recordPanel;

    private static final int MIN_INTERVAL_TIME = 700;
    private static final int MAX_INTERVAL_TIME = 60000;


    private float startedDraggingY = -1;
    private float distCanMove = dp(80);
    private boolean mouseMoved = false;

    private float initX = 0;
    private float initY = 0;
    private float disX = 0;
    private float disY = 0;

    ConnectPhoneTask connectPhoneTask = new ConnectPhoneTask();
    boolean isConnected = connectPhoneTask.getIsConnected();
    Context context = connectPhoneTask.getContext();
    Socket socket = connectPhoneTask.getSocket();
    PrintWriter out = connectPhoneTask.getOut();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.cmd_centre);

        homeButton = (ImageButton) findViewById(R.id.homeButton);
        menuButton = (ImageButton) findViewById(R.id.menuButton);
        backButton = (ImageButton) findViewById(R.id.backButton);
        micButton = (ImageButton) findViewById(R.id.micButton);

        slideText = findViewById(R.id.slideText);
        recordPanel = findViewById(R.id.record_panel);

        homeButton.setOnClickListener(this);
        menuButton.setOnClickListener(this);
        backButton.setOnClickListener(this);

        mousepad = (TextView) findViewById(R.id.mousePad);

        mousepad.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (isConnected && out != null) {
                    switch (motionEvent.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            initX = motionEvent.getX();
                            initY = motionEvent.getY();
                            mouseMoved = false;
                            break;
                        case MotionEvent.ACTION_MOVE:
                            disX = motionEvent.getX() - initX;
                            disY = motionEvent.getY() - initY;
                            initX = motionEvent.getX();
                            initY = motionEvent.getY();
                            if (disX != 0 || disY != 0) {
                                out.println(disX + "," + disY);
                            }
                            mouseMoved = true;
                            break;
                        case MotionEvent.ACTION_UP:
                            if (!mouseMoved) {
                                out.println("Left Click");
                            }
                    }
                }
                return true;
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.homeButton:
                if (isConnected && out != null) {
                    out.println("Home button");
                }
                break;
            case R.id.menuButton:
                if (isConnected && out != null) {
                    out.println("Menu button");
                }
                break;
            case R.id.backButton:
                if (isConnected && out != null) {
                    out.println("Back button");
                }
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isConnected && out != null) {
            try {
                out.println("exit");
                socket.close();
            } catch (IOException e) {
                Log.e("Remote", "Error in closing socket", e);
            }
        }
    }

    private void startRecord() {
        Toast.makeText(this, "Recording started", Toast.LENGTH_SHORT).show();
    }

    private void stopRecord() {
        Toast.makeText(this, "Recording stopped", Toast.LENGTH_SHORT).show();
    }

    public static int dp(float value) {
        return (int) Math.ceil(1 * value);
    }
}


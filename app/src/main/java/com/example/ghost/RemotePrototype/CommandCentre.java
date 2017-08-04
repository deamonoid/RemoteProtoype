package com.example.ghost.RemotePrototype;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

import ui.ViewProxy;

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
    private RecordButtonUtil mAudioUtil;


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

        micButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) slideText.getLayoutParams();
                    params.bottomMargin = dp(30);
                    slideText.setLayoutParams(params);
                    ViewProxy.setAlpha(slideText, 1);
                    startedDraggingY = -1;
                    startRecord();
                    micButton.getParent().requestDisallowInterceptTouchEvent(true);
                    recordPanel.setVisibility(View.VISIBLE);
                } else if (motionEvent.getAction() == MotionEvent.ACTION_UP || motionEvent.getAction() == MotionEvent.ACTION_CANCEL) {
                    startedDraggingY = -1;
                    stopRecord();
                } else if (motionEvent.getAction() == MotionEvent.ACTION_MOVE) {
                    float y = motionEvent.getY();
                    if (y < -distCanMove) {
                        stopRecord();
                    }
                    y = y + ViewProxy.getY(micButton);
                    FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) slideText.getLayoutParams();
                    if (startedDraggingY != -1) {
                        float dist = (y - startedDraggingY);
                        params.bottomMargin = dp(30) + (int) dist;
                        slideText.setLayoutParams(params);
                        float alpha = 1.0f + dist / distCanMove;
                        if (alpha > 1) {
                            alpha = 1;
                        } else if (alpha < 0) {
                            alpha = 0;
                        }
                        ViewProxy.setAlpha(slideText, alpha);
                    }
                    if (y <= ViewProxy.getY(slideText) + slideText.getHeight()
                            + dp(30)) {
                        if (startedDraggingY == -1) {
                            startedDraggingY = y;
                            distCanMove = (recordPanel.getMeasuredHeight()
                                    - slideText.getMeasuredHeight() - dp(48)) / 2.0f;
                            if (distCanMove <= 0) {
                                distCanMove = dp(80);
                            } else if (distCanMove > dp(80)) {
                                distCanMove = dp(80);
                            }
                        }
                    }
                    if (params.bottomMargin > dp(30)) {
                        params.bottomMargin = dp(30);
                        slideText.setLayoutParams(params);
                        ViewProxy.setAlpha(slideText, 1);
                        startedDraggingY = -1;
                    }
                }
                view.onTouchEvent(motionEvent);
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
        mAudioUtil.recordAudio();
    }

    private void stopRecord() {
        Toast.makeText(this, "Recording stopped", Toast.LENGTH_SHORT).show();
        mAudioUtil.stopRecord();
    }

    public static int dp(float value) {
        return (int) Math.ceil(1 * value);
    }
}


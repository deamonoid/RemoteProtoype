package com.example.ghost.RemotePrototype;

import android.content.Context;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
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

    private static final String AUDIO_RECORDER_FILE_EXT_3GP = ".3gp";
    private static final String AUDIO_RECORDER_FOLDER = "AudioRecorder";
    private MediaRecorder recorder = null;
    private boolean mouseMoved = false;
    private static final String TAG = CommandCentre.class.getSimpleName();

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
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        AppLog.logString("Start Recording");
                        Toast.makeText(getBaseContext(), "Recordng started", Toast.LENGTH_SHORT).show();
                        startRecording();
                        break;
                    case MotionEvent.ACTION_UP:
                        AppLog.logString("stop Recording");
                        Toast.makeText(getBaseContext(), "Recording stopped", Toast.LENGTH_SHORT).show();
                        stopRecording();
                        break;
                }
                return false;
            }
        });

    }

    private String getFilename() {
        String filepath = Environment.getExternalStorageDirectory().getPath();
        File file = new File(filepath, AUDIO_RECORDER_FOLDER);

        if (!file.exists()) {
            file.mkdirs();
        }

        return (file.getAbsolutePath() + "/" + System.currentTimeMillis() + AUDIO_RECORDER_FILE_EXT_3GP);
    }

    private void startRecording() {
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        recorder.setOutputFile(getFilename());
        recorder.setOnErrorListener(errorListener);
        recorder.setOnInfoListener(infoListener);

        try {
            recorder.prepare();
            recorder.start();
        } catch (IllegalStateException | IOException e) {
            e.printStackTrace();
        }
    }

    private void stopRecording() {
        if (null != recorder) {
            recorder.stop();
            recorder.reset();
            recorder.release();
            sendFile();
            recorder = null;
        }
    }

    public void sendFile() {

        try {

            String filepath = Environment.getExternalStorageDirectory().getPath();
            File file = new File(filepath, AUDIO_RECORDER_FOLDER);

            File f = new File(file.getAbsolutePath() + "/" + System.currentTimeMillis() + AUDIO_RECORDER_FILE_EXT_3GP);
            byte[] bytes = new byte[(int) f.length()];
            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(f));
            bis.read(bytes, 0, bytes.length);

            OutputStream outputStream = socket.getOutputStream();
            outputStream.write(bytes, 0, bytes.length);
            outputStream.flush();
            socket.close();

        } catch (IOException e) {
            Log.e(TAG, "Error in sending files", e);
        }
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

    private MediaRecorder.OnErrorListener errorListener = new MediaRecorder.OnErrorListener() {
        @Override
        public void onError(MediaRecorder mr, int what, int extra) {
            AppLog.logString("Error: " + what + ", " + extra);
        }
    };

    private MediaRecorder.OnInfoListener infoListener = new MediaRecorder.OnInfoListener() {
        @Override
        public void onInfo(MediaRecorder mr, int what, int extra) {
            AppLog.logString("Warning: " + what + ", " + extra);
        }
    };

    public static int dp(float value) {
        return (int) Math.ceil(1 * value);
    }
}


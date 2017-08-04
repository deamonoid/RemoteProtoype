package com.example.ghost.RemotePrototype;

import android.media.MediaRecorder;
import android.os.Environment;

import java.io.IOException;

/**
 * Utility Class for recording
 */

public class RecordButtonUtil {
    public static final String AUDIO_DIR = Environment
            .getExternalStorageDirectory().getAbsolutePath() + "/ProjectX/audio"; // audio file path

    private String mAudioPath = AUDIO_DIR + "/audiorecordtest.3gp";
    private boolean mIsRecording;
    MediaRecorder mRecorder;

    // initialize the recorder
    private void initRecorder() {
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        mRecorder.setOutputFile(mAudioPath);
        mIsRecording = true;
    }

    /**
     * start recording and save the file
     */
    public void recordAudio() {
        initRecorder();
        try {
            mRecorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mRecorder.start();
    }

    /**
     * get the volumne
     */
    public int getVolumn() {
        int volumn = 0;
        // recording
        if (mRecorder != null && mIsRecording) {
            volumn = mRecorder.getMaxAmplitude();
            if (volumn != 0)
                volumn = (int) (10 * Math.log(volumn) / Math.log(10)) / 7;
        }
        return volumn;
    }

    /**
     * stop recording
     */
    public void stopRecord() {
        if (mRecorder != null) {
            mRecorder.stop();
            mRecorder.release();
            mRecorder = null;
            mIsRecording = false;
        }
    }
}

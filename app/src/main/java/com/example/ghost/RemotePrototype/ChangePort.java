package com.example.ghost.RemotePrototype;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by Tanmay on 07/10/17.
 */

public class ChangePort extends AppCompatActivity {

    private EditText changePort;
    private Button submit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.menu_changeport);

        changePort = (EditText) findViewById(R.id.changePort_portNumber);
        submit = (Button) findViewById(R.id.changePort_submitButton);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changePortNumber();
            }
        });
    }

    private void changePortNumber() {
        int portNumber;
        portNumber = Integer.parseInt(changePort.getText().toString());

        if (changePort.getText().toString().equals("")) {
            Toast.makeText(this, "Enter your Port Number", Toast.LENGTH_SHORT).show();
        } else {
            Constants.SERVER_PORT = portNumber;
            finish();
        }
    }
}

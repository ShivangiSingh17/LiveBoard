package com.example.livewhiteboard;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class JoinNewSession extends AppCompatActivity {

    Socket socket;

    EditText etJoinSessionId;
    Button btnJoin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_new_session);
        socket = ((SocketHandler) getApplication()).getSocket();

        etJoinSessionId = findViewById(R.id.etJoinSessionId);
        btnJoin = findViewById(R.id.btnJoin);

        btnJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (etJoinSessionId.getText().toString().trim().equalsIgnoreCase("")) {
                    etJoinSessionId.setError("This field cannot be blank");
                }
                try {
                    JSONObject jsonObjectToSend = new JSONObject();
                    jsonObjectToSend.put("sessionId", etJoinSessionId.getText().toString().trim());
                    socket.connect();
                    socket.emit("joinSession", jsonObjectToSend);
                    socket.on("joinedSession", new Emitter.Listener() {
                        @Override
                        public void call(Object... args) {
                            JSONObject jsonObject = (JSONObject) args[0];
                            try {
                                if (jsonObject.getBoolean("success")) {
                                    Intent i = new Intent(JoinNewSession.this, Drawing.class);
                                    i.putExtra("sessionId", jsonObject.getString("sessionId"));
                                    startActivity(i);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                //send request to the server and it is successful send intent to teh canvas activity.
            }
        });
    }
}

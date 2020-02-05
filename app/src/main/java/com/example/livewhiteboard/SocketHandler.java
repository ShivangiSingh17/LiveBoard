package com.example.livewhiteboard;

import android.app.Application;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;
public class SocketHandler extends Application {
    private Socket socket;

    {
        try {
            socket = IO.socket("http://192.168.43.34:4000/");
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public Socket getSocket() {
        return socket;
    }
}

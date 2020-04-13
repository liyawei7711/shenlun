package com.example.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.services.androidService;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class SecondActivity extends Activity {
    public static int PHONE_PORT = 22222;
    private static final String TAG = "ServerThread";
    ServerThread serverThread;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Toast.makeText(getApplicationContext(), msg.getData().getString("MSG", "Toast"), Toast.LENGTH_SHORT).show();
            tv_txt.setText(msg.getData().getString("MSG", "Toast") + "");
        }
    };

    TextView tv_txt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        tv_txt = findViewById(R.id.tv_txt);
        Intent intent = new Intent(this, androidService.class);
        startService(intent);
//        serverThread = new ServerThread();
//        serverThread.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        serverThread.setIsLoop(false);
    }

    class ServerThread extends Thread {
        boolean isLoop = true;

        public void setIsLoop(boolean isLoop) {
            this.isLoop = isLoop;
        }

        @Override
        public void run() {
            System.out.println("cccccccccccccccccccccccc running");
            ServerSocket serverSocket = null;
            try {
                serverSocket = new ServerSocket(PHONE_PORT);
                while (isLoop) {
                    Socket socket = serverSocket.accept();
                    System.out.println("cccccccccccccccccccccccc accept");
                    DataInputStream inputStream = new DataInputStream(socket.getInputStream());
                    DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
                    String msg = inputStream.readUTF();
                    Message message = Message.obtain();
                    Bundle bundle = new Bundle();
                    bundle.putString("MSG", msg);
                    message.setData(bundle);
                    handler.sendMessage(message);
                    socket.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                System.out.println("cccccccccccccccccccccccc destory");
                if (serverSocket != null) {
                    try {
                        serverSocket.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

}

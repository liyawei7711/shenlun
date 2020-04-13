package com.example.libddd;

import com.android.ddmlib.AdbCommandRejectedException;
import com.android.ddmlib.IDevice;
import com.android.ddmlib.TimeoutException;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class MainClss2 {
    public static int PC_LOCAL_PORT = 22222;
    public static int PHONE_PORT = 22222;
    private static ADB mADB;
    private static IDevice[] mDevices;
    private static IDevice mDevice;
    static Socket socket;

    public static void main(String[] args) {
        mADB = new ADB();
        mADB.initialize();
        mDevices = mADB.getDevices();
        mDevice = mDevices[0];//取第一个连接的设备
        try {
            mDevice.createForward(PC_LOCAL_PORT, PHONE_PORT);//映射端口
        } catch (TimeoutException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (AdbCommandRejectedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        System.out.println("任意字符, 回车键发送Toast");
        Scanner scanner = new Scanner(System.in);
        while (true) {
            String msg = scanner.next();
            initializeConnection(msg);
        }
//        initializeConnection();//socket数据传输
    }

    private static void initializeConnection(String str) {
        // Create socket connection
        try {
            socket = new Socket("127.0.0.1", PC_LOCAL_PORT);
            OutputStream outputStream = socket.getOutputStream();
            outputStream.write("llaalal".getBytes());
            outputStream.close();
            socket.close();
        } catch (UnknownHostException e) {
            System.err.println("Socket connection problem (Unknown host)"
                    + e.getStackTrace());
            e.printStackTrace();
        } catch (IOException e) {
            System.err.println("Could not initialize I/O on socket");
            e.printStackTrace();
        }
    }

}

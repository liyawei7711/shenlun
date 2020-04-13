package com.example.myapplication.services;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.Charset;

import android.content.Context;
import android.util.Log;


/**
 * 功能：用于socket的交互
 *
 * @author wufenglong
 *
 */
public class ThreadReadWriterIOSocket implements Runnable {
    private Socket client;
    private Context context;

    ThreadReadWriterIOSocket(Context context, Socket client) {

        this.client = client;
        this.context = context;
    }

    @Override
    public void run() {
        System.out.println("ccccccccccccccccccc   "+Thread.currentThread().getName() + "---->"
                + "a client has connected to server!");
        BufferedOutputStream out;
        BufferedInputStream in;
        try {
            /* PC端发来的数据msg */
            String currCMD = "";
            out = new BufferedOutputStream(client.getOutputStream());
            in = new BufferedInputStream(client.getInputStream());
            // testSocket();// 测试socket方法
            androidService.ioThreadFlag = true;
            while (androidService.ioThreadFlag) {
                try {
                    if (!client.isConnected()) {
                        break;
                    }

                    /* 接收PC发来的数据 */
                    System.out.println("ccccccccccccccccccc   "+ Thread.currentThread().getName()
                            + "---->" + "will read...... "+in.toString());
                    /* 读操作命令 */
                    readStream(in);
                    currCMD = readCMDFromSocket(in);
                    System.out.println("ccccccccccccccccccc   "+ Thread.currentThread().getName()
                            + "---->" + "**currCMD ==== " + currCMD);

                    /* 根据命令分别处理数据 */
                    if (currCMD.equals("1")) {
                        out.write("OK".getBytes());
                        out.flush();
                    } else if (currCMD.equals("2")) {
                        out.write("OK".getBytes());
                        out.flush();
                    } else if (currCMD.equals("3")) {
                        out.write("OK".getBytes());
                        out.flush();
                    } else if (currCMD.equals("4")) {
                        /* 准备接收文件数据 */
                        try {
                            out.write("service receive OK".getBytes());
                            out.flush();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        /* 接收文件数据，4字节文件长度，4字节文件格式，其后是文件数据 */
                        byte[] filelength = new byte[4];
                        byte[] fileformat = new byte[4];
                        byte[] filebytes = null;

                        /* 从socket流中读取完整文件数据 */
                        filebytes = receiveFileFromSocket(in, out, filelength,
                                fileformat);

                        // Log.v(Service139.TAG, "receive data =" + new
                        // String(filebytes));
                        try {
                            /* 生成文件 */
                            File file = FileHelper.newFile("R0013340.JPG");
                            FileHelper.writeFile(file, filebytes, 0,
                                    filebytes.length);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else if (currCMD.equals("exit")) {

                    }
                } catch (Exception e) {
                    // try {
                    // out.write("error".getBytes("utf-8"));
                    // out.flush();
                    // } catch (IOException e1) {
                    // e1.printStackTrace();
                    // }
                    System.out.println("ccccccccccccccccccc   "+ Thread.currentThread().getName()
                            + "---->" + "read write error111111");
                }
            }
            out.close();
            in.close();
        } catch (Exception e) {
            System.out.println("ccccccccccccccccccc   "+ Thread.currentThread().getName()
                    + "---->" + "read write error222222");
            e.printStackTrace();
        } finally {
            try {
                if (client != null) {
                    System.out.println("ccccccccccccccccccc   "+ Thread.currentThread().getName()
                            + "---->" + "client.close()");
                    client.close();
                }
            } catch (IOException e) {
                System.out.println("ccccccccccccccccccc   "+ Thread.currentThread().getName()
                        + "---->" + "read write error333333");
                e.printStackTrace();
            }
        }
    }

    /**
     * 功能：从socket流中读取完整文件数据
     *
     * InputStream in：socket输入流
     *
     * byte[] filelength: 流的前4个字节存储要转送的文件的字节数
     *
     * byte[] fileformat：流的前5-8字节存储要转送的文件的格式（如.apk）
     *
     * */
    public static byte[] receiveFileFromSocket(InputStream in,
                                               OutputStream out, byte[] filelength, byte[] fileformat) {
        byte[] filebytes = null;// 文件数据
        try {
            in.read(filelength);// 读文件长度
            int filelen = MyUtil.bytesToInt(filelength);// 文件长度从4字节byte[]转成Int
            String strtmp = "read file length ok:" + filelen;
            out.write(strtmp.getBytes("utf-8"));
            out.flush();

            filebytes = new byte[filelen];
            int pos = 0;
            int rcvLen = 0;
            while ((rcvLen = in.read(filebytes, pos, filelen - pos)) > 0) {
                pos += rcvLen;
            }
            System.out.println("ccccccccccccccccccc   "+ Thread.currentThread().getName()
                    + "---->" + "read file OK:file size=" + filebytes.length);
            out.write("read file ok".getBytes("utf-8"));
            out.flush();
        } catch (Exception e) {
            System.out.println("ccccccccccccccccccc   "+ Thread.currentThread().getName()
                    + "---->" + "receiveFileFromSocket error");
            e.printStackTrace();
        }
        return filebytes;
    }

    public void readStream(InputStream inStream) {
        byte[] data = new byte[1024];
        StringBuffer sb = new StringBuffer();
        int length = 0;
        try{
            System.out.println("ccccccccccccccccccccccccccccccccc0   shoudao ");
            while ((length = inStream.read(data)) != -1) {
                String s = new String(data,0,length, Charset.forName("utf-8"));
                //Log.debug("Http.get", s);
                sb.append(s);
            }
            String message = sb.toString();
            System.out.println("ccccccccccccccccccccccccccccccccc "+message + "   shoudao ");
        }catch (Exception e){
            System.out.println("ccccccccccccccccccccccccccccccccc eee0 "+e.getMessage());
            try {
                inStream.close();
            } catch (IOException ex) {
                System.out.println("ccccccccccccccccccccccccccccccccc eee1 "+ex.getMessage());
            }
        }


    }

    /* 读取命令 */
    public static String  readCMDFromSocket(InputStream in) {
        int MAX_BUFFER_BYTES = 2048;
        String msg = "";
        byte[] tempbuffer = new byte[MAX_BUFFER_BYTES];
        try {
            int numReadedBytes = in.read(tempbuffer, 0, tempbuffer.length);
            System.out.println("ccccccccccccccccccc   "+ Thread.currentThread().getName()
                    + "---->" + "readFromSocket error msgmsg111 "+msg);
            msg = new String(tempbuffer, 0, numReadedBytes, "utf-8");
            tempbuffer = null;
            System.out.println("ccccccccccccccccccc   "+ Thread.currentThread().getName()
                    + "---->" + "readFromSocket error msgmsg222 "+msg);
        } catch (Exception e) {
            System.out.println("ccccccccccccccccccc   "+ Thread.currentThread().getName()
                    + "---->" + "readFromSocket error333 "+e);
            e.printStackTrace();
        }
        // Log.v(Service139.TAG, "msg=" + msg);
        return msg;
    }
}

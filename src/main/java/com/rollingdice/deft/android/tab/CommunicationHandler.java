package com.rollingdice.deft.android.tab;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by koushikpal on 30/09/15.
 */
public class CommunicationHandler extends HandlerThread {

    private static Handler handler;
    private Context context;
    DatabaseReference localRef=GlobalApplication.firebaseRef;

    private boolean communicationHandalerConnected;

    Socket echoSocket = null;
    PrintWriter out = null;
    BufferedReader in = null;

    private ConcurrentHashMap<String, String> response = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, Boolean> responsListner = new ConcurrentHashMap<>();


    String serverHostname;
    int port;

    public CommunicationHandler(String name) {
        super(name);
    }

    public CommunicationHandler(Context context, String name, String serverHostname, int port) {
        super(name);
        this.context = context;
        this.serverHostname = serverHostname;
        this.port = port;
    }

    @Override
    protected void onLooperPrepared() {
        super.onLooperPrepared();
        initSocket();
        prepareHandler();
    }

    private void prepareHandler() {
        handler = new Handler(getLooper()) {

            public void handleMessage(Message msg) {

               // responseready = false;
                if (getEchoSocket() != null) {
                    if (!getEchoSocket().isConnected()) {
                        initSocket();
                    }
                    if (out != null && in != null) {

                        out.println(msg.getData().getString("msg"));

                        try {
                            response.put(msg.getData().getString("msg"), "");
                            response.put(msg.getData().getString("msg"), in.readLine());
                            Thread.sleep(200);
                            responsListner.put(msg.getData().getString("msg"), true);

                            //Toast.makeText(context, "Response from " + serverHostname +" is " +in.readLine(),Toast.LENGTH_SHORT).show();
                        } catch (SocketTimeoutException ex)
                        {
                            response.put(msg.getData().getString("msg"), msg.getData().getString("msg"));
                            responsListner.put(msg.getData().getString("msg"), true);
                            try {
                                getEchoSocket().close();
                                getLooper().quit();
                                Toast.makeText(context, "No response from Master Controller.Please restart the MasterController and restart the service", Toast.LENGTH_LONG).show();
                                // to be tested
                                //initSocket();

                            } catch (IOException e) {
                                try {
                                    getEchoSocket().close();
                                } catch (IOException e1) {
                                    e1.printStackTrace();
                                }
                                getLooper().quit();
                                e.printStackTrace();
                            }

                        } catch (IOException e) {
                            Toast.makeText(context, "No response from " + serverHostname, Toast.LENGTH_SHORT).show();
                            //e.printStackTrace();

                        }
                        catch(Exception e)
                        {
                            try {
                                getEchoSocket().close();
                            } catch (IOException e1) {
                                e1.printStackTrace();
                            }
                            getLooper().quit();
                            Toast.makeText(context, "Handler Exception from " + serverHostname, Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    Toast.makeText(context, "Not Connected to any Server", Toast.LENGTH_SHORT).show();
                    communicationHandalerConnected =false;
                }
            }
        };
    }

    private void initSocket() {
        try {
            echoSocket = new Socket();
            echoSocket.connect(new InetSocketAddress(serverHostname, port), 8000);
            out = new PrintWriter(echoSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(echoSocket.getInputStream()));
            echoSocket.setKeepAlive(true);
            echoSocket.setSoTimeout(8000);
            Toast.makeText(context, "Connected to host: " + serverHostname + " "+ port, Toast.LENGTH_SHORT).show();
            DatabaseReference state=localRef.child("servicestate").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
            state.setValue(true);
            communicationHandalerConnected =true;
        } catch (SocketTimeoutException e) {
            e.printStackTrace();
            Toast.makeText(context, "Socket timeout to host: " + serverHostname +" on port "+port, Toast.LENGTH_SHORT).show();
            communicationHandalerConnected =false;
        } catch (UnknownHostException e) {
            e.printStackTrace();
            communicationHandalerConnected =false;
              Toast.makeText(context, "Don't know about host: " + serverHostname, Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            communicationHandalerConnected =false;
            e.printStackTrace();
                Toast.makeText(context, "Couldn't get I/O for " + "the connection to: " + serverHostname+port,Toast.LENGTH_SHORT).show();
        }
        catch (Exception e)
        {
            Toast.makeText(context, " "+ serverHostname+" "+port,Toast.LENGTH_SHORT).show();

        }
    }

    /*@Override
    public void dispatchMessage(Message msg) {
        // catch any Exception
        try {
            handler.dispatchMessage(msg);
        } catch (Exception e) {
            Toast.makeText(context, "NOoo " + serverHostname,Toast.LENGTH_SHORT).show();
        }
    }*/

    public Handler getCommhandler() {
        return handler;
    }

    public Socket getEchoSocket() {
        return echoSocket;
    }



    public ConcurrentHashMap<String, String> getResponse() {
        return response;
    }

    public ConcurrentHashMap<String, Boolean> getResponsListner() {
        return responsListner;
    }

    public void setResponsListner(ConcurrentHashMap<String, Boolean> responsListner) {
        this.responsListner = responsListner;
    }

    public boolean isCommunicationHandalerConnected() {
        return communicationHandalerConnected;
    }

    public void setCommunicationHandalerConnected(boolean communicationHandalerConnected) {
        this.communicationHandalerConnected = communicationHandalerConnected;
    }
}

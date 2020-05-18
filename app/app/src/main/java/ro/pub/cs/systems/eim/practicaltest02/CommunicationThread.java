package ro.pub.cs.systems.eim.practicaltest02;


import android.util.Log;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.NameValuePair;
import cz.msebera.android.httpclient.client.ClientProtocolException;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.ResponseHandler;
import cz.msebera.android.httpclient.client.entity.UrlEncodedFormEntity;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.impl.client.BasicResponseHandler;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.message.BasicNameValuePair;
import cz.msebera.android.httpclient.protocol.HTTP;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CommunicationThread extends Thread {

    private Socket socket;
    private ServerThread serverThread;

    public CommunicationThread(ServerThread serverThread, Socket socket) {
        this.socket = socket;
        this.serverThread = serverThread;
    }

    @Override
    public void run() {
        try {
            Log.v(Constants.TAG, "Connection opened to " + socket.getLocalAddress() + ":" + socket.getLocalPort() + " from " + socket.getInetAddress());
            if (socket == null) {
                Log.e(Constants.TAG, "[COMMUNICATION THREAD] Socket is null!");
                return;
            }

            BufferedReader bufferedReader = Utilities.getReader(socket);
            PrintWriter printWriter = Utilities.getWriter(socket);
            if (bufferedReader == null || printWriter == null) {
                Log.e(Constants.TAG, "[COMMUNICATION THREAD] Buffered Reader / Print Writer are null!");
                return;
            }

            String informationType = bufferedReader.readLine();
            if (informationType == null || informationType.isEmpty()) {
                Log.e(Constants.TAG, "[COMMUNICATION THREAD] Error receiving parameters from client (city / information type!");
                return;
            }

            Log.e(Constants.TAG, informationType);

            HashMap<String, BitcoinInfo> data = serverThread.getData();
            BitcoinInfo bitcoinInfo = null;

            if (data.containsKey(informationType)) {
                Log.i(Constants.TAG, "[COMMUNICATION THREAD] Getting the information from the cache...");
                bitcoinInfo = data.get(informationType);
            }

            if (bitcoinInfo != null) {
                //send the result to the client
                switch (informationType) {
                    case "EUR":
                        String result = bitcoinInfo.EURvalue;
                        printWriter.println(result);
                        printWriter.flush();
                        break;
                    case "USD":
                        result = bitcoinInfo.USDvalue;
                        printWriter.println(result);
                        printWriter.flush();
                        break;
                }
            }

        } catch (IOException ioException) {
            Log.e(Constants.TAG, "[COMMUNICATION THREAD] An exception has occurred: " + ioException.getMessage());
            if (Constants.DEBUG) {
                ioException.printStackTrace();
            }
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException ioException) {
                    Log.e(Constants.TAG, "[COMMUNICATION THREAD] An exception has occurred: " + ioException.getMessage());
                    if (Constants.DEBUG) {
                        ioException.printStackTrace();
                    }
                }
            }
        }
    }
}


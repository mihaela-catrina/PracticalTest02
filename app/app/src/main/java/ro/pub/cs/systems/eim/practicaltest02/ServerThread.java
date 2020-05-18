package ro.pub.cs.systems.eim.practicaltest02;

import android.util.Log;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.ResponseHandler;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.impl.client.BasicResponseHandler;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;


public class ServerThread extends Thread {

    private boolean isRunning;

    private ServerSocket serverSocket;
    private int port;
    private HashMap data;
    Runnable taskRunnable = new Runnable() {
        public void run() {
            Log.v("ceva", "ceva");

            try {
                HttpClient httpClient = new DefaultHttpClient();
                HttpGet httpGet = new HttpGet(Constants.PAGE_INTERNET_ADDRESS
                        + "/" + "EUR" + ".json");

                ResponseHandler<String> responseHandler = new BasicResponseHandler();
                String EURPage = httpClient.execute(httpGet, responseHandler);
                httpGet = new HttpGet(Constants.PAGE_INTERNET_ADDRESS
                        + "/" + "USD" + ".json");
                responseHandler = new BasicResponseHandler();
                String USDPage = httpClient.execute(httpGet, responseHandler);
                Log.v("ceva", USDPage);
                Log.v("ceva", EURPage);

                JSONObject pageContentusd = new JSONObject(USDPage);
                String updated = pageContentusd.getJSONObject("time").getString("updated");
                String USDvalue = pageContentusd.getJSONObject("bpi").getJSONObject("USD").getString("rate_float");

                JSONObject pageContentEUR = new JSONObject(EURPage);
                String EURvalue = pageContentEUR.getJSONObject("bpi").getJSONObject("EUR").getString("rate_float");

                BitcoinInfo info = new BitcoinInfo(updated, USDvalue, EURvalue);
                setData("EUR", info);
                setData("USD", info);

            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
        }
    };

    public ServerThread(int port) {
        this.port = port;
        data = new HashMap<String, BitcoinInfo>();
    }

    public void startServer() {
        isRunning = true;
        start();
        ScheduledExecutorService exec = Executors.newScheduledThreadPool(1);
        exec.scheduleAtFixedRate(taskRunnable , 0, 1, TimeUnit.MINUTES);

        Log.v(Constants.TAG, "startServer() method invoked " + serverSocket);
    }

    public void stopServer() {
        isRunning = false;
        try {
            if (serverSocket != null) {
                serverSocket.close();
            }
        } catch (IOException ioException) {
            Log.e(Constants.TAG, "An exception has occurred: " + ioException.getMessage());
            if (Constants.DEBUG) {
                ioException.printStackTrace();
            }
        }
        Log.v(Constants.TAG, "stopServer() method invoked");
    }

    public synchronized void setData(String updated, BitcoinInfo bitcoinInfo) {
        this.data.put(updated, bitcoinInfo);
    }

    public synchronized HashMap<String, BitcoinInfo> getData() {
        return data;
    }

    @Override
    public void run() {
        try {
            serverSocket = new ServerSocket(port);
            while (isRunning) {
                Socket socket = serverSocket.accept();
                Log.v(Constants.TAG, "Connection opened with " + socket.getInetAddress() + ":" + socket.getLocalPort());

                if (socket != null) {
                    CommunicationThread communicationThread = new CommunicationThread(this, socket);
                    communicationThread.start();
                }
            }
        } catch (IOException ioException) {
            Log.e(Constants.TAG, "An exception has occurred: " + ioException.getMessage());
            if (Constants.DEBUG) {
                ioException.printStackTrace();
            }
        }
    }

}


package ro.pub.cs.systems.eim.practicaltest02;

import android.os.AsyncTask;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientAsyncTask extends AsyncTask<String, String, Void> {

    private TextView resultTextView;
    Socket socket;

    public ClientAsyncTask(TextView resultTextView) {
        this.resultTextView = resultTextView;
    }

    @Override
    protected Void doInBackground(String... params) {
        try {

            socket = new Socket(params[0], Integer.valueOf(params[1]));
            if (socket == null) {
                return null;
            }

            PrintWriter printWriter = Utilities.getWriter(socket);
            BufferedReader bufferedReader = Utilities.getReader(socket);

            printWriter.println(params[2]);
            printWriter.flush();


            String currentLine;
            while ((currentLine = bufferedReader.readLine()) != null) {
                publishProgress(currentLine);
            }

        } catch (Exception exception) {
            exception.printStackTrace();
        }finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException ioException) {
                    ioException.printStackTrace();

                }
            }
        }
        return null;
    }

    @Override
    protected void onPreExecute() {
        resultTextView.setText("");
    }

    @Override
    protected void onProgressUpdate(String... progress) {
        resultTextView.append(progress[0]+ '\n');
    }

    @Override
    protected void onPostExecute(Void result) {}

}

package ro.pub.cs.systems.eim.practicaltest02;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class PracticalTest02MainActivity extends AppCompatActivity {

    EditText serverPortEditText, clientAddressEditText, clientPortEditText;
    Button serverConnect, clientGetInfo;
    Spinner informationTypeSpinner;
    ServerThread serverThread;
    ClientAsyncTask clientThread;
    TextView bitcoinResultTxxtView;

    private class ConnectButtonClickListener implements Button.OnClickListener {

        @Override
        public void onClick(View view) {
            String serverPort = serverPortEditText.getText().toString();
            if (serverPort == null || serverPort.isEmpty()) {
                Toast.makeText(getApplicationContext(), "[MAIN ACTIVITY] Server port should be filled!", Toast.LENGTH_SHORT).show();
                return;
            }
            serverThread = new ServerThread(Integer.parseInt(serverPort));
            serverThread.startServer();
        }

    }


    private class GetInfoClickListener implements Button.OnClickListener {

        @Override
        public void onClick(View view) {
            String clientAddress = clientAddressEditText.getText().toString();
            String clientPort = clientPortEditText.getText().toString();
            if (clientAddress == null || clientAddress.isEmpty()
                    || clientPort == null || clientPort.isEmpty()) {
                Toast.makeText(getApplicationContext(), "[MAIN ACTIVITY] Client connection parameters should be filled!", Toast.LENGTH_SHORT).show();
                return;
            }
            if (serverThread == null || !serverThread.isAlive()) {
                Toast.makeText(getApplicationContext(), "[MAIN ACTIVITY] There is no server to connect to!", Toast.LENGTH_SHORT).show();
                return;
            }

            String informationType = informationTypeSpinner.getSelectedItem().toString();
            if (informationType == null || informationType.isEmpty()) {
                Toast.makeText(getApplicationContext(), "[MAIN ACTIVITY] Parameters from client (city / information type) should be filled", Toast.LENGTH_SHORT).show();
                return;
            }

            bitcoinResultTxxtView.setText("");

            clientThread = new ClientAsyncTask(bitcoinResultTxxtView);
            clientThread.execute(clientAddress, clientPort, informationType);
        }

    }


    ConnectButtonClickListener connectButtonClickListener = new ConnectButtonClickListener();
    GetInfoClickListener getInfoButtonListener = new GetInfoClickListener();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.practical_test02_main);

        serverPortEditText = findViewById(R.id.server_port_edit_text);
        serverConnect = findViewById(R.id.connect_button);

        clientPortEditText = (EditText) findViewById(R.id.client_port_edit_text);
        clientAddressEditText = (EditText) findViewById(R.id.client_address_edit_text);
        informationTypeSpinner = findViewById(R.id.information_type_spinner);
        clientGetInfo = findViewById(R.id.get_weather_forecast_button);
        bitcoinResultTxxtView = findViewById(R.id.weather_forecast_text_view);

        clientGetInfo.setOnClickListener(getInfoButtonListener);
        serverConnect.setOnClickListener(connectButtonClickListener);
    }

    @Override
    protected void onDestroy() {
        if (serverThread != null) {
            serverThread.stopServer();
        }
        super.onDestroy();
    }
}

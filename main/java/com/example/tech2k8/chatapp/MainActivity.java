package com.example.tech2k8.chatapp;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.IOException;
import java.util.Arrays;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private BluetoothAdapter bluetoothAdapter;
    private EditText btName;
    private Button server,client,send;
    private String MY_UUID="e92b29c8-2cf5-11e9-b210-d663bd873d93";
    private BluetoothDevice pairedBltDevice;
    private EditText message;
    private String TAG="MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btName=findViewById(R.id.bt_name);
        server=findViewById(R.id.server);
        client=findViewById(R.id.client);

        message=findViewById(R.id.message);
        send=findViewById(R.id.send);

        checkBluetoothAvailability();

        server.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    StartAsServer startAsServer =new StartAsServer();
                    startAsServer.run();
            }
        });

        client.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (checkBluetoothAvailability())
                {
                    pairDevice();

                }

            }
        });

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendText();
            }
        });
    }


    private boolean checkBluetoothAvailability()
    {
        bluetoothAdapter =BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter==null)
        {
            return false;
        }
        else
        {
            return true;
        }

    }


    private void pairDevice()
    {
        Set<BluetoothDevice> pairedDvices=bluetoothAdapter.getBondedDevices();
        for(BluetoothDevice device : pairedDvices)
        {
            Log.i(TAG,"bt name "+device.getName());
          if (device.getName().equals(btName.getText().toString()))
          {
              pairedBltDevice=device;
              new startAsClient().run();
          }
        }
    }
    BluetoothServerSocket globalServerSocket;
    class  StartAsServer extends Thread
    {

        @Override
        public void run() {
            super.run();

            Log.i(TAG,"start as server");
            BluetoothServerSocket serverSocket= null;
            try {
                Log.i(TAG,"waiting for socket connection");
                serverSocket = bluetoothAdapter.listenUsingInsecureRfcommWithServiceRecord("ChatApp", UUID.fromString(MY_UUID));
                Log.i(TAG,"socket created");
            } catch (IOException e) {
                e.printStackTrace();
                Log.i(TAG,"error in channel "+e.getMessage()+""+e.getLocalizedMessage()+""+e.getCause());
            }


            try {
                Log.i(TAG,"starts to accept connection");
                    BluetoothSocket socket=serverSocket.accept();
                Log.i(TAG,"conn accepted");
                new ReadDataFromBt(socket).execute();
                } catch (IOException e) {
                    e.printStackTrace();
                Log.i(TAG,"Error in server "+e.getMessage());
                }



        }
    }

    BluetoothSocket clientSocket;
    class startAsClient extends Thread
    {

        @Override
        public void run() {
            super.run();

            BluetoothSocket socket = null;
            try {
                Log.i(TAG,"start to create channel");
                 socket=pairedBltDevice.createRfcommSocketToServiceRecord(UUID.fromString(MY_UUID));
            } catch (IOException e) {
                e.printStackTrace();
                Log.i(TAG,"error in channel creation "+e.getMessage());
            }

            try {
                Log.i(TAG,"client req conn");
                socket.connect();
                Log.i(TAG,"client connected");
                clientSocket=socket;
            } catch (IOException e) {
                e.printStackTrace();
                Log.i(TAG,"error in client conn "+e.getMessage());
            }
        }
    }


    private void sendText()
    {
        try {
            clientSocket.getOutputStream().write(message.getText().toString().getBytes());
            Log.i(TAG,"Message sented");
        } catch (IOException e) {
            e.printStackTrace();
            Log.i(TAG,"message se error "+e.getMessage());
        }
    }


    class ReadDataFromBt extends AsyncTask
    {
        BluetoothSocket socket;

        public ReadDataFromBt(BluetoothSocket socket) {
            this.socket = socket;
        }

        @Override
        protected Object doInBackground(Object[] objects) {

            byte data[] = new byte[100];
            while (true)
            {

                Log.i(TAG,"read starts");
                try {
                    socket.getInputStream().read(data);
                } catch (IOException e) {
                    e.printStackTrace();
                }

               // Toast.makeText(MainActivity.this, ""+Arrays.toString(data), Toast.LENGTH_SHORT).show();
                Log.i(TAG,"read values "+ Arrays.toString(data));
            }
           // return null;
        }
    }
}

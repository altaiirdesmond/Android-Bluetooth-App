package com.cdtekk.bluetoothapp;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.PopupMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Objects;
import java.util.Set;

public class MainActivity extends Activity implements AsyncResponse {

    private Button btnPowerOn;
    private Button btnPowerOff;
    private PopupMenu popupMenu;
    private ImageView imageViewConveyorState;
    private ArrayAdapter<String> arrayAdapter;
    private BluetoothAdapter bluetoothAdapter;
    private ConnectBT connectBT;
    private AsyncResponse asyncResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Make user enable bluetooth
        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(enableBtIntent, 1);

        asyncResponse = this;
        ImageButton imageButtonSettings = findViewById(R.id.btnSetting);
        imageViewConveyorState = findViewById(R.id.imageStateConveyor);
        btnPowerOff = findViewById(R.id.btnPowerOff);
        btnPowerOn = findViewById(R.id.btnPowerOn);
        popupMenu = new PopupMenu(this, imageButtonSettings);
        final MenuInflater menuInflater = popupMenu.getMenuInflater();
        menuInflater.inflate(R.menu.setting_menu, popupMenu.getMenu());

        btnPowerOn.setEnabled(false);
        btnPowerOff.setEnabled(false);

        imageButtonSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupMenu.show();
            }
        });

        btnPowerOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    ConnectBT.getmSocket().getOutputStream().write("POWEROFF".getBytes());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Toast.makeText(getApplicationContext(), "Turn off", Toast.LENGTH_SHORT).show();

                imageViewConveyorState.setImageDrawable(getDrawable(R.drawable.electricity_96));
            }
        });

        btnPowerOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    ConnectBT.getmSocket().getOutputStream().write("POWERON".getBytes());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Toast.makeText(getApplicationContext(), "Turn on", Toast.LENGTH_SHORT).show();

                imageViewConveyorState.setImageDrawable(getDrawable(R.drawable.checkmark_96));
            }
        });

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.btnConnect:
                        bluetoothStart();
                        break;
                    case R.id.btnExit:
                        finish();
                        System.exit(0);
                        break;
                    case R.id.btnDisconnect:
                        if(ConnectBT.getmSocket() != null){
                            try {
                                ConnectBT.getmSocket().close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            connectBT = null;

                            btnPowerOff.setEnabled(false);
                            btnPowerOn.setEnabled(false);
                        }
                        break;
                }

                return false;
            }
        });
    }

    private void bluetoothStart(){
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(bluetoothAdapter == null){
            Toast.makeText(getApplicationContext(), "Bluetooth Device Not Available", Toast.LENGTH_LONG).show();
        }else{
            // Bluetooth off
            if(!bluetoothAdapter.isEnabled()){
                // Make user enable bluetooth
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, 1);

                return;
            }

            Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
            if(pairedDevices.size() > 0){
                arrayAdapter =
                        new ArrayAdapter<>(MainActivity.this, android.R.layout.select_dialog_singlechoice);
                for (BluetoothDevice bluetoothDevice : pairedDevices){
                    arrayAdapter.add(bluetoothDevice.getName() + "\n" + bluetoothDevice.getAddress());
                }
            }

            // Show all bonded devices only
            AlertDialog.Builder builderSingle = new AlertDialog.Builder(MainActivity.this);
            builderSingle.setTitle("Connect to");

            final String[] strName = new String[1];
            builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    strName[0] = arrayAdapter.getItem(which);

                    connectBT = new ConnectBT(
                            getApplicationContext(),
                            bluetoothAdapter,
                            Objects.requireNonNull(strName[0]).split("\n")[1]);
                    connectBT.delegate = asyncResponse;
                    connectBT.execute();
                }
            });

            // Show listed devices
            builderSingle.show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void processingFinish(Boolean output) {
        btnPowerOn.setEnabled(output);
        btnPowerOff.setEnabled(output);
    }
}

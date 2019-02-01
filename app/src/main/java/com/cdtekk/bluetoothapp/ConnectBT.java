package com.cdtekk.bluetoothapp;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.AsyncTask;
import android.os.ParcelUuid;
import android.widget.Toast;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ConnectBT extends AsyncTask<Void, Void, Void> {

    @SuppressLint("StaticFieldLeak")
    private Context context;
    private BluetoothSocket socket;
    private String address;
    private BluetoothAdapter adapter;
    private BluetoothDevice device;

    ConnectBT(Context context, BluetoothAdapter adapter, String address) {
        this.context = context;
        this.address = address;
        this.adapter = adapter;
    }

    @Override
    protected void onPreExecute(){
        Toast.makeText(context, "Connecting...Please wait", Toast.LENGTH_LONG).show();
    }

    @Override
    protected Void doInBackground(Void... voids) {
        try{
            device = adapter.getRemoteDevice(address);
            Method method = device.getClass().getMethod("getUuids");
            ParcelUuid[] parcelUuids = (ParcelUuid[]) method.invoke(device);

            socket = device.createInsecureRfcommSocketToServiceRecord(parcelUuids[0].getUuid());

            BluetoothAdapter.getDefaultAdapter().cancelDiscovery();

            socket.connect();
        }catch (IOException ignored){
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void result){
        super.onPostExecute(result);

        if(!socket.isConnected()){
            Toast.makeText(context, "Failed to connect", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(context, "Connected to " + device.getName() + "\n" + device.getAddress(), Toast.LENGTH_SHORT).show();
        }
    }
}

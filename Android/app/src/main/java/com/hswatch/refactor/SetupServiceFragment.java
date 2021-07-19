package com.hswatch.refactor;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.hswatch.MainActivity;
import com.hswatch.R;
import com.hswatch.Utils;
import com.hswatch.bluetooth.MainServico;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import static com.hswatch.Utils.connectionSucceeded;
import static com.hswatch.Utils.tryConnecting;

public class SetupServiceFragment extends Fragment {

    private ListView listView;
    private boolean searchAble = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(
                R.layout.fragment_setup_service,
                container,
                false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        listView = view.findViewById(R.id.listview_setup_service);

        showBTDevices(listView);

        listView.setOnItemClickListener((adapterView, view1, position, l) -> {
            if (searchAble) startConnection((String) listView.getItemAtPosition(position));
        });

    }

    private void startConnection(String deviceName) {
        startMainServico(deviceName);

        new Thread(new Runnable(){
            @Override
            public void run() {

                synchronized (this) {
                    try {
                        this.wait(1000);
                    } catch (InterruptedException interruptedException) {
                        interruptedException.printStackTrace();
                    }
                }

                while (tryConnecting);

                if (connectionSucceeded) {
                    ConfigurationFragment configurationFragment = (ConfigurationFragment) getParentFragment();

                    if (configurationFragment != null) {
                        configurationFragment.changeFragment(Utils.NEXT_FROM_LIST);
                    }
                }
            }
        }).start();
    }

    private void startMainServico(String deviceName) {

        Context context;
        try {
            context = requireContext();
        } catch (IllegalStateException illegalStateException) {
            illegalStateException.printStackTrace();
            return;
        }

        Intent startServiceIntent = new Intent(context, MainServico.class);
        startServiceIntent.putExtra(Utils.BT_DEVICE_NAME, deviceName);
        ConfigurationFragment configurationFragment = (ConfigurationFragment) getParentFragment();

        if (configurationFragment != null) {
            MainActivity mainActivity = configurationFragment.getMainActivity();
            mainActivity.startForegroundService(startServiceIntent);
        }

//        getActivity().startForegroundService(startServiceIntent);
//        ContextCompat.startForegroundService(context, startServiceIntent);

        tryConnecting = true;
    }

    private void showBTDevices(@NonNull ListView listView) {

        Context context;
        try {
            context = requireContext();
        } catch (IllegalStateException illegalStateException) {
            illegalStateException.printStackTrace();
            return;
        }

        listView.setAdapter(new ArrayAdapter<>(
                context,
                R.layout.lista_emparelhados_layout,
                returnNames(BluetoothAdapter.getDefaultAdapter().getBondedDevices(), context)
        ));

    }

    private List<String> returnNames(Set<BluetoothDevice> bluetoothDeviceSet, Context context) {
        List<String> names = new ArrayList<>();
        if (bluetoothDeviceSet.size() > 0) {
            for (BluetoothDevice bluetoothDevice : bluetoothDeviceSet) {
                names.add(bluetoothDevice.getName());
            }
            searchAble = true;
        } else {
            names.add(context.getResources().getString(R.string.BT_DEVICE_NOT_FOUND));
            searchAble = false;
        }
        return names;
    }
}

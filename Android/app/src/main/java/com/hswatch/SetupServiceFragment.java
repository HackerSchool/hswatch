package com.hswatch;

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
import android.widget.Toast;

import com.hswatch.bluetooth.MainServico;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

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
            if (searchAble) startService((String) listView.getItemAtPosition(position));
        });

    }

    private void startService(String deviceName) {
        Context context;
        try {
            context = requireContext();
        } catch (IllegalStateException illegalStateException) {
            illegalStateException.printStackTrace();
            return;
        }

        Intent startService = new Intent(getContext(), MainServico.class);
        startService.putExtra(Utils.BT_DEVICE_NAME, deviceName);
        ContextCompat.startForegroundService(context, startService);
    }

    private void showBTDevices(@NonNull ListView listView) {
        Set<BluetoothDevice> bluetoothDeviceSet = BluetoothAdapter.getDefaultAdapter()
                .getBondedDevices();

        List<String> names = new ArrayList<>();
        if (bluetoothDeviceSet.size() > 0) {
            for (BluetoothDevice bluetoothDevice : bluetoothDeviceSet) {
                names.add(bluetoothDevice.getName());
            }
            searchAble = true;
        } else {
            try {
                names.add(requireContext().getResources().getString(R.string.BT_DEVICE_NOT_FOUND));
            } catch (IllegalStateException illegalStateException) {
                illegalStateException.printStackTrace();
                names.add("Error");
            }
            searchAble = false;
        }

        listView.setAdapter(new ArrayAdapter<>(
                requireContext(),
                R.layout.lista_emparelhados_layout,
                names
        ));

    }
}

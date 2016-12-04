package com.qj.directtalk;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * Created by qasimj on 12/3/16.
 */

public class DiscoveryResultsFragment extends Fragment {
    private static final String TAG = DiscoveryResultsFragment.class.getSimpleName();

    private ListView mDiscoveryResultsList;
    private ArrayList<WifiP2pDevice> mDevices;
    private ArrayAdapter<WifiP2pDevice> mDeviceAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDevices = new ArrayList<WifiP2pDevice>();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.discovery_results_fragment, null, false);
        mDiscoveryResultsList = (ListView) v.findViewById(R.id.discoveryResultsList);
        mDiscoveryResultsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                WifiP2pDevice device = mDevices.get(position);
                Log.i(TAG, "Connecting to: " + device.deviceName);
                Intent intent = new Intent(getActivity(), ConnectionService.class);
                intent.setAction(ConnectionService.CONNECT_ACTION);
                intent.putExtra(ConnectionService.PEER, device);
                getActivity().startService(intent);
            }
        });
        mDeviceAdapter = new ArrayAdapter<WifiP2pDevice>(
                getActivity(), android.R.layout.simple_list_item_1, mDevices);
        Log.i(TAG, "Discovered devices: " + mDevices.size());
        mDiscoveryResultsList.setAdapter(mDeviceAdapter);

        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    public void updateDiscoveredPeers() {
        mDevices.addAll(WifiP2pState.getInstance().getDiscoveredDevices());
        Log.i(TAG, "Number of disc: " + mDevices.size());
        mDeviceAdapter.notifyDataSetChanged();
    }
}

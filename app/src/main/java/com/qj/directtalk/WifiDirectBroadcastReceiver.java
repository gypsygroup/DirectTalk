package com.qj.directtalk;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pGroup;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by qasimj on 12/2/16.
 */

public class WifiDirectBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = WifiDirectBroadcastReceiver.class.getSimpleName();

    private WifiP2pManager mWifiP2pManager;
    private WifiP2pManager.Channel mChannel;

    public WifiDirectBroadcastReceiver(WifiP2pManager manager, WifiP2pManager.Channel channel) {
        this.mWifiP2pManager = manager;
        this.mChannel = channel;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
            int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
            if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
            } else if (state == WifiP2pManager.WIFI_P2P_STATE_DISABLED) {
            }
        } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
            WifiP2pDeviceList devices = intent.getParcelableExtra(
                    WifiP2pManager.EXTRA_P2P_DEVICE_LIST);
            Log.i(TAG, "Discovered device list size: " + devices.getDeviceList().size());
            WifiP2pState.getInstance().setDiscoveredDevices(new ArrayList<WifiP2pDevice>(
                    devices.getDeviceList()));
            notifyPeersChanged(context);
        } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
            WifiP2pInfo p2pInfo = intent.getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_INFO);
            NetworkInfo netInfo = intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);
            WifiP2pGroup groupInfo = intent.getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_GROUP);
            if (p2pInfo.isGroupOwner) {
                if (groupInfo != null) {
                    groupOwnerInfoAvailable(context, groupInfo);
                }
            } else {
                if (netInfo != null) {
                    if (netInfo.isConnected()) {
                        Toast.makeText(context, "Connected to " + p2pInfo.groupOwnerAddress,
                                Toast.LENGTH_LONG).show();
                    }
                }
            }
        } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {

        }
    }

    private void notifyPeersChanged(Context context) {
        Intent intent = new Intent();
        intent.setAction(MainActivity.PEERS_CHANGED_ACTION);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

    private void groupOwnerInfoAvailable(Context context, WifiP2pGroup info) {
        WifiP2pState.getInstance().setGroupOwnerInfo(info);
        Intent intent = new Intent();
        intent.setAction(MainActivity.GO_INFO_AVAILABLE_ACTION);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }
}

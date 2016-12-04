package com.qj.directtalk;

import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pGroup;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Handler;
import android.os.Looper;

import java.util.ArrayList;

/**
 * Created by qasimj on 12/2/16.
 */

public class WifiP2pState {
    private static final String TAG = WifiP2pState.class.getSimpleName();

    private static WifiP2pState mInstance;
    public static WifiP2pState createInstance(Context context, Looper looper) {
        if (mInstance == null) {
            mInstance = new WifiP2pState(context, looper);
        }
        return mInstance;
    }

    public static WifiP2pState getInstance() {
        return mInstance;
    }

    private Context context;
    private WifiP2pManager mWifiP2pManager;
    private WifiP2pManager.Channel mChannel;
    private WifiDirectBroadcastReceiver mReceiver;
    private IntentFilter mFilter;
    private ArrayList<WifiP2pDevice> mDiscoveredDevices;
    private WifiP2pGroup mGroupOwnerInfo;

    private WifiP2pState(Context context, Looper looper) {
        mWifiP2pManager = (WifiP2pManager) context.getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = mWifiP2pManager.initialize(context, looper, null);
        mFilter = new IntentFilter();
        mFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        mFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        mFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        mFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
        mReceiver = new WifiDirectBroadcastReceiver(mWifiP2pManager, mChannel);
        mDiscoveredDevices = new ArrayList<WifiP2pDevice>();
    }

    public WifiP2pManager getWifiP2pManager() {
        return mWifiP2pManager;
    }

    public WifiP2pManager.Channel getChannel() {
        return mChannel;
    }

    public WifiDirectBroadcastReceiver getReceiver() {
        return mReceiver;
    }

    public IntentFilter getFilter() {
        return mFilter;
    }

    public WifiP2pGroup getGroupOwnerInfo() { return mGroupOwnerInfo; }

    public ArrayList<WifiP2pDevice> getDiscoveredDevices() {
        return mDiscoveredDevices;
    }
    public void setDiscoveredDevices(ArrayList<WifiP2pDevice> devices) {
        mDiscoveredDevices = devices;
    }
    public void setGroupOwnerInfo(WifiP2pGroup info) {
        mGroupOwnerInfo = info;
    }
}

package com.qj.directtalk;

import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

/**
 * Created by qasimj on 12/2/16.
 */

public class WifiDirectClient {
    private static final String TAG = WifiDirectClient.class.getSimpleName();

    private static final int CLIENT_IDLE = 0;
    private static final int CLIENT_DISC_INIT = 1;
    private static final int CLIENT_DISC_DONE = 2;
    private static final int CLIENT_CONN_SENT = 3;
    private static final int CLIENT_CONNECTED = 4;

    private WifiP2pManager mManager;
    private WifiP2pManager.Channel mChannel;
    private int mClientState = CLIENT_IDLE;

    public WifiDirectClient() {
        mManager = WifiP2pState.getInstance().getWifiP2pManager();
        mChannel = WifiP2pState.getInstance().getChannel();
    }

    public void start() {
        if (mClientState == CLIENT_IDLE) {
            mManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {
                @Override
                public void onSuccess() {
                    Log.i(TAG, "Discovery request sent!");
                    mClientState = CLIENT_DISC_INIT;
                }

                @Override
                public void onFailure(int reason) {
                    Log.i(TAG, "Discovery request failed: " + reason);
                }
            });
        }
    }

    public void stop() {

    }

    public void connect(WifiP2pDevice remote) {
        mClientState = CLIENT_DISC_DONE;
        WifiP2pConfig config = new WifiP2pConfig();
        config.deviceAddress = remote.deviceAddress;
        config.groupOwnerIntent = 1;
        config.wps.setup = WpsInfo.PBC;
        mManager.connect(mChannel, config, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Log.i(TAG, "Successfully initiated connect!");
                mClientState = CLIENT_CONN_SENT;
            }

            @Override
            public void onFailure(int reason) {
                Log.i(TAG, "Failed to initiate connect: " + reason);
            }
        });
    }
}

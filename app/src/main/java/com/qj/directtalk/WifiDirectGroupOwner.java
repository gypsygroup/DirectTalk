package com.qj.directtalk;

import android.net.wifi.p2p.WifiP2pManager;
import android.util.Log;

/**
 * Created by qasimj on 12/2/16.
 */

public class WifiDirectGroupOwner {
    private static final String TAG = WifiDirectGroupOwner.class.getSimpleName();

    private static final int GROUP_NONE = 0;
    private static final int GROUP_CREATE_SENT = 1;

    private WifiP2pManager mManager;
    private WifiP2pManager.Channel mChannel;
    private int mGroupState = GROUP_NONE;

    public WifiDirectGroupOwner() {
        mManager = WifiP2pState.getInstance().getWifiP2pManager();
        mChannel = WifiP2pState.getInstance().getChannel();
    }

    public void start() {
        mManager.createGroup(mChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Log.i(TAG, "createGroup request initiated!");
                mGroupState = GROUP_CREATE_SENT;
            }

            @Override
            public void onFailure(int reason) {
                Log.i(TAG, "createGroup request failed: " + reason);
            }
        });
    }

    public void stop() {
        if (mGroupState == GROUP_CREATE_SENT) {
            mManager.removeGroup(mChannel, new WifiP2pManager.ActionListener() {
                @Override
                public void onSuccess() {
                    Log.i(TAG, "removeGroup request initiated!");
                    mGroupState = GROUP_NONE;
                }

                @Override
                public void onFailure(int reason) {
                    Log.i(TAG, "removeGroup request failed: " + reason);
                }
            });
        }
    }
}

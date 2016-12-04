package com.qj.directtalk;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.util.Log;

public class ConnectionService extends Service {
    private static final String TAG = ConnectionService.class.getSimpleName();

    public static final String START_ACTION = "START_ACTION";
    public static final String STOP_ACTION = "START_ACTION";
    public static final String ROLE = "ROLE";
    public static final int ROLE_GO = 0;
    public static final int ROLE_CLIENT = 1;
    public static final String CONNECT_ACTION = "CONNECT_ACTION";
    public static final String PEER = "PEER";

    private WifiDirectGroupOwner mGo;
    private WifiDirectClient mClient;
    private boolean mIsGo;
    private WifiP2pState mState;

    private HandlerThread mThread;
    private Handler mHandler;

    @Override
    public void onCreate() {
        super.onCreate();
        mThread = new HandlerThread("ConnService");
        mThread.start();
        mState = WifiP2pState.createInstance(this, mThread.getLooper());
        registerReceiver(mState.getReceiver(), mState.getFilter());
        mHandler = new Handler(mThread.getLooper());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String action;
        if (intent != null) {
            action = intent.getAction();

            if (START_ACTION.equals(action)) {
                int role = intent.getIntExtra(ROLE, -1);
                switch (role) {
                    case ROLE_GO:
                        mIsGo = true;
                        startGroupOwner();
                        break;
                    case ROLE_CLIENT:
                        mIsGo = false;
                        startClient();
                        break;
                    default:
                        Log.i(TAG, "Invalid role received for START_ACTION: " + role);
                }
            } else if (CONNECT_ACTION.equals(action)) {
                WifiP2pDevice peer = intent.getParcelableExtra(ConnectionService.PEER);
                if (peer != null) {
                    connect(peer);
                }
            } else if (STOP_ACTION.equals(action)) {
                int role = intent.getIntExtra(ROLE, -1);
                switch(role) {
                    case ROLE_GO:
                        stopGroupOwner();
                        break;
                    default:
                        Log.i(TAG, "Invalid role recevied for STOP_ACTION: " + role);
                }
            }
        }
        return START_STICKY;
    }

    private void startGroupOwner() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mGo = new WifiDirectGroupOwner();
                mGo.start();
            }
        });
    }

    private void stopGroupOwner() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mGo.stop();
            }
        });
    }

    private void startClient() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mClient = new WifiDirectClient();
                mClient.start();
            }
        });
    }

    private void connect(WifiP2pDevice peer) {
        final WifiP2pDevice remote = peer;
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mClient.connect(remote);
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mIsGo) {
            mGo.stop();
        } else {
            mClient.stop();
        }
        mThread.quit();
        unregisterReceiver(mState.getReceiver());
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}

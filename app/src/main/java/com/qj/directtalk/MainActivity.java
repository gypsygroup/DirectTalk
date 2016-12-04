package com.qj.directtalk;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ToggleButton;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    public static final String PEERS_CHANGED_ACTION = "PEERS_CHANGED";
    public static final String GO_INFO_AVAILABLE_ACTION = "GO_INFO_AVAILABLE_ACTION";


    private ToggleButton mToggleGo;
    private DiscoveryResultsFragment mDiscoveryResulsFragment;
    private GroupOwnerInfoFragment mGroupOwnerInfoFragment;
    private ActivityBroadcastReceiver mReceiver;

    private class ActivityBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (PEERS_CHANGED_ACTION.equals(action)) {
                if (mDiscoveryResulsFragment != null) {
                    Log.d(TAG, "Updating the UI with new discovered peers.");
                    mDiscoveryResulsFragment.updateDiscoveredPeers();
                }
            } else if (GO_INFO_AVAILABLE_ACTION.equals(action)) {
                if (mGroupOwnerInfoFragment != null) {
                    Log.d(TAG, "Updating the UI with Group OWner info.");
                    mGroupOwnerInfoFragment.updateGroupOwnerInfo();
                }
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mReceiver);
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver,
                new IntentFilter(PEERS_CHANGED_ACTION));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mToggleGo = (ToggleButton) findViewById(R.id.toggleGo);
        mToggleGo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Intent intent = new Intent(MainActivity.this, ConnectionService.class);
                if (isChecked) {
                    loadGroupOwnerInfoFragment();
                    intent.setAction(ConnectionService.START_ACTION);
                } else {
                    intent.setAction(ConnectionService.STOP_ACTION);
                }
                intent.putExtra(ConnectionService.ROLE, ConnectionService.ROLE_GO);
                startService(intent);
            }
        });
        mReceiver = new ActivityBroadcastReceiver();
    }

    public void startDiscovery(View v) {
        Button discoverButton = (Button) findViewById(R.id.discoverButton);
        loadDiscoveryResultsFragment();
        if (discoverButton == (Button) v) {
            Intent intent = new Intent(this, ConnectionService.class);
            intent.setAction(ConnectionService.START_ACTION);
            intent.putExtra(ConnectionService.ROLE, ConnectionService.ROLE_CLIENT);
            startService(intent);
        }
    }

    private void loadDiscoveryResultsFragment() {
        FragmentManager fManager = getFragmentManager();
        mDiscoveryResulsFragment = (DiscoveryResultsFragment) fManager.findFragmentById(
                R.id.fragmentContainer);
        if (mDiscoveryResulsFragment == null) {
            mDiscoveryResulsFragment = new DiscoveryResultsFragment();
            fManager.beginTransaction()
                    .add(R.id.fragmentContainer, mDiscoveryResulsFragment)
                    .commit();
        }
    }

    private void loadGroupOwnerInfoFragment() {
        FragmentManager fManager = getFragmentManager();
        mGroupOwnerInfoFragment = (GroupOwnerInfoFragment) fManager.findFragmentById(
                R.id.fragmentContainer);
        if (mGroupOwnerInfoFragment == null) {
            mGroupOwnerInfoFragment = new GroupOwnerInfoFragment();
            fManager.beginTransaction()
                    .add(R.id.fragmentContainer, mGroupOwnerInfoFragment)
                    .commit();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}

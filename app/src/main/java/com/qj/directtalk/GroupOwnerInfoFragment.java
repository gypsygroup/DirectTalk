package com.qj.directtalk;

import android.app.Fragment;
import android.net.wifi.p2p.WifiP2pGroup;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by qasimj on 12/4/16.
 */

public class GroupOwnerInfoFragment extends Fragment {
    private WifiP2pGroup mGroupOwnerInfo;
    private TextView mGroupOwnerInfoView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.group_owner_info_fragment, null, false);
        mGroupOwnerInfoView = (TextView) v.findViewById(R.id.groupOwnerInfo);
        return v;
    }

    public void updateGroupOwnerInfo() {
        WifiP2pGroup info = WifiP2pState.getInstance().getGroupOwnerInfo();
        mGroupOwnerInfoView.setText(info.toString());
    }
}

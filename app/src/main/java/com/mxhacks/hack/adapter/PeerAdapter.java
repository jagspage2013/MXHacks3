package com.mxhacks.hack.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import ch.uepaa.p2pkit.discovery.Peer;

/**
 * Created by José Ángel García Salinas on 03/10/15.
 */
public class PeerAdapter extends BaseAdapter{

    private ArrayList<Peer> mPeers = new ArrayList<>();
    int  dp ;


    public PeerAdapter(Context context, ArrayList<Peer> peers) {
        this.mPeers = peers;
        this.dp = (int) context.getResources().getDisplayMetrics().density * 16;
    }

    @Override
    public int getCount() {
        return mPeers.size();
    }

    @Override
    public Object getItem(int i) {
        return mPeers.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        TextView v = new TextView(viewGroup.getContext());
        v.setLayoutParams(new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        v.setPadding(dp, dp, dp, dp);
        try {
            v.setText("DiscoverInfo: "+new String(mPeers.get(i).getDiscoveryInfo()) +"\nUUID " + mPeers.get(i).getNodeId());
        }catch (NullPointerException e){
            v.setText("DiscoverInfo: null" +"\nUUID " + mPeers.get(i).getNodeId());

        }
        return v;
    }


}

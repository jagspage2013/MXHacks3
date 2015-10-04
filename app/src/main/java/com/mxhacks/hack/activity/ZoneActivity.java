package com.mxhacks.hack.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.mxhacks.hack.R;
import com.mxhacks.hack.adapter.PeerAdapter;
import com.mxhacks.hack.model.MusicZone;
import com.mxhacks.hack.utils.Global;
import com.mxhacks.hack.utils.Logger;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.UUID;

import ch.uepaa.p2pkit.ConnectionCallbacks;
import ch.uepaa.p2pkit.ConnectionResult;
import ch.uepaa.p2pkit.ConnectionResultHandling;
import ch.uepaa.p2pkit.KitClient;
import ch.uepaa.p2pkit.discovery.GeoListener;
import ch.uepaa.p2pkit.discovery.InfoTooLongException;
import ch.uepaa.p2pkit.discovery.P2pListener;
import ch.uepaa.p2pkit.discovery.Peer;
import ch.uepaa.p2pkit.messaging.MessageListener;



public class ZoneActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    //Constants

    //Adapter
    private PeerAdapter mAdapter;

    //List
    private ArrayList<Peer> mP2pUsers;
    
    //Variables
    private boolean shouldStartServices = true;


    /********************************** P2P LISTENERS  **********************************/

    //Connection callbacks listener
    private ConnectionCallbacks mConnectionCallbacks = new ConnectionCallbacks() {
        //KitClient Connection Callbacks
        @Override
        public void onConnected() {
            Logger.D("P2PKit connectado exitosamente descubriendo peers");

            if(shouldStartServices){
                shouldStartServices=false;
                startP2pDiscover();
                startGeoDiscover();
            }

        }

        @Override
        public void onConnectionSuspended() {
            Logger.D("ConnectionCallback suspended");
        }

        @Override
        public void onConnectionFailed(ConnectionResult connectionResult) {
            Logger.E("P2PKit conexión fallado por "+connectionResult.getStatusCode());

        }

    };

    //P2pListener
    private P2pListener mP2pListener = new P2pListener() {
        @Override
        public void onStateChanged(int i) {

            String state = "";
            switch (i){
                case STATE_ON:
                    state = "state on";
                    break;

                case P2pListener.STATE_OFF:
                    state = " off";
                    break;

                case P2pListener.STATE_WIFI_NOT_AVAILABLE:
                    state = " wifi not available";
                    break;

                case P2pListener.STATE_BLE_NOT_AVAILABLE:
                    state = " ble not available";
                    break;

            }

            Logger.D("P2PListener | State Changed: "+state);
        }

        @Override
        public void onPeerDiscovered(Peer peer) {
            mP2pUsers.add(peer);

            Logger.D("Peer descubierto " + peer.getNodeId());
            try{
                Toast.makeText(getApplicationContext(), "Nuevo descubrimiento " + new String(peer.getDiscoveryInfo()), Toast.LENGTH_SHORT).show();
            }catch (NullPointerException e){
                Logger.E("Doesn't have discovery info "+peer.getNodeId());
                Toast.makeText(getApplicationContext(),"Nuevo descubrimiento "+peer.getNodeId(),Toast.LENGTH_SHORT).show();

            }
            mAdapter.notifyDataSetChanged();
        }

        @Override
        public void onPeerLost(Peer peer) {
            Logger.D("Peer perdido " + peer.getNodeId());

        }

        @Override
        public void onPeerUpdatedDiscoveryInfo(Peer peer) {

        }
    };

    //**************************** GeoListener
    private GeoListener mGeoListener = new GeoListener() {
        @Override
        public void onStateChanged(int i) {
            String state = "";
            switch (i){
                case GeoListener.STATE_ON:
                    state = " on";
                    break;

                case GeoListener.STATE_OFF:
                    state = " off";
                    break;

                case GeoListener.STATE_NET_SUSPENDED:
                    state = " net suspended";
                    break;

                case GeoListener.STATE_LOCATION_SUSPENDED:
                    state = " loc suspended";
                    break;

            }

            Logger.D("GeoListener | State Changed: " + state);
        }

        @Override
        public void onPeerDiscovered(UUID uuid) {
            Logger.D("Peer descubierto " + uuid);

            KitClient.getInstance(getApplicationContext()).getMessageServices().sendMessage(uuid, "SimpleChatMessage", "Descubierto por GEO!".getBytes());

        }

        @Override
        public void onPeerLost(UUID uuid) {

        }
    };

    //**************************** P2P MESSAGING LISTENER
    private MessageListener mMessageListenner = new MessageListener() {
        @Override
        public void onStateChanged(int i) {
            String state = "";
            switch (i){
                case MessageListener.STATE_ON:
                    state = "state on";
                    break;

                case MessageListener.STATE_OFF:
                    state = "state off";
                    break;

                case MessageListener.STATE_NET_SUSPENDED:
                    state = "state suspended";
                    break;

            }
            Logger.D("Messagging | State Changed: " + state);
        }

        @Override
        public void onMessageReceived(long l, UUID uuid, String s, byte[] bytes) {
            Logger.D("Message received from "+uuid);
            Toast.makeText(getApplicationContext(),"Mensaje Recibido "+new String(bytes),Toast.LENGTH_LONG).show();
        }
    };

    /********************************** LifeCycle Methods  **********************************/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mP2pUsers = new ArrayList<>();
        mAdapter = new PeerAdapter(this,mP2pUsers);
        
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToCreateMusicZone();
            }
        });

        showList();
    }


    @Override
    public void onResume() {
        super.onResume();

        if(!KitClient.getInstance(this).isConnected()){
            initP2pConection();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        KitClient.getInstance(this).disconnect();
    }

    /********************************** Click Listener Methods  **********************************/

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        sendMessage(i);
    }




    /********************************** P2P Action Methods  **********************************/

    public void initP2pConection() {

        final int statusCode = KitClient.isP2PServicesAvailable(this);
        if(statusCode == ConnectionResult.SUCCESS){
            KitClient kitClient = KitClient.getInstance(this);

            kitClient.registerConnectionCallbacks(mConnectionCallbacks);
            if (kitClient.isConnected()) {
                Logger.D("Client already initialized");
            } else {
                Logger.D("Connecting P2PKit client");
                kitClient.connect(Global.P2PKIT_CLIENT_API_KEY);
            }
        }else{
            ConnectionResultHandling.showAlertDialogForConnectionError(this, statusCode);
        }
    }

    private void startP2pDiscover(){
        //para ya no salir en las pantallas de los otros.
        try {
            MusicZone m = new MusicZone("Titulo 1","",KitClient.getInstance(this).getNodeId());
            KitClient.getInstance(this).getDiscoveryServices().setP2pDiscoveryInfo(m.toByteArray());

        }catch (InfoTooLongException e) {
            Toast.makeText(this, "P2pListener | The discovery info is too long", Toast.LENGTH_SHORT).show();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        KitClient.getInstance(this).getDiscoveryServices().addListener(mP2pListener);
    }

    private void startGeoDiscover(){
        KitClient.getInstance(this).getMessageServices().addListener(mMessageListenner);
        KitClient.getInstance(this).getDiscoveryServices().addListener(mGeoListener);
    }

    private void sendMessage(int position) {
        boolean forwarded = KitClient.getInstance(this).getMessageServices().sendMessage(mP2pUsers.get(position).getNodeId(), "text/plain", "Hello Que hay".getBytes());
        if (forwarded){
            Toast.makeText(this,"Mensaje Enviado",Toast.LENGTH_SHORT).show();
        }else{
            Logger.E("Mensaje NO ENVIADO " + mP2pUsers.get(position));
            KitClient.getInstance(this).getMessageServices().checkWorkingState();
        }
    }

    private void showList() {

        this.setTitle("Listas Cercanas.");
        ListView mListView = (ListView) findViewById(android.R.id.list);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(this);
    }

    private void goToCreateMusicZone(){
        startActivity(new Intent(ZoneActivity.this,CreateMusicZoneActivity.class));
    }

   
}

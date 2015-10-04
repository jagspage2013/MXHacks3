package com.mxhacks.hack.activity;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.mxhacks.hack.R;
import com.mxhacks.hack.utils.Global;
import com.mxhacks.hack.utils.Logger;
import com.mxhacks.hack.utils.ValueSave;
import com.spotify.sdk.android.player.Config;
import com.spotify.sdk.android.player.ConnectionStateCallback;
import com.spotify.sdk.android.player.Connectivity;
import com.spotify.sdk.android.player.Player;
import com.spotify.sdk.android.player.PlayerNotificationCallback;
import com.spotify.sdk.android.player.PlayerState;
import com.spotify.sdk.android.player.Spotify;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Pager;
import kaaes.spotify.webapi.android.models.PlaylistTrack;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class MyZoneActivity extends AppCompatActivity implements PlayerNotificationCallback, ConnectionStateCallback {

    public static final String PLAYLIST = "platlist";
    public static final String PLAYLIST_TITLE = "platlist_title";
    public static final String USER_ID = "user_id";
    public static final String SHOW_LIST = "SHOW_LIST";

    private String mPlayList;
    private String mPlayListTitle;
    private String mUserId;

    private boolean showList;

    private List<String> mTracks = new ArrayList<>();
    private Pager<PlaylistTrack> playlistTrack;
    private String mCurrentTrack;

    private ArrayList<UUID> peersIds = new ArrayList<>();

    private Player mPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mi_zone);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mPlayList = getIntent().getStringExtra(PLAYLIST);
        mPlayListTitle = getIntent().getStringExtra(PLAYLIST_TITLE);
        mUserId = getIntent().getStringExtra(USER_ID);
        showList = getIntent().getBooleanExtra(SHOW_LIST,false);

        setTitle("Reproduciendo "+mPlayListTitle);

        configurePlayer();
        //spoty config
        SpotifyApi api = new SpotifyApi();
        api.setAccessToken(ValueSave.getString(this, Global.SPOTIFY_ACCESS_TOKEN));

        final SpotifyService spotifyService = api.getService();
        Logger.D("Getting tracks with userid "+mUserId + " playlist is "+mPlayList);
        spotifyService.getPlaylistTracks(mUserId, mPlayList, new Callback<Pager<PlaylistTrack>>() {
            @Override
            public void success(Pager<PlaylistTrack> playlistTrackPager, Response response) {
                playlistTrack = playlistTrackPager;
                prepareTrackList(playlistTrackPager);
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }


    @Override
    protected void onPause() {
        super.onPause();

        if (mPlayer != null) {
            mPlayer.removePlayerNotificationCallback(this);
            mPlayer.removeConnectionStateCallback(this);
        }
    }

    @Override
    protected void onDestroy() {
        Spotify.destroyPlayer(this);
        super.onDestroy();
    }

    private void configurePlayer() {
        Config config = new Config(this,ValueSave.getString(this,Global.SPOTIFY_ACCESS_TOKEN),Global.SPOTIFY_CLIENT_ID);

        if(mPlayer ==null){
            mPlayer = Spotify.getPlayer(config, this, new Player.InitializationObserver() {
                @Override
                public void onInitialized(Player player) {
                    mPlayer.setConnectivityStatus(getNetworkConnectivity(getApplicationContext()));
                    mPlayer.addPlayerNotificationCallback(MyZoneActivity.this);
                    mPlayer.addConnectionStateCallback(MyZoneActivity.this);
                }

                @Override
                public void onError(Throwable throwable) {

                }
            });
        }else {
            mPlayer.login(ValueSave.getString(this, Global.SPOTIFY_ACCESS_TOKEN));
        }

    }

    private void prepareTrackList(Pager<PlaylistTrack> playlistTrackPager) {
        for(PlaylistTrack x : playlistTrackPager.items){
            mTracks.add(x.track.uri);
        }

        mCurrentTrack = mTracks.get(0);
        reproduceSong();
    }

    private void reproduceSong(){
        Logger.D("PlayList Uri " + mPlayList);
        mPlayer.play(mTracks);

    }


    @Override
    public void onLoggedIn() {

    }

    @Override
    public void onLoggedOut() {

    }

    @Override
    public void onLoginFailed(Throwable throwable) {

    }

    @Override
    public void onTemporaryError() {

    }

    @Override
    public void onConnectionMessage(String s) {

    }

    @Override
    public void onPlaybackEvent(EventType eventType, PlayerState playerState) {


    }

    @Override
    public void onPlaybackError(ErrorType errorType, String s) {

    }


    private Connectivity getNetworkConnectivity(Context context) {
        ConnectivityManager connectivityManager;
        connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        if (activeNetwork != null && activeNetwork.isConnected()) {
            return Connectivity.fromNetworkType(activeNetwork.getType());
        } else {
            return Connectivity.OFFLINE;
        }
    }


    private PlaylistTrack fromId(String uri){

        for(PlaylistTrack x: playlistTrack.items){
            if(uri.equals(x.track.uri)){
                return x;
            }
        }
        return null;
    }
}

package com.mxhacks.hack.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.mxhacks.hack.R;
import com.mxhacks.hack.adapter.PlayListAdapter;
import com.mxhacks.hack.utils.Global;
import com.mxhacks.hack.utils.Logger;
import com.mxhacks.hack.utils.ValueSave;
import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;

import java.util.ArrayList;
import java.util.List;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Pager;
import kaaes.spotify.webapi.android.models.PlaylistSimple;
import kaaes.spotify.webapi.android.models.UserPrivate;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class CreateMusicZoneActivity extends AppCompatActivity implements Callback<UserPrivate> {

    private static final int SPOTIFY_LOGIN_REQUEST_CODE = 1337;

    private String userId;
    private  SpotifyService spotifyService;
    private List<PlaylistSimple> playlistSimplePager;

    private PlayListAdapter mPlayListAdapter;

    private PlaylistSimple mSelectedPlaylist;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_music_zone);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        playlistSimplePager = new ArrayList<>();
        mPlayListAdapter = new PlayListAdapter(this,playlistSimplePager);

        //list view config
        ListView v = (ListView)findViewById(R.id.act_create_spotify_playlist);
        v.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        v.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                mSelectedPlaylist = playlistSimplePager.get(i);
            }

        });
        v.setAdapter(mPlayListAdapter);

        initializeSpotifyApi();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_act_create_zone, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        if(item.getItemId() == R.id.action_send){
            if(mSelectedPlaylist!=null){
                Intent i = new Intent(CreateMusicZoneActivity.this,MyZoneActivity.class);
                Logger.D("Playlist" + mSelectedPlaylist.uri + " id "+mSelectedPlaylist.id);
                i.putExtra(MyZoneActivity.PLAYLIST,mSelectedPlaylist.id);
                i.putExtra(MyZoneActivity.PLAYLIST_TITLE,mSelectedPlaylist.name);
                i.putExtra(MyZoneActivity.USER_ID,userId);
                startActivity(i);
            }else{
                Snackbar.make(findViewById(R.id.main_content_layout)," Selecciona primero una PlayList",Snackbar.LENGTH_SHORT).show();
            }
        }

            return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SPOTIFY_LOGIN_REQUEST_CODE) {
            AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, data);
            if (response.getType() == AuthenticationResponse.Type.TOKEN) {
                ValueSave.saveString(this, Global.SPOTIFY_ACCESS_TOKEN, response.getAccessToken());

                //spoty config
                SpotifyApi api = new SpotifyApi();
                api.setAccessToken(response.getAccessToken());

                spotifyService = api.getService();
                spotifyService.getMe(this);
            }
            if (response.getError() != null) {
                Logger.E("Error al iniciar sesión en spotify: " + response.getError());
            }

        }
    }


    /**********************Spotify Action Methods **************************/
    private void initializeSpotifyApi() {
        if (ValueSave.getString(this, Global.SPOTIFY_ACCESS_TOKEN) == null) {
           performLogin();
        } else {
            //spoty config
            SpotifyApi api = new SpotifyApi();
            api.setAccessToken(ValueSave.getString(this, Global.SPOTIFY_ACCESS_TOKEN));

            spotifyService = api.getService();
            spotifyService.getMe(this);

        }
    }

    private void performLogin() {

        AuthenticationRequest.Builder builder =
                new AuthenticationRequest.Builder(Global.SPOTIFY_CLIENT_ID, AuthenticationResponse.Type.TOKEN, Global.SPOTIFY_REDIRECT_URL);
        builder.setScopes(new String[]{
                "user-read-private","user-read-birthdate","user-read-email","streaming","playlist-read-collaborative","playlist-read-private","streaming"});
        AuthenticationClient.openLoginActivity(this, SPOTIFY_LOGIN_REQUEST_CODE, builder.build());
    }

    /**********************Spotify EndPointCall **************************/
    @Override
    public void success(UserPrivate userPrivate, Response response) {
        userId = userPrivate.id;
        spotifyService.getPlaylists(userId, new Callback<Pager<PlaylistSimple>>() {
            @Override
            public void success(Pager<PlaylistSimple> playList, Response response) {
                Logger.D("Número de PlaylistObtenidas " + playList.items.size());
                playlistSimplePager.addAll(playList.items);
                mPlayListAdapter.notifyDataSetChanged();

            }
            @Override
            public void failure(RetrofitError error) {
                Logger.E("PlayList Get Error ", error);
            }
        });
    }

    @Override
    public void failure(RetrofitError error) {
        Logger.E("UserMeError ",error);

    }
}

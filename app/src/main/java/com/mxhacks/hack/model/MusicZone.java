package com.mxhacks.hack.model;

import com.mxhacks.hack.utils.Logger;
import com.mxhacks.hack.utils.SerializeUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.UUID;

/**
 * Created by José Ángel García Salinas on 03/10/15.
 */
public class MusicZone extends JSONObject {

    public static final String TITLE = "title";
    public static final String PLAYLIST_ID = "playlistId";
    public static final String PEER_UUID = "peerUuid";

    public MusicZone( String title, String sPlaylistId, UUID peerUID) throws JSONException {
        this.put(TITLE,title);
        this.put(PLAYLIST_ID,sPlaylistId);
        this.put(PEER_UUID,peerUID.toString());
    }

    public byte[] toByteArray(){
        try{
            return SerializeUtils.serialize(toString());
        }catch (IOException e){

            Logger.E("Error al convertir el json a bytes ",e);
            return null;
        }
    }
}

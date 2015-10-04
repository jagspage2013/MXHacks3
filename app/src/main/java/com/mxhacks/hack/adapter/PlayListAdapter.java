package com.mxhacks.hack.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.mxhacks.hack.ui.PlaylistItem;

import java.util.ArrayList;
import java.util.List;

import kaaes.spotify.webapi.android.models.PlaylistSimple;

/**
 * Created by José Ángel García Salinas on 04/10/15.
 */
public class PlayListAdapter  extends BaseAdapter{

    private List<PlaylistSimple> mPlaylistSimples =  new ArrayList<>();
    private Context context;


    public PlayListAdapter(Context context,List<PlaylistSimple> mPlaylistSimples) {
        this.mPlaylistSimples = mPlaylistSimples;
        this.context = context;
    }

    @Override
    public int getCount() {
        return mPlaylistSimples.size();
    }

    @Override
    public Object getItem(int i) {
        return mPlaylistSimples.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {

        ViewHolder viewHolder;

        if(convertView==null){
            convertView = new PlaylistItem(context);
            viewHolder = new ViewHolder((PlaylistItem) convertView);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder)convertView.getTag();
        }

        PlaylistSimple playlistSimple = mPlaylistSimples.get(i);

        if(playlistSimple!=null){
            viewHolder.draweeView.setImageURI(Uri.parse(playlistSimple.images.get(0).url));
            viewHolder.playlistTitle.setText(playlistSimple.name);
            viewHolder.playlistNumberSongs.setText(playlistSimple.tracks.total+" canciones");
        }

        return convertView;
    }

    static class ViewHolder {

        SimpleDraweeView draweeView;
        TextView playlistTitle;
        TextView playlistNumberSongs;

        public ViewHolder(PlaylistItem v){
            draweeView = v.getDraweeView();
            playlistTitle = v.getPlaylistTitle();
            playlistNumberSongs = v.getPlaylistNumberSongs();
        }
    }




}

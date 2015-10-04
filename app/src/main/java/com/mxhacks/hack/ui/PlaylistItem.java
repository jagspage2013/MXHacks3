package com.mxhacks.hack.ui;

import android.content.Context;
import android.net.Uri;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Checkable;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.generic.RoundingParams;
import com.facebook.drawee.view.SimpleDraweeView;
import com.mxhacks.hack.R;

import kaaes.spotify.webapi.android.models.PlaylistSimple;

/**
 * Created by José Ángel García Salinas on 04/10/15.
 */
public class PlaylistItem extends RelativeLayout implements Checkable {

    private float dpParameter ;
    private boolean checked = false;

    private SimpleDraweeView draweeView;
    private TextView playlistTitle;
    private TextView playlistNumberSongs;



    public PlaylistItem(Context context) {
        super(context);
        dpParameter = context.getResources().getDisplayMetrics().density;
        int pad = (int)(16* dpParameter);
        this.setPadding(pad,pad,pad,pad);

        setSimpleDrawee();
        setText();

    }

    private void setSimpleDrawee() {

        GenericDraweeHierarchyBuilder builder= new GenericDraweeHierarchyBuilder(getContext().getResources());
        LinearLayout.LayoutParams params= new LinearLayout.LayoutParams((int)(80*dpParameter),(int)(80*dpParameter));
        RoundingParams roundingParams  = new RoundingParams();

        roundingParams.setRoundAsCircle(true);
        GenericDraweeHierarchy hierarchy = builder
                .setFadeDuration(300)
                .setRoundingParams(roundingParams)
                .build();


        draweeView = new SimpleDraweeView(getContext(),hierarchy);
        draweeView.setLayoutParams(params);
        draweeView.setId(View.generateViewId());
        this.addView(draweeView);

    }

    private void setText() {
        LinearLayout base = new LinearLayout(getContext());
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.ALIGN_BOTTOM,draweeView.getId());
        params.addRule(RelativeLayout.ALIGN_TOP,draweeView.getId());
        params.addRule(RelativeLayout.RIGHT_OF,draweeView.getId());
        params.addRule(RelativeLayout.END_OF,draweeView.getId());
        params.leftMargin = (int)(16* dpParameter);
        params.setMarginStart((int) (16 * dpParameter));
        base.setLayoutParams(params);
        base.setOrientation(LinearLayout.VERTICAL);
        base.setGravity(Gravity.CENTER_VERTICAL);


        //add textViews
        playlistTitle = new TextView(getContext());
        playlistTitle.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        playlistTitle.setTextSize(20);
        playlistTitle.setTextColor(getResources().getColor(android.R.color.black));
        playlistTitle.setText("hola");
        base.addView(playlistTitle);

        playlistNumberSongs = new TextView(getContext());
        playlistNumberSongs.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        playlistNumberSongs.setTextSize(16);
        playlistNumberSongs.setTextColor(getResources().getColor(android.R.color.darker_gray));
        playlistNumberSongs.setText("23 canciones");

        base.addView(playlistNumberSongs);

        this.addView(base);

    }


    @Override
    public void setChecked(boolean b) {
        this.checked = b;
        this.setBackgroundColor(getResources().getColor((checked)? android.R.color.darker_gray : R.color.background));
    }

    @Override
    public boolean isChecked() {
        return checked;
    }

    @Override
    public void toggle() {
        setChecked(!checked);
    }

    public SimpleDraweeView getDraweeView() {
        return draweeView;
    }

    public TextView getPlaylistTitle() {
        return playlistTitle;
    }

    public TextView getPlaylistNumberSongs() {
        return playlistNumberSongs;
    }
}

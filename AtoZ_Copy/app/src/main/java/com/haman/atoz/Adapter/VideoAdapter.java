package com.haman.atoz.Adapter;

import android.app.Activity;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.VideoView;

import com.haman.atoz.Networking.AppConfig;
import com.haman.atoz.ViewHolder.PostViewHolder;

//ADAPTER FOR VIDEO POST
public class VideoAdapter extends MediaAdapter{

    private static final String TAG = ".VideoAdapter";

    private final Activity activity;
    private final PostViewHolder postViewHolder;
    private final String videoFileUri;

    //ITS VIDEO VIEW
    private VideoView video;
    private ProgressBar buffering;

    //CREATE VIDEO VIEW TO SHOW VIDEO POST
    public VideoAdapter(Activity activity, PostViewHolder postViewHolder, String videoFileUri){

        this. activity = activity;
        this.postViewHolder = postViewHolder;
        this.videoFileUri = videoFileUri;

        video = new VideoView(activity);
        buffering = new ProgressBar(activity);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        video.setLayoutParams(params);

        postViewHolder.postLayout.addView(buffering);
        postViewHolder.postLayout.addView(video);;
    }

    public void onPlay(){

        postViewHolder.thumbnailImage.setVisibility(View.INVISIBLE);

        try{

            video.requestFocus();
            //COMPLETE TO PREPARE VIDEO RESOURCE
            video.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    buffering.setVisibility(View.INVISIBLE);
                    video.start();
                }
            });

            //VIDEO STATE INFORMATION
            video.setOnInfoListener(new MediaPlayer.OnInfoListener() {
                @Override
                public boolean onInfo(MediaPlayer mp, int what, int extra) {

                    if(what == MediaPlayer.MEDIA_INFO_BUFFERING_START){
                        buffering.setVisibility(View.INVISIBLE);
                        return true;
                    }else if(what == MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START){
                        buffering.setVisibility(View.VISIBLE);
                        return true;
                    }else if(what == MediaPlayer.MEDIA_INFO_BUFFERING_END){
                        buffering.setVisibility(View.INVISIBLE);
                        return true;
                    }
                    return false;
                }
            });

            video.setVideoURI(Uri.parse(AppConfig.BASE_URL + "/" + videoFileUri));
        }catch (Exception exception){
            Log.i(TAG, "FAIL TO UPLOAD VIDEO POST FROM SERVER");
        }
    }

    public void onRePlay(){
        video.start();
    }

    public void onPause(){
        video.pause();
        buffering.setVisibility(View.INVISIBLE);
    }

    public void onStop(){
        video.stopPlayback();

        postViewHolder.postLayout.removeView(video);
        postViewHolder.postLayout.removeView(buffering);
        postViewHolder.thumbnailImage.setVisibility(View.VISIBLE);
    }
}

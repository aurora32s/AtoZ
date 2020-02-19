package com.haman.atoz.Adpater;

import android.app.Activity;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.VideoView;

import com.haman.atoz.Data.MediaData;
import com.haman.atoz.Networking.AppConfig;
import com.haman.atoz.R;

import retrofit2.http.Url;

public class MainVideoAdapter extends MainMultiDataAdapter{

    private static final String TAG = ".MainVideoAdapter";

    //View
    private VideoView videoView;
    private ProgressBar buffering;

    //pause -> replay
    private int currentTime;

    public MainVideoAdapter(Activity parentActivity, ViewGroup container, MediaData mediaData, int layoutId, int backgroundLayoutId,
                            int txtLikeNoId, int txtCommentNoId){
        super(parentActivity,container,mediaData,layoutId,backgroundLayoutId,txtLikeNoId,txtCommentNoId);

        videoView = mediaView.findViewById(R.id.main_video);
        buffering = mediaView.findViewById(R.id.video_buffering);
    }

    //영상 재생 시작
    public void start(){
        Log.i(TAG,"START");

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, heightThumbnail);
        videoView.setLayoutParams(params);

        thumbnailImage.setVisibility(View.INVISIBLE); //썸네일 이미지 제거

        try{
            //request focus
            videoView.requestFocus();
            videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mp.setLooping(true);
                    buffering.setVisibility(View.INVISIBLE);
                    videoView.start();
                }
            });

            videoView.setOnInfoListener(new MediaPlayer.OnInfoListener() {
                @Override
                public boolean onInfo(MediaPlayer mp, int what, int extra) {

                    if(what == MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START){
                        buffering.setVisibility(View.INVISIBLE);
                        return true;
                    }else if(what == MediaPlayer.MEDIA_INFO_BUFFERING_START){
                        buffering.setVisibility(View.VISIBLE);
                        buffering.bringToFront();
                        return true;
                    }else if(what == MediaPlayer.MEDIA_INFO_BUFFERING_END){
                        buffering.setVisibility(View.INVISIBLE);
                        return true;
                    }
                    return false;
                }
            });

            buffering.setVisibility(View.VISIBLE);
            buffering.bringToFront();
            videoView.setVideoURI(Uri.parse(AppConfig.BASE_URL+"/"+currentMediaData.getMultiUri()));

        }catch (Exception exception){
            exception.printStackTrace();
        }
    }

    //영상 종료
    public void stop(){
        Log.i(TAG,"STOP");
        thumbnailImage.setVisibility(View.VISIBLE);
        thumbnailImage.bringToFront();

        this.videoView.stopPlayback();

        //videoview 의 크기를 다시 0으로( thumbnail 보다 videoview의 크기가 클 경우 밖으로 보이는 현상 방지 )
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0);
        videoView.setLayoutParams(params);
    }

    //영상 일시 정지
    public void pause(){
        Log.i(TAG,"PAUSE");
        buffering.setVisibility(View.INVISIBLE);
        videoView.pause();
        currentTime = videoView.getCurrentPosition();
    }

    //영상 다시 재생
    public void replay(){
        Log.i(TAG,"Replay");
        videoView.seekTo(currentTime);
        videoView.start();
    }
}

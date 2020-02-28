package com.haman.atoz.Adapter;

import android.media.MediaPlayer;
import android.util.Log;
import com.haman.atoz.Networking.AppConfig;
import java.io.IOException;

//ADAPTER FOR AUDIO POST
public class AudioAdapter extends MediaAdapter{

    private static final String TAG = ".AudioAdapter";

    //AUDIO CONTROL
    private MediaPlayer mediaPlayer;
    private final String audioFileUri;

    public AudioAdapter(MediaPlayer mediaPlayer, String audioFileUri){
        this.mediaPlayer = mediaPlayer;
        this.audioFileUri = audioFileUri;
    }

    public void onPlay(){
        //PLAY SONG
        try{
            mediaPlayer.reset();
            String path = AppConfig.BASE_URL + "/" + audioFileUri;

            mediaPlayer.setDataSource(path);
            mediaPlayer.prepare();
            mediaPlayer.start();
        }catch (IOException exception){
            Log.i(TAG,"ERROR UPLOAD AUDIO SOURCE FILE TO SERVER");
            exception.printStackTrace();
        }
    }

    public void onRePlay(){
        //REPLAY SONG
        mediaPlayer.start();
    }

    public void onPause(){
        mediaPlayer.pause();
    }

    public void onStop(){
        mediaPlayer.stop();
    }
}

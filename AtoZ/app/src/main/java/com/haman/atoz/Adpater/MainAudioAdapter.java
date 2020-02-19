package com.haman.atoz.Adpater;

import android.app.Activity;
import android.util.Log;
import android.view.ViewGroup;
import com.haman.atoz.Data.MediaData;

public class MainAudioAdapter extends MainMultiDataAdapter{

    private static final String TAG = ".MainAudioAdapter";

    public MainAudioAdapter(Activity parentActivity, ViewGroup container,MediaData mediaData, int layoutId, int backgroundLayoutId,
                            int txtLikeNoId, int txtCommentNoId){
        super(parentActivity,container,mediaData,layoutId,backgroundLayoutId,txtLikeNoId,txtCommentNoId);
    }

    public void print(){
        Log.i(TAG, "HELLO");
    }
}

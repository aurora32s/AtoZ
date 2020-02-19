package com.haman.atoz.Adpater;

import android.app.Activity;
import android.content.Context;
import android.media.Image;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.haman.atoz.Data.MediaData;
import com.haman.atoz.Dialog.DetailedMediaDialogForAudio;
import com.haman.atoz.Dialog.DetailedMediaDialogForVideo;
import com.haman.atoz.Networking.AppConfig;
import com.haman.atoz.R;
import com.haman.atoz.ViewHolder.UserMediaViewHolder;
import com.squareup.picasso.Picasso;

public class UserMediaAdapter implements View.OnClickListener {

    private static final String TAG = ".UserMediaAdapter";

    private Activity parentActivity;
    private MediaData currentMediaData;
    private View mediaView;

    //view holder
    private UserMediaViewHolder userViewHolder = new UserMediaViewHolder();

    //제목 길이 제한
    private final int titleSize = 30;

    public UserMediaAdapter(Activity parentActivity, MediaData mediaData, int layoutId, ViewGroup container){
        this.parentActivity = parentActivity;
        this.currentMediaData = mediaData;

        LayoutInflater inflater = (LayoutInflater)parentActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mediaView = inflater.inflate(layoutId,container,false);

        userViewHolder.thumbnail = (ImageView) mediaView.findViewById(R.id.user_thumbnail);
        userViewHolder.thumbnail.setOnClickListener(this);

        userViewHolder.txtMediaTitle = mediaView.findViewById(R.id.user_list_media_name);
        userViewHolder.txtLikeNo = mediaView.findViewById(R.id.user_list_likeNo);
        userViewHolder.txtCommentNo = mediaView.findViewById(R.id.user_list_commentNo);

        //data setting
        userViewHolder.txtLikeNo.setText(String.valueOf(currentMediaData.getLikeNo()));
        userViewHolder.txtCommentNo.setText(String.valueOf(currentMediaData.getCommentNo()));

        String title = currentMediaData.getTitle();
        String mediaType = currentMediaData.getType() == 0 ? "[Video]" : "[Audio]";
        if(title.length() > titleSize)
            title = title.substring(0,titleSize)+"....";
        userViewHolder.txtMediaTitle.setText(mediaType+title);
    }

    public void setMediaData(){
        Picasso.with(parentActivity).load(AppConfig.BASE_URL+"/"+currentMediaData.getImageUri()).into(userViewHolder.thumbnail);
    }

    public void addMediaData(LinearLayout userList){
        userList.addView(mediaView);
    }

    @Override
    public void onClick(View view){

        int type = currentMediaData.getType();

        //media 타입에 따라 dialog에서 재생
        if(type == 0){ //video
            DetailedMediaDialogForVideo videoDialog = new DetailedMediaDialogForVideo(parentActivity,currentMediaData);
            videoDialog.show();
        }
        else{ //audio
            DetailedMediaDialogForAudio audioDialog = new DetailedMediaDialogForAudio(parentActivity,currentMediaData);
            audioDialog.show();
        }
    }
}

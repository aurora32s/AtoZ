package com.haman.atoz.Adpater;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.haman.atoz.Data.MediaData;
import com.haman.atoz.Networking.AppConfig;
import com.haman.atoz.R;
import com.squareup.picasso.Picasso;

public class MainMultiDataAdapter {

    protected MediaData currentMediaData;
    protected Activity parentActivity;

    //View
    protected View mediaView;
    protected ImageView thumbnailImage;
    protected RelativeLayout backgroundLayout;
    protected TextView mediaTitle;

    //image size
    protected int heightThumbnail;

    //제목 길이 제한
    private final int titleSize = 30;

    public MainMultiDataAdapter(Activity parentActivity, ViewGroup container, MediaData mediaData, int layoutId, int backgroundLayoutId,
                                int txtLikeNoId, int txtCommentNoId) {

        this.parentActivity = parentActivity;
        this.currentMediaData = mediaData;

        LayoutInflater inflater = (LayoutInflater)parentActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mediaView = inflater.inflate(layoutId,container,false);
        backgroundLayout = mediaView.findViewById(backgroundLayoutId);

        //like no , comment no
        TextView txtLikeNo = mediaView.findViewById(txtLikeNoId);
        TextView txtCommentNo = mediaView.findViewById(txtCommentNoId);

        txtLikeNo.setText(String.valueOf(mediaData.getLikeNo()));
        txtCommentNo.setText(String.valueOf(mediaData.getCommentNo()));

        //title
        mediaTitle = mediaView.findViewById(R.id.main_media_title);

        String title = currentMediaData.getTitle();
        String mediaType = currentMediaData.getType() == 0 ? "[Video]" : "[Audio]";
        if(title.length() > titleSize)
            title = title.substring(0,titleSize)+"....";
        mediaTitle.setText(mediaType+title);
    }

    public void setThumbnailImage(final LinearLayout layoutLeft, final LinearLayout layoutRight, int thumbnailImageId) {

        thumbnailImage = mediaView.findViewById(thumbnailImageId);
        backgroundLayout.setTag(currentMediaData.getId());

        //Server 로부터 이미지 로딩
        Picasso.with(parentActivity).load(AppConfig.BASE_URL + "/" + currentMediaData.getImageUri())
                .into(thumbnailImage, new com.squareup.picasso.Callback() {
                    @Override
                    public void onSuccess() {
                        Drawable drawable = thumbnailImage.getDrawable();

                        int heightLeft = (int) layoutLeft.getTag();
                        int heightRight = (int) layoutRight.getTag();
                        heightThumbnail = drawable.getIntrinsicHeight();

                        //길이가 짧은 layout 에 이미지 추가
                        if (heightLeft <= heightRight) {
                            layoutLeft.addView(mediaView);
                            layoutLeft.setTag(heightLeft + heightThumbnail);
                        } else {
                            layoutRight.addView(mediaView);
                            layoutRight.setTag(heightRight + heightThumbnail);
                        }
                    }
                    @Override
                    public void onError() {
                    }
                });
    }

    public RelativeLayout getBackgroundLayout(){return this.backgroundLayout;}
    public MediaData getCurrentMediaData(){return this.currentMediaData;}
}

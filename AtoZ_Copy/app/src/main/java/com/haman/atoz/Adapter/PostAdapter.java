package com.haman.atoz.Adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.haman.atoz.Data.Common;
import com.haman.atoz.Dialog.DetailPostDialog;
import com.haman.atoz.Networking.AppConfig;
import com.haman.atoz.R;
import com.haman.atoz.Data.PostData;
import com.haman.atoz.ViewHolder.PostViewHolder;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

//Adapter for showing Post uploaded by Users
//Using in HomeFragment and AlbumFragment
public class PostAdapter implements View.OnClickListener, View.OnLongClickListener {

    private static final String TAG = ".PostAdapter";

    //VIEW DATA
    private Activity activity;
    private View postView;
    private PostViewHolder postViewHolder;

    //ITS POST DATA
    private PostData postData;
    private MediaAdapter mediaAdapter;
    private MediaPlayer player;

    //PREVIOUSLY SELECTED POST
    private static PostAdapter currentSelectedPost = null;

    //TITLE MAX LENGTH
    private static final int titleMaxLength = 30;
    //STATE WHETHER ITS POST IS PLAYING OR PAUSE
    private boolean isPlaying = false;
    //STATE WHETHER THIS POST IS SELECTED OR NOT
    private boolean isSelected = false;

    public PostAdapter(Activity activity, ViewGroup container, PostData postData, MediaPlayer player){

        this.activity = activity;
        this.postData = postData;
        this.player = player;

        LayoutInflater postInflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        assert postInflater != null;
        postView = postInflater.inflate(R.layout.item_post, container, false);

        postViewHolder = new PostViewHolder();

        //Relative to show post(Audio / Video)
        postViewHolder.postLayout = postView.findViewById(R.id.item_post_data);
        postViewHolder.thumbnailImage = postView.findViewById(R.id.item_post_thumbnail);
        postViewHolder.txtLikeNo = postView.findViewById(R.id.item_post_likeNo);
        postViewHolder.txtCommentNo = postView.findViewById(R.id.item_post_commentNo);
        postViewHolder.txtTitle = postView.findViewById(R.id.item_post_title);

        //INIT
        postViewHolder.txtLikeNo.setText(String.valueOf(postData.getLikeNo()));
        postViewHolder.txtCommentNo.setText(String.valueOf(postData.getCommentNo()));

        //SETTING TITLE
        String postTitle = postData.getTitle();
        if(postTitle.length() > titleMaxLength)
            postTitle = postTitle.substring(0, titleMaxLength) + "....";

        postViewHolder.txtTitle.setText(postTitle);
    }

    public void setView(final LinearLayout listLayoutLeft, final LinearLayout listLayoutRight){

        //LOADING THUMBNAIL IMAGE FROM SERVER
        Picasso.with(activity).load(AppConfig.BASE_URL + "/" + postData.getThumbnailUri())
                .into(postViewHolder.thumbnailImage, new Callback() {
                    @Override
                    public void onSuccess() {
                        //SUCCESS LOADING THUMBNAIL IMAGE FROM SERVER
                        Drawable drawable = postViewHolder.thumbnailImage.getDrawable();
                        int heightThumbnail = drawable.getIntrinsicHeight();

                        int listLayoutLeftHeight = (int)listLayoutLeft.getTag();
                        int listLayoutRightHeight = (int)listLayoutRight.getTag();

                        if(listLayoutLeftHeight < listLayoutRightHeight){
                            listLayoutLeft.addView(postView);
                            listLayoutLeft.setTag(listLayoutLeftHeight + heightThumbnail);
                        }else{
                            listLayoutRight.addView(postView);
                            listLayoutRight.setTag(listLayoutRightHeight + heightThumbnail);
                        }
                    }
                    @Override
                    public void onError() {
                        Log.i(TAG, "FAIL TO LOAD THUMBNAIL IMAGE FROM SERVER");
                    }
                });

        postView.setOnClickListener(this); // POST PLAY / PAUSE
        postView.setOnLongClickListener(this); // SHOW POST USING DIALOG
    }

    //POST PLAY OR PAUSE
    @Override
    public void onClick(View view){

        Log.i(TAG, "isSelected : "+isSelected+" / isPlaying : "+isPlaying);
        Log.i(TAG, "POST URI : "+postData.getPostUri());
        if(!isSelected){ //ADD AUDIO OR VIDEO IN RELATIVE LAYOUT

            //이전에 재생 중이던 MEDIA 정지
            if(currentSelectedPost != null)
                currentSelectedPost.stop();

            if(postData.getType() == Common.getInstance().VIDEO){ //POST == VIDEO
                mediaAdapter = new VideoAdapter(this.activity, postViewHolder, postData.getPostUri());
            }else{ //POST == AUDIO
                mediaAdapter = new AudioAdapter(player, postData.getPostUri());
            }

            isSelected = true;
            isPlaying = true;
            mediaAdapter.onPlay();

            currentSelectedPost = this;

        }else if(isPlaying){ //PAUSE
            mediaAdapter.onPause();
            isPlaying = false;
        }else{ //PLAY
            mediaAdapter.onRePlay();
            isPlaying = true;
        }
    }

    //STOP THE POST MEDIA
    private void stop(){
        if(mediaAdapter != null)
            mediaAdapter.onStop();

        isSelected = false;
        isPlaying = false;
        mediaAdapter = null;
    }

    //SHOW DIALOG
    @Override
    public boolean onLongClick(View view){

        if(!isSelected && currentSelectedPost != null){
            currentSelectedPost.stop();
        }else if(isPlaying){
            stop();
        }

        DetailPostDialog postDialog = new DetailPostDialog(activity, postData);
        postDialog.show();

        return true;
    }
}

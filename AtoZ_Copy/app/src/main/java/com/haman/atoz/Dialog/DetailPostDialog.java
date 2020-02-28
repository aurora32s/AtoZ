package com.haman.atoz.Dialog;

import android.app.Activity;
import android.app.Dialog;
import android.app.DownloadManager;
import android.content.Context;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.common.api.Api;
import com.haman.atoz.Adapter.AudioAdapter;
import com.haman.atoz.Adapter.MediaAdapter;
import com.haman.atoz.Adapter.VideoAdapter;
import com.haman.atoz.Data.Common;
import com.haman.atoz.Data.GetResponseUserData;
import com.haman.atoz.Data.PostData;
import com.haman.atoz.Data.PostResponseData;
import com.haman.atoz.Data.UserData;
import com.haman.atoz.Networking.ApiConfig;
import com.haman.atoz.Networking.AppConfig;
import com.haman.atoz.R;
import com.haman.atoz.ViewHolder.PostViewHolder;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

//SHOW DETAIL INFORMATION OF POST USING DIALOG
public class DetailPostDialog extends Dialog implements View.OnClickListener {

    private static final String TAG = ".DetailPostDialog";

    //ITS POST DATA
    private PostData postData;
    private Activity activity;

    //MANAGE VIEW
    private PostViewHolder postViewHolder;

    //EXTRA VIEW
    private ImageView userProfileImage;
    private TextView userNickname;
    private ImageView btnLike, btnComment, btnAlbum, btnMessage, btnDownload;

    //MANAGE MEDIA DATA
    private MediaAdapter mediaAdapter;
    private boolean isPlaying = true;

    public DetailPostDialog(Activity activity, PostData postData){
        super(activity);

        this.activity = activity;
        this.postData = postData;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        //DIALOG SETTING
        WindowManager.LayoutParams dialogParam = new WindowManager.LayoutParams();
        dialogParam.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        dialogParam.dimAmount = 1f;
        getWindow().setAttributes(dialogParam);

        setContentView(R.layout.dialog_detail_post);
        postViewHolder = new PostViewHolder();

        //SETTING VIEW FOR POST INFORMATION
        postViewHolder.postLayout = findViewById(R.id.dialog_post_layout);
        postViewHolder.thumbnailImage = findViewById(R.id.dialog_post_thumbnail);
        postViewHolder.txtLikeNo = findViewById(R.id.dialog_post_likeNo);
        postViewHolder.txtCommentNo = findViewById(R.id.dialog_post_commentNo);
        postViewHolder.txtTitle = findViewById(R.id.dialog_post_title);

        userProfileImage = findViewById(R.id.dialog_upload_user_profile);
        userNickname = findViewById(R.id.dialog_upload_user_nickname);
        TextView txtDescription = findViewById(R.id.dialog_post_description);
        TextView txtDate = findViewById(R.id.dialog_post_date);

        //SETTING DATA
        Picasso.with(activity).load(AppConfig.BASE_URL + "/" + postData.getThumbnailUri()).into(postViewHolder.thumbnailImage);
        postViewHolder.txtLikeNo.setText(String.valueOf(postData.getLikeNo()));
        postViewHolder.txtCommentNo.setText(String.valueOf(postData.getCommentNo()));
        postViewHolder.txtTitle.setText(postData.getTitle());

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd hh:mm a");
        txtDate.setText(dateFormat.format(postData.getDate()));

        txtDescription.setText(postData.getDescription());

        //해당 게시글을 업로드한 사용자 정보 요청
        getUserData();
        //해당 게시글을 이미 "좋아요" 표시 했는지 확인
        checkLike();

        //SETTING BUTTON
        btnLike = findViewById(R.id.dialog_button_like);
        btnComment = findViewById(R.id.dialog_button_comment);
        btnAlbum = findViewById(R.id.dialog_button_album);
        btnMessage = findViewById(R.id.dialog_button_message);
        btnDownload = findViewById(R.id.dialog_button_download);

        //SETTING LISTENER
        postViewHolder.postLayout.setOnClickListener(this);
        btnLike.setOnClickListener(this);
        btnComment.setOnClickListener(this);
        btnAlbum.setOnClickListener(this);
        btnMessage.setOnClickListener(this);
        btnDownload.setOnClickListener(this);

        //SETTING MEDIA ADAPTER
        if(postData.getType() == Common.getInstance().VIDEO){ //POST == VIDEO
            mediaAdapter = new VideoAdapter(activity, postViewHolder, postData.getPostUri());
        }else if(postData.getType() == Common.getInstance().AUDIO){ //POST == AUDIO
            mediaAdapter = new AudioAdapter(new MediaPlayer(), postData.getPostUri());
        }
        mediaAdapter.onPlay();
    }

    //REQUEST USER DATA WHO UPLOADED THIS POST
    private void getUserData(){

        String email = postData.getUserEmail();

        ApiConfig apiConfig = AppConfig.getRetrofit().create(ApiConfig.class);
        final Call<GetResponseUserData> request = apiConfig.getUserData(Common.getInstance().TEST_AUTH, email);

        request.enqueue(new Callback<GetResponseUserData>() {
            @Override
            public void onResponse(Call<GetResponseUserData> call, Response<GetResponseUserData> response) {

                //SUCCESS GET USER DATA FROM SERVER
                if(response.body().getResponseCode() == Common.getInstance().RESPONSE_SUCCESS){

                    UserData userData = response.body().getResponseBody();

                    //FOR MAKING CIRCLE IMAGE
                    userProfileImage.setBackground(new ShapeDrawable(new OvalShape()));
                    userProfileImage.setClipToOutline(true);

                    Picasso.with(activity).load(userData.getProfile()).into(userProfileImage);
                    userNickname.setText(userData.getNickname());
                }
                else if(response.body().getResponseCode() == Common.getInstance().RESPONSE_FAIL){
                    Log.i(TAG,"FAIL TO GET USER DATA FROM SERVER / SERVER PROBLEM");
                }
            }

            @Override
            public void onFailure(Call<GetResponseUserData> call, Throwable t) {
                Log.i(TAG,"FAIL TO GET USER DATA FROM SERVER");
            }
        });
    }

    //사용자가 이미 해당 게시글을 좋아요 체크 했는지 확인
    private void checkLike(){

        ApiConfig apiConfig = AppConfig.getRetrofit().create(ApiConfig.class);
        Call<PostResponseData> request = apiConfig.getLikeInfo(Common.getInstance().TEST_AUTH,
                                                                Common.getInstance().getUserData().getEmail(),
                                                                postData.getId());

        request.enqueue(new Callback<PostResponseData>() {
            @Override
            public void onResponse(Call<PostResponseData> call, Response<PostResponseData> response) {

                //좋아요 표시로 변경
                if(response.body().getResponseCode() == Common.getInstance().RESPONSE_SUCCESS){
                    btnLike.setImageDrawable(getContext().getResources().getDrawable(R.drawable.heart_2,null));
                }
                else if(response.body().getResponseCode() == Common.getInstance().RESPONSE_FAIL){
                    Log.i(TAG,"FAIL TO GET LIKE INFO FROM SERVER / SERVER PROBLEM");
                }
            }
            @Override
            public void onFailure(Call<PostResponseData> call, Throwable t) {
                Log.i(TAG,"FAIL TO GET LIKE INFO FROM SERVER");
            }
        });
    }

    @Override
    public void onClick(View view){

        if(view == postViewHolder.postLayout){ //CLICK THUMBNAIL
            if(isPlaying) {
                mediaAdapter.onPause();
                isPlaying = false;
            }
            else {
                mediaAdapter.onRePlay();
                isPlaying = true;
            }
        }
        else if(view == btnLike){ requestLikePost(); }
        else if(view == btnAlbum){ requestAddAlbum(); }
        else if(view == btnDownload){
            if(postData.isDownloadable()){
                requestDownload();
            }else{
                Common.getInstance().showMessage(activity, R.string.NON_DOWNLOADABLE);
            }
        }
    }

    //CHANGE LIKE STATE
    private void requestLikePost(){

        //USER DATA & POST DATA
        HashMap<String, String> requestBody = new HashMap<>();
        requestBody.put("requestBody","{\"user_id\":\""+Common.getInstance().getUserData().getEmail()+
                "\", \"media_id\":\""+postData.getId()+"\"}");

        ApiConfig apiConfig = AppConfig.getRetrofit().create(ApiConfig.class);
        final Call<PostResponseData> request = apiConfig.requestLike(Common.getInstance().TEST_AUTH, requestBody);

        request.enqueue(new Callback<PostResponseData>() {
            @Override
            public void onResponse(Call<PostResponseData> call, Response<PostResponseData> response) {

                //좋아요 표시 & 추가
                if(response.body().getResponseCode() == Common.getInstance().RESPONSE_SUCCESS){

                    btnLike.setImageDrawable(getContext().getResources().getDrawable(R.drawable.heart_2));
                }
                //좋아요 표시 제거
                else if(response.body().getResponseCode() == Common.getInstance().RESPONSE_EXTRA_SUCCESS){

                    btnLike.setImageDrawable(getContext().getResources().getDrawable(R.drawable.heart));
                }
                postViewHolder.txtLikeNo.setText(response.body().getResponseBody());
            }
            @Override
            public void onFailure(Call<PostResponseData> call, Throwable t) {
                Log.i(TAG, "FAIL TO REQUEST CHANGING LIKE STATE FROM SERVER");
            }
        });
    }

    //ADD POST TO ALBUM
    private void requestAddAlbum(){

        HashMap<String, String> requestBody = new HashMap<>();
        requestBody.put("requestBody","{\"user_id\":\""+ Common.getInstance().getUserData().getEmail()+
                "\", \"media_id\":\""+postData.getId()+"\"}");

        ApiConfig apiConfig = AppConfig.getRetrofit().create(ApiConfig.class);
        Call<PostResponseData> request = apiConfig.requestAddAblum(Common.getInstance().TEST_AUTH, requestBody);

        request.enqueue(new Callback<PostResponseData>() {
            @Override
            public void onResponse(Call<PostResponseData> call, Response<PostResponseData> response) {

                //사용자 앨범 리스트 추가/삭제 중 문제 발생
                if(response.body().getResponseCode() == Common.getInstance().RESPONSE_FAIL){
                    Log.i(TAG, "FAIL TO ADD ALBUM IN SERVER / SERVER PROBLEM");
                }
            }
            @Override
            public void onFailure(Call<PostResponseData> call, Throwable t) {
                Log.i(TAG, "FAIL TO ADD ALBUM IN SERVER");
            }
        });
    }

    //DOWNLOAD POST AUDIO OR VIDEO
    private void requestDownload(){

        //GET URI OF POST DATA
        String postUri = postData.getPostUri();

        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(AppConfig.BASE_URL + "/" + postUri));

        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
        request.setTitle("Download : " + postData.getTitle()); //SET TITLE IN DOWNLOAD NOTIFICATION
        request.setDescription(postData.getTitle()+" Downloading....."); //SET DESCRIPTION IN DOWNLOAD NOTIFICATION

        request.allowScanningByMediaScanner();
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

        //GET CURRENT TIMESTAMP FOR FILE NAME
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS," "+System.currentTimeMillis());

        DownloadManager manager = (DownloadManager)getContext().getSystemService(Context.DOWNLOAD_SERVICE);
        manager.enqueue(request);
    }

    //DISMISS DIALOG
    @Override
    public void dismiss(){
        super.dismiss();
        mediaAdapter.onStop();
    }
}

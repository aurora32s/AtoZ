package com.haman.atoz.Dialog;

import android.app.Dialog;
import android.app.DownloadManager;
import android.content.Context;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.media.Image;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.ViewPager;

import com.haman.atoz.Data.Common;
import com.haman.atoz.Data.MediaData;
import com.haman.atoz.Networking.ApiConfig;
import com.haman.atoz.Networking.AppConfig;
import com.haman.atoz.Networking.GetUserResponse;
import com.haman.atoz.Networking.PostResponse;
import com.haman.atoz.R;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

//dialog 오디오 재생
//오디오 제어 가능
public class DetailedMediaDialogForAudio extends Dialog implements View.OnClickListener {

    private static final String TAG = ".DetailedMediaDialogForVideo";

    //media Data 정보
    private MediaData currentMediaData;
    private Context context;

    //view
    private ViewPager viewPager;
    private ImageView audioThumbnail;

    //detailed Data
    private ImageView userProfile;
    private TextView userNickname;
    private TextView txtLikeNo;
    private TextView txtCommentNo;
    private TextView txtDate;

    //audio controller
    private MediaPlayer mediaPlayer;
    private MediaController mediaController;

    //audio flag
    private ImageView btnLike;
    private ImageView btnComment;
    private ImageView btnAlbum;
    private ImageView btnMessage;
    private ImageView btnDownload;

    //wheteher user check like
    private boolean likeMedia = false;

    @Override
    public void dismiss() {
        super.dismiss();
        mediaPlayer.release();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        layoutParams.dimAmount = 1f;
        getWindow().setAttributes(layoutParams);

        setContentView(R.layout.fragment_dialog_detailed_audio);
        audioThumbnail = findViewById(R.id.fragment_dialog_thumbnail);

        //media data setting
        userProfile = findViewById(R.id.detailed_audio_userProfile);
        userNickname = findViewById(R.id.detailed_audio_nickname);
        TextView txtMediaTitle = findViewById(R.id.detailed_audio_title);
        TextView txtMediaDescription = findViewById(R.id.detailed_audio_description);
        txtLikeNo = findViewById(R.id.detailed_audio_likeNo);
        txtCommentNo = findViewById(R.id.detailed_audio_commentNo);
        txtDate = findViewById(R.id.detailed_audio_date);

//        Log.i(TAG, "data : "+currentMediaData.getTitle());
        txtMediaTitle.setText(currentMediaData.getTitle());
        txtMediaDescription.setText(currentMediaData.getDescription());

        txtLikeNo.setText(String.valueOf(currentMediaData.getLikeNo()));
        txtCommentNo.setText(String.valueOf(currentMediaData.getCommentNo()));

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd hh:mm a");
        txtDate.setText(dateFormat.format(currentMediaData.getDate()));

        //image rounding
//        GradientDrawable drawable = (GradientDrawable)getContext().getDrawable(R.drawable.drawable_circle_image);
        userProfile.setBackground(new ShapeDrawable(new OvalShape()));
        userProfile.setClipToOutline(true);

        Picasso.with(context).load(AppConfig.BASE_URL+"/"+currentMediaData.getImageUri()).into(audioThumbnail);
        getUserData();

        //btn event setting
        btnLike = findViewById(R.id.detailed_audio_btn_like);
        btnLike.setOnClickListener(this);

        btnAlbum = findViewById(R.id.detailed_audio_btn_album);
        btnAlbum.setOnClickListener(this);

        btnDownload = findViewById(R.id.detailed_audio_btn_download);
        btnDownload.setOnClickListener(this);

        //media controller
        mediaPlayer = new MediaPlayer();
        playSong();
    }

    public DetailedMediaDialogForAudio(@NonNull Context context, MediaData currentMediaData) {
        super(context);
        this.currentMediaData = currentMediaData;
        this.context = context;
        checkLikeMedia();
    }

    //해당 미디어를 좋아요 표시했는지 않했는지 확인
    private void checkLikeMedia(){
        Log.i(TAG,"Check Like");

        //media data
//        RequestBody requestBody = RequestBody.create(MediaType.parse("text/plain"),"{\"user_id\":1, \"media_id\":"+currentMediaData.getId());
        ApiConfig request = AppConfig.getRetrofit().create(ApiConfig.class);
        Call<PostResponse> calling = request.checkLike("token", Common.getInstance().getUserData().getEmail(),currentMediaData.getId());

        calling.enqueue(new Callback<PostResponse>() {
            @Override
            public void onResponse(Call<PostResponse> call, Response<PostResponse> response) {
                Log.i(TAG,"SUCCESS / "+response.body().getResponseBody());
                int responseCode = response.body().getResponseCode();

                print(response.body().getResponseBody());
                //==REQUEST_SUCCESS : 이미 좋아요 표시함.
                if(response.body().getResponseCode() == Common.getInstance().REQUEST_SUCCESS){
                    btnLike.setImageDrawable(getContext().getResources().getDrawable(R.drawable.heart_2));
                    likeMedia = true;
                }
            }

            @Override
            public void onFailure(Call<PostResponse> call, Throwable t) {
                Log.i(TAG,"FAIL : "+t.toString());
            }
        });
    }

    //Function to play a song
    public void playSong(){

        //playsong
        try{
            mediaPlayer.reset();
            String path = AppConfig.BASE_URL+"/"+currentMediaData.getMultiUri();

            mediaPlayer.setDataSource(path);
            mediaPlayer.prepare();
            mediaController = new MediaController(context);
            mediaController.setAnchorView(audioThumbnail);
            mediaPlayer.start();
        }catch (IOException exception){
            exception.printStackTrace();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event){
//        mediaController.show();
        return false;
    }

    @Override
    public void onClick(View view){
        if(view == btnLike){ //좋야요 버튼
            changeLikeState();
        }else if(view == btnComment){ //댓글 보기 버튼

        }else if(view == btnAlbum){ //앨범 보내기 버튼
            changeAblumState();
        }else if(view == btnMessage){ //메시지 보내기 버튼

        }else if(view == btnDownload){ //다운로드 버튼
            if(currentMediaData.isDownloadable()){ //다운로드 가능
                downloadMediaData();
            }else{ //다운로드 불가능
                print("해당 게시물은 다운로드가 불가능합니다.");
            }
        }
    }

    //사용자의 앨범에 현재 media 추가
    private void changeAblumState(){
        Log.i(TAG,"add User Album");

        //media data
//        RequestBody requestBody = RequestBody.create(MediaType.parse("text/plain"),"{\"user_id\":1, \"media_id\":"+currentMediaData.getId());

        final HashMap<String,String> requestBody = new HashMap<>();
        requestBody.put("requestBody","{\"user_id\":\""+ Common.getInstance().getUserData().getEmail()+"\", \"media_id\":\""+currentMediaData.getId()+"\"}");

        ApiConfig request = AppConfig.getRetrofit().create(ApiConfig.class);
        Call<PostResponse> calling = request.addUserAlbum("token", requestBody);

        calling.enqueue(new Callback<PostResponse>() {
            @Override
            public void onResponse(Call<PostResponse> call, Response<PostResponse> response) {
                Log.i(TAG,"SUCCESS / "+response.body().getResponseBody());
                int responseCode = response.body().getResponseCode();

                print(response.body().getResponseBody());
            }

            @Override
            public void onFailure(Call<PostResponse> call, Throwable t) {
                Log.i(TAG,"FAIL : "+t.toString());
            }
        });
    }

    //좋아요 표시 변경
    private void changeLikeState(){
        Log.i(TAG,"add User Album");

        //media data
//        RequestBody requestBody = RequestBody.create(MediaType.parse("text/plain"),"{\"user_id\":1, \"media_id\":"+currentMediaData.getId());

        final HashMap<String,String> requestBody = new HashMap<>();
        requestBody.put("requestBody","{\"user_id\":\""+Common.getInstance().getUserData().getEmail()+"\", \"media_id\":\""+currentMediaData.getId()+"\"}");

        ApiConfig request = AppConfig.getRetrofit().create(ApiConfig.class);
        Call<PostResponse> calling = request.addLike("token", requestBody);

        calling.enqueue(new Callback<PostResponse>() {
            @Override
            public void onResponse(Call<PostResponse> call, Response<PostResponse> response) {
                Log.i(TAG,"SUCCESS / "+response.body().getResponseCode());
                int responseCode = response.body().getResponseCode();

                print(response.body().getResponseBody());
                //좋아요 표시
                if(responseCode == Common.getInstance().REQUEST_SUCCESS){
                    btnLike.setImageDrawable(getContext().getResources().getDrawable(R.drawable.heart_2));
                    txtLikeNo.setText(response.body().getResponseBody());
                }
                //이미 좋아요 표시함
                else if(responseCode == Common.getInstance().PUT_ALREADY_EXIST){
                    btnLike.setImageDrawable(getContext().getResources().getDrawable(R.drawable.heart));
                    txtLikeNo.setText(response.body().getResponseBody());
                }
            }

            @Override
            public void onFailure(Call<PostResponse> call, Throwable t) {
                Log.i(TAG,"FAIL : "+t.toString());
            }
        });
    }

    //download current media data
    private void downloadMediaData(){
        Log.i(TAG,"Start Download");

        //GET url / text from currentMediaData
        String mediaUrl = currentMediaData.getMultiUri();
        //create download request
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(AppConfig.BASE_URL+"/"+mediaUrl));
        //allow types of network to download files
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
        request.setTitle("Download : "+currentMediaData.getTitle()); //set title tin download notification
        request.setDescription(currentMediaData.getTitle()+" Downloading....."); //set description in download notification

        request.allowScanningByMediaScanner();
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
//        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
        //get current timestamp as file name
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS," "+System.currentTimeMillis());

        //get download service and enque file
        DownloadManager manager = (DownloadManager)getContext().getSystemService(Context.DOWNLOAD_SERVICE);
        manager.enqueue(request);
    }

    //toast print
    private void print(String message){
        Toast.makeText(getContext(),message,Toast.LENGTH_LONG).show();
    }

    //get user data
    private void getUserData(){

        String userEmail = currentMediaData.getUserId();
        Log.i(TAG,"UserEmail : "+userEmail);

        final ApiConfig request = AppConfig.getRetrofit().create(ApiConfig.class);
        Call<GetUserResponse> call = request.getUserData("token",userEmail);

        call.enqueue(new Callback<GetUserResponse>() {
            @Override
            public void onResponse(Call<GetUserResponse> call, Response<GetUserResponse> response) {
                Log.i(TAG, "RESPONSE CODE : "+response.body().getResponseCode());
                if(response.body().getResponseCode() == Common.getInstance().REQUEST_SUCCESS){ //업로드 사용자 정보 get success

                    Picasso.with(getContext()).load(response.body().getResponseBody().getProfileUrl()).into(userProfile);
                    userNickname.setText(response.body().getResponseBody().getNickname());

                }
            }

            @Override
            public void onFailure(Call<GetUserResponse> call, Throwable t) {

            }
        });
    }
}

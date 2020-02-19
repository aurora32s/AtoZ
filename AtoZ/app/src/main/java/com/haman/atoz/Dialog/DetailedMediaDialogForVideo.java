package com.haman.atoz.Dialog;

import android.app.Dialog;
import android.app.DownloadManager;
import android.content.Context;
import android.database.sqlite.SQLiteDoneException;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.ViewPager;

import com.haman.atoz.Adpater.CommentAdapter;
import com.haman.atoz.Adpater.MainAudioAdapter;
import com.haman.atoz.Adpater.MainMultiDataAdapter;
import com.haman.atoz.Adpater.MainVideoAdapter;
import com.haman.atoz.Data.Common;
import com.haman.atoz.Data.MediaData;
import com.haman.atoz.Data.UserData;
import com.haman.atoz.Fragments.HomeFragment;
import com.haman.atoz.Networking.ApiConfig;
import com.haman.atoz.Networking.AppConfig;
import com.haman.atoz.Networking.GetResponse;
import com.haman.atoz.Networking.GetUserResponse;
import com.haman.atoz.Networking.PostResponse;
import com.haman.atoz.R;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

//dialog에서 video 재생
//video 제어 가능
public class DetailedMediaDialogForVideo extends Dialog implements View.OnClickListener{

    private static final String TAG = ".DetailedMediaDialogForVideo";

    //media Data 정보
    private MediaData currentMediaData;

    //view
    private ViewPager viewPager;
    private VideoView videoView;

    //detailed Data
    private ImageView userProfile;
    private TextView userNickname;
    private TextView txtLikeNo;
    private TextView txtCommentNo;
    private TextView txtDate;

    //control video
    private boolean isPlaying = false;

    //audio/video forward / back time
    private final int FORWARD_TIME = 5000;
    private final int BACKWARD_TIME = 5000;

    //buffering
//    private ProgressBar buffering;

    //video flag
    private ImageView btnLike;
    private ImageView btnComment;
    private ImageView btnAlbum;
    private ImageView btnMessage;
    private ImageView btnDownload;

    //좋아요 표시 유무
    private boolean likeMedia = false;

    //file download
    private DownloadManager downloadManager;
    private Long queueId;

    //댓글 기능
    private ListView commentList;
    private CommentAdapter commentAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        layoutParams.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
//        layoutParams.dimAmount = 1f;
        getWindow().setAttributes(layoutParams);


//        setContentView(R.layout.dialog_detailed_media);
//        viewPager = findViewById(R.id.dialog_viewpager);

        setContentView(R.layout.fragment_dialog_detailed_video);

        //media data setting
        userProfile = findViewById(R.id.detailed_video_userProfile);
        userNickname = findViewById(R.id.detailed_video_nickname);
        TextView txtMediaTitle = findViewById(R.id.detailed_video_title);
        TextView txtMediaDescription = findViewById(R.id.detailed_video_description);
        txtLikeNo = findViewById(R.id.detailed_video_likeNo);
        txtCommentNo = findViewById(R.id.detailed_video_commentNo);
        txtDate = findViewById(R.id.detailed_video_date);

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

        //업로드한 사용자 정보 update
        getUserData();

        //btn event setting
        btnLike = findViewById(R.id.detailed_video_btn_like);
        btnLike.setOnClickListener(this);

        btnAlbum = findViewById(R.id.detailed_video_btn_album);
        btnAlbum.setOnClickListener(this);

        btnDownload = findViewById(R.id.detailed_video_btn_download);
        btnDownload.setOnClickListener(this);

        //댓글 setting
        if(currentMediaData.isCommentable()){ //댓글이 가능한 게시글
            Log.i(TAG,"Comment Length : "+currentMediaData.getComments().size());
            commentList = findViewById(R.id.detailed_video_commentList);
            commentAdapter = new CommentAdapter(getContext(), currentMediaData.getComments());
            commentList.setAdapter(commentAdapter);
        }

        //video setting
        videoView = findViewById(R.id.fragment_dialog_videoview);
//        videoView.setVideoPath(AppConfig.BASE_URL+"/"+currentMediaData.getMultiUri());
        videoView.setTag(0); //0초부터 시작

        //buffering
//        buffering = findViewById(R.id.detailed_video_buffering);

            try{
            ViewTreeObserver vto = videoView.getViewTreeObserver();
            vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    if(videoView.getMeasuredHeight() > getContext().getResources().getDisplayMetrics().heightPixels/2){
                        ViewGroup.LayoutParams params = videoView.getLayoutParams();
                        params.height = getContext().getResources().getDisplayMetrics().heightPixels/2;
                        params.width = ViewGroup.LayoutParams.WRAP_CONTENT;
                    }
                    videoView.start();
                    isPlaying = true;
                }
            });

            //request focus
            videoView.requestFocus();
            videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
//                    mp.setLooping(true);
                }
            });

            videoView.setOnInfoListener(new MediaPlayer.OnInfoListener() {
                @Override
                public boolean onInfo(MediaPlayer mp, int what, int extra) {

                    if(what == MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START){
//                        buffering.setVisibility(View.INVISIBLE);
                        return true;
                    }else if(what == MediaPlayer.MEDIA_INFO_BUFFERING_START){
//                        buffering.setVisibility(View.VISIBLE);
//                        buffering.bringToFront();
                        return true;
                    }else if(what == MediaPlayer.MEDIA_INFO_BUFFERING_END){
//                        buffering.setVisibility(View.INVISIBLE);
                        return true;
                    }
                    return false;
                }
            });

//            buffering.setVisibility(View.VISIBLE);
//            buffering.bringToFront();
            videoView.setVideoURI(Uri.parse(AppConfig.BASE_URL+"/"+currentMediaData.getMultiUri()));

        }catch (Exception exception){
            exception.printStackTrace();
        }
    }

    public DetailedMediaDialogForVideo(@NonNull Context context, MediaData currentMediaData) {
        super(context);
        this.currentMediaData = currentMediaData;
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

    //********************************************************* 파일 재생 control
    //파일 재생/정지
    private void mediaPlayOrPause() {
        if (videoView != null) {
            if (videoView.isPlaying()) { //재생 중이라면
//                buffering.setVisibility(View.INVISIBLE);
                videoView.pause();
                videoView.setTag(videoView.getCurrentPosition());
            } else { //재생 중이 아니라면
                int currentPosition = (int) videoView.getTag();
                videoView.seekTo(currentPosition);
                videoView.start();
            }
        }
    }

    //파일 뒤로 이동
    private void mediaMoveBack() {

        if (videoView != null) { //3초 전으로 이동
            int currentPosition = videoView.getCurrentPosition();
            if (currentPosition + BACKWARD_TIME >= 0) {
                videoView.seekTo(currentPosition - BACKWARD_TIME);
            } else {
                videoView.seekTo(0);
            }
        }
    }

    //파일 앞으로 이동
    private void mediaMoveForward() {

        if (videoView != null) { //5초 앞으로 이동
            int currentPosition = videoView.getCurrentPosition();
            if (currentPosition + FORWARD_TIME <= videoView.getDuration()) {
                videoView.seekTo(currentPosition + FORWARD_TIME);
            } else {
                videoView.seekTo(videoView.getDuration());
            }
        }
    }

    //사용자의 앨범에 현재 media 추가
    private void changeAblumState(){
        Log.i(TAG,"add User Album");

        //media data
//        RequestBody requestBody = RequestBody.create(MediaType.parse("text/plain"),"{\"user_id\":1, \"media_id\":"+currentMediaData.getId());

        final HashMap<String,String> requestBody = new HashMap<>();
        requestBody.put("requestBody","{\"user_id\":\""+Common.getInstance().getUserData().getEmail()+
                "\", \"media_id\":\""+currentMediaData.getId()+"\"}");

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
        requestBody.put("requestBody","{\"user_id\":\""+Common.getInstance().getUserData().getEmail()+
                "\", \"media_id\":\""+currentMediaData.getId()+"\"}");

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

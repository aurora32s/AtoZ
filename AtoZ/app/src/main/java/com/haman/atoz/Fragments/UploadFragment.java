package com.haman.atoz.Fragments;

import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.FileUtils;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.gson.JsonObject;
import com.haman.atoz.Adpater.AudioListAdapter;
import com.haman.atoz.Data.Common;
import com.haman.atoz.MainActivity;
import com.haman.atoz.Manager.AudioFileManager;
import com.haman.atoz.Networking.ApiConfig;
import com.haman.atoz.Networking.AppConfig;
import com.haman.atoz.Networking.PostResponse;
import com.haman.atoz.R;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okio.BufferedSink;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


//기본 기능 완성하면 audio/video 별로 class 묶자!
public class UploadFragment extends Fragment implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    private static final String TAG = ".UploadFragment";

    //parent activity(MainActivity)
    private MainActivity parentActivity;

    //Data Type Button
    private Button btnVideo, btnAudio;
    //Current Data Type
    private int currentDataType; //0: Video, 1: Audio

    //실제 media 데이터 view
    private RelativeLayout mediaLayout;

    //현재 media 데이터가 선택 되었는가
    //default : false
    private boolean selectMedia = false;

    //영상 업로드일 경우 새로운 videoView 동적 생성
    private VideoView videoView = null;

    //video/audio select
    private static final int SELECT_MEDIA_REQUEST_CODE = 200;
    private ProgressDialog pDialog;
    private String mediaFilePath; //video/audio 파일 uri

    //media data control
    private LinearLayout layoutBack, layoutPlay, layoutForward;

    //음성 업로드일 경우 새로운 listview 동적 생성
    private ListView audioListView;

    //audio list
    private ArrayList<HashMap<String,String>> audioList = null;
    private MediaPlayer audioPlayer; //audio player
    private ImageView thumbnailImage; //audio album image

    //audio/video forward / back time
    private final int FORWARD_TIME = 5000;
    private final int BACKWARD_TIME = 5000;

    //upload
    private ImageView btnUplaod;

    //게시글의 flag 설정
    private Switch setComment, setUserName, setDirectMessage, setDownloadable;
    //게시글의 flag
    private boolean commentable = false;
    private boolean anonymous = false;
    private boolean messagable = false;
    private boolean downloadable = false;

    //게시글 제목 , 설명문
    private EditText editTitle, editDescription;

    public UploadFragment(MainActivity parentActivity){
        this.parentActivity = parentActivity;
    }

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        //기본 layout 설정
        View uploadLayout= inflater.inflate(R.layout.fragment_upload,container,false);

        //DataType Button
        btnVideo = uploadLayout.findViewById(R.id.btnUpload_dataType_video);
        btnAudio = uploadLayout.findViewById(R.id.btnUpload_dataType_audio);

        //current Data Type
        currentDataType = 0; //default : video

        //Select Data Type
        btnVideo.setOnClickListener(this);
        btnAudio.setOnClickListener(this);

        //실제 media data view
        mediaLayout = uploadLayout.findViewById(R.id.layout_media);

        //media control view
        layoutBack = uploadLayout.findViewById(R.id.layout_back);
        layoutPlay = uploadLayout.findViewById(R.id.layout_play);
        layoutForward = uploadLayout.findViewById(R.id.layout_forward);

        //media control event
        layoutBack.setOnClickListener(this);
        layoutPlay.setOnClickListener(this);
        layoutForward.setOnClickListener(this);

        //upload dialog 초기화
        initDialog();

        //Upload button
        btnUplaod = uploadLayout.findViewById(R.id.btnUpload_upload);
        btnUplaod.setOnClickListener(this);

        //게시글 flag setting switch
        setComment = uploadLayout.findViewById(R.id.switch_comment);
        setUserName = uploadLayout.findViewById(R.id.switch_userName);
        setDirectMessage = uploadLayout.findViewById(R.id.switch_directMessage);
        setDownloadable = uploadLayout.findViewById(R.id.switch_download);

        //게시글 flag switch listener
        setComment.setOnCheckedChangeListener(this);
        setUserName.setOnCheckedChangeListener(this);
        setDirectMessage.setOnCheckedChangeListener(this);
        setDownloadable.setOnCheckedChangeListener(this);

        //게시글 제목 / 설명문
        editTitle = uploadLayout.findViewById(R.id.edit_upload_title);
        editDescription = uploadLayout.findViewById(R.id.edit_upload_description);
        return uploadLayout;
    }

    //파일 선택
    private void selectFile(){
        if(currentDataType == 0){ //현재 원하는 데이터 타입이 비디오
            selectVideoFile();
        }else{
            selectAudioFile();
        }
    }

    //음성 파일 선택
    private void selectAudioFile(){

        audioListView = new ListView(getContext());
        AudioFileManager audioManager = new AudioFileManager(getContext());
        audioList = audioManager.getAudioList();
        final AudioListAdapter audioAdapter = new AudioListAdapter(getContext(),audioList);
        audioListView.setAdapter(audioAdapter);

        //item click 시
        audioListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                //audio list 제거
                mediaLayout.removeView(audioListView);
                audioListView = null;

                //image view 생성
                thumbnailImage = new ImageView(getContext());

                //음성 앨범 사진 setting
                final Uri artwork = Uri.parse("content://media/external/audio/albumart");
                Uri albumArt = ContentUris.withAppendedId(artwork,Integer.parseInt(audioList.get(position).get("albumImage")));
                Picasso.with(getContext()).load(albumArt).into(thumbnailImage);

                //media view 가운데 정렬
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT);
                params.addRule(RelativeLayout.CENTER_HORIZONTAL,RelativeLayout.TRUE);
                params.addRule(RelativeLayout.CENTER_VERTICAL,RelativeLayout.TRUE);

                thumbnailImage.setLayoutParams(params);

                mediaLayout.addView(thumbnailImage);

                //현재 선택된 media uri
                mediaFilePath = audioList.get(position).get("path");

                try{
                    audioPlayer = new MediaPlayer();
                    audioPlayer.setDataSource(mediaFilePath);
                    audioPlayer.prepare();
                    audioPlayer.start();
                }catch (IOException exception){
                    exception.printStackTrace();
                }

                audioList = null;
            }
        });

        mediaLayout.setBackgroundColor(getActivity().getColor(R.color.upload_media_bg_on));
        mediaLayout.addView(audioListView);
        selectMedia = true;
    }

    //음성 파일 재생
    private void playAudioFile(){
        if(audioPlayer.isPlaying()){ //노래 정지
            audioPlayer.pause();
        }else{ //노래를 재생
            audioPlayer.start();
        }
    }

    //******************************************************** 영상관련
    //영상 파일 선택
    private void selectVideoFile(){
        Intent selectVideoIntent = new Intent(Intent.ACTION_GET_CONTENT);
        selectVideoIntent.setType("video/*"); //모든 video type 검색
        startActivityForResult(selectVideoIntent, SELECT_MEDIA_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode,data);

        if(resultCode == getActivity().RESULT_OK){ //success selecting video file

            if(requestCode == SELECT_MEDIA_REQUEST_CODE){

                if(data != null){
                    mediaFilePath = getPath(data.getData());
                    initializeVideoPlayer();

                    selectMedia = true;
                }

            }

        }else if(resultCode == getActivity().RESULT_CANCELED){ //cancel to select video file
            print(getString(R.string.cancel_select_video_file));
        }else{ //fail to select video file
            print(getString(R.string.fail_select_video_file));
        }
    }


    //if you click choose video button, need to get file path
    private String getPath(Uri uri){
        Cursor cursor = getActivity().getContentResolver().query(uri,null,null,null);
        cursor.moveToFirst();

        String documentId = cursor.getString(0);
        documentId = documentId.substring(documentId.lastIndexOf(":")+1);
        cursor.close();

        cursor = getActivity().getContentResolver().query(
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                null,
                MediaStore.Images.Media._ID+"=?",
                new String[]{documentId},
                null
        );
        cursor.moveToFirst();
        String path = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA));
        cursor.close();

        return path;
    }

    //video type 의 file 을 선택했을 경우, 해당 video view setting 및 재생
    private void initializeVideoPlayer(){

        //새로운 video video view 생성
        videoView = new VideoView(getContext());
        videoView.setTag(0); //현재 재생 위치

        //media view 가운데 정렬
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT);
        params.addRule(RelativeLayout.CENTER_HORIZONTAL,RelativeLayout.TRUE);
        params.addRule(RelativeLayout.CENTER_VERTICAL,RelativeLayout.TRUE);

        videoView.setLayoutParams(params);

        //media view 배경 색 -> 검은색
        mediaLayout.setBackgroundColor(getActivity().getColor(R.color.upload_media_bg_on));
        videoView.setVideoPath(mediaFilePath);
        mediaLayout.addView(videoView);
        videoView.start();

    }


    //********************************************************* 파일 재생 control
    //파일 재생/정지
    private void mediaPlayOrPause(){

        if(currentDataType == 0) { //영상 control
            if (videoView != null) {
                if (videoView.isPlaying()) { //재생 중이라면
                    videoView.pause();
                    videoView.setTag(videoView.getCurrentPosition());
                } else { //재생 중이 아니라면
                    int currentPosition = (int) videoView.getTag();
                    videoView.seekTo(currentPosition);
                    videoView.start();
                }
            }
        }else{ //audio control
            if(audioPlayer != null){
                if(audioPlayer.isPlaying()){ //재생 중이라면
                    audioPlayer.pause();
                }else{ //정지 중이라면
                    audioPlayer.start();
                }
            }
        }
    }

    //파일 뒤로 이동
    private void mediaMoveBack(){

        if(currentDataType == 0){ //영상 control
            if(videoView != null){ //3초 전으로 이동
                int currentPosition = videoView.getCurrentPosition();
                if(currentPosition+BACKWARD_TIME >= 0){
                    videoView.seekTo(currentPosition-BACKWARD_TIME);
                }else{
                    videoView.seekTo(0);
                }
            }
        }else{ //음성 control
            if(audioPlayer != null){ //
                //get current song position
                int currentPosition = audioPlayer.getCurrentPosition();
                //Check if seekBackward time is greater than 0 sec
                if(currentPosition - BACKWARD_TIME >= 0){
                    //forward song
                    audioPlayer.seekTo(currentPosition - BACKWARD_TIME);
                }else{
                    //backward to starting position
                    audioPlayer.seekTo(0);
                }
            }
        }
    }

    //파일 앞으로 이동
    private void mediaMoveForward(){

        if(currentDataType == 0){ //영상 control
            if(videoView != null){ //5초 앞으로 이동
                int currentPosition = videoView.getCurrentPosition();
                if(currentPosition + FORWARD_TIME <= videoView.getDuration()){
                    videoView.seekTo(currentPosition+FORWARD_TIME);
                }else{
                    videoView.seekTo(videoView.getDuration());
                }
            }
        }else{ //음성 control
            if(audioPlayer != null){ //
                //Get current song position
                int currentPosition = audioPlayer.getCurrentPosition();
                //check if seekForward time is lesser than song duration
                if(currentPosition + FORWARD_TIME <= audioPlayer.getDuration()){
                    //forward song
                    audioPlayer.seekTo(currentPosition + FORWARD_TIME);
                }else{
                    //forward to end position
                    audioPlayer.seekTo(audioPlayer.getDuration());
                }
            }
        }
    }

    //********************************************************* 데이터 타입 변경
    //데이터 타입 변경 시에 media view 를 reset
    private void resetMediaView(){

        if(currentDataType == 0){ //video type이 선택되어 있었다면
            videoView.stopPlayback();
            mediaLayout.removeView(videoView);
            videoView = null;
        }else if(currentDataType == 1){ //audio type이 선택되어 있었다면
            if(mediaFilePath == null){ //audio list 상태
                mediaLayout.removeView(audioListView);
                audioListView = null;
            }else{ //audio file을 선택한 이후
                audioPlayer.stop();
                mediaLayout.removeView(thumbnailImage);
                audioPlayer = null;
            }
        }

        //원상복귀
        mediaLayout.setBackgroundColor(getActivity().getColor(R.color.upload_media_bg_off));
        mediaFilePath = null;
        selectMedia = false;
    }


    //******************************************************** 파일 업로드
    //media data server upload
    private void uploadMediaToServer(){

        if(mediaFilePath == null || mediaFilePath.equals("")){
            print(getString(R.string.warning_dont_select_media));
            return;
        }else{
            //서버 업로드가 완료 될때까지 dialog 띄워줌.
            showDialog();

            //Map is used to multipart the file using okhttp3.RequestBody
            Map<String, RequestBody> body = new HashMap<>();

            File file = new File(mediaFilePath);

//            //media data
            RequestBody description = RequestBody.create(MediaType.parse("text/plain"),getRequestBody());
            //Parsing any Media type files
            RequestBody media = RequestBody.create(MediaType.parse("*/*"),file);
            MultipartBody.Part uploadFile = MultipartBody.Part.createFormData("file", file.getName(),media);

            body.put("description",description);
            body.put("file\";filename=\""+file.getName()+
                    "\";email=\""+Common.getInstance().getUserData().getEmail()+
                    "\";type=\""+currentDataType,media);

            ApiConfig requestPost = AppConfig.getRetrofit().create(ApiConfig.class);
//            Call<PostResponse> calling = requestPost.uploadPost("token",uploadFile,description);
            Call<PostResponse> calling = requestPost.uploadPost("token",body);

            calling.enqueue(new Callback<PostResponse>() {
                @Override
                public void onResponse(Call<PostResponse> call, Response<PostResponse> response) {
                    //request가 완료되었으니 progress dialog 종료
                    hideDialog();

                    int responseCode = response.body().getResponseCode();
                    if(responseCode == Common.getInstance().REQUEST_SUCCESS){ //upload 성공

                        print(getString(R.string.success_request_upload_media));
                        //해당 fragment를 종료하고 user 화면으로 이동
                        ((MainActivity)parentActivity).mediaUploadSuccess();
                    }
                }

                @Override
                public void onFailure(Call<PostResponse> call, Throwable t) {
                    //request가 완료되었으니 progress dialog 종료
                    hideDialog();

                    print(getString(R.string.fail_request_upload_media));
                    Log.i(TAG,"Error : "+t.toString());
                    //후에 에러 메시지에 따른 처리 요청
                }
            });
        }
    }

    //init dialog
    protected void initDialog(){
        pDialog = new ProgressDialog(getContext());
        pDialog.setCancelable(false);
    }

    protected void showDialog(){
        if(!pDialog.isShowing())
            pDialog.show();
    }

    protected void hideDialog(){
        if(pDialog.isShowing())
            pDialog.dismiss();
    }

    //toast
    private void print(String message){
        Toast.makeText(getContext(),message,Toast.LENGTH_LONG).show();
    }


    //************************************************************** click event
    @Override
    public void onClick(View view){

        if(view == btnVideo && currentDataType != 0){ //data type 을 video 로 변경

            btnVideo.setBackgroundColor(getActivity().getColor(R.color.dataType_button_bg_on));
            btnAudio.setBackgroundColor(getActivity().getColor(R.color.dataType_button_bg_off));

            //파일 선택 초기화
            if(selectMedia){ //파일이 선택되어 있다면
                resetMediaView();
            }

            currentDataType = 0; //video

        }else if(view == btnAudio && currentDataType != 1){ //data type 을 audio 로 변경

            btnVideo.setBackgroundColor(getActivity().getColor(R.color.dataType_button_bg_off));
            btnAudio.setBackgroundColor(getActivity().getColor(R.color.dataType_button_bg_on));

            //파일 선택 초기화
            if(selectMedia){ //파일이 선택되어 있다면
                resetMediaView();
            }

            currentDataType = 1; //audio
            selectFile();

        }else if(view == layoutPlay){ //재생, 정지
            Log.i(TAG,"PLAY");
            if(selectMedia)
                mediaPlayOrPause();
            else
                selectFile();
        }else if(view == layoutBack){ //뒤으로 이동
            Log.i(TAG,"BACK");
            if(selectMedia)
                mediaMoveBack();
            else
                selectFile();
        }else if(view == layoutForward){ //앞으로 이동
            Log.i(TAG,"FORWARD");
            if(selectMedia)
                mediaMoveForward();
            else
                selectFile();
        }else if(view == btnUplaod){ //파일 업로드
            uploadMediaToServer();
        }
    }

    //게시글 flag setting
    //##후에 class로 나누자
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if(buttonView == setComment){
            commentable = isChecked;
        }else if(buttonView == setUserName){
            anonymous = isChecked;
        }else if(buttonView == setDirectMessage){
            messagable = isChecked;
        }else if(buttonView == setDownloadable){
            downloadable = isChecked;
        }
    }

    //request body 구성
    private String getRequestBody(){

        JSONObject requestBody = new JSONObject();

        //media type
        try{
            requestBody.put("commentable",commentable);
            requestBody.put("anonymous",anonymous);
            requestBody.put("messagable",messagable);
            requestBody.put("downloadable",downloadable);
            requestBody.put("userEmail",Common.getInstance().getUserData().getEmail());
            requestBody.put("media_type", currentDataType);
            requestBody.put("title",editTitle.getText());
            requestBody.put("description",editDescription.getText());
        }catch (JSONException exception){
            Log.i(TAG,"JSON Exception : "+exception);
        }

        Log.i(TAG,"REQUEST BODY : "+requestBody.toString());
        return requestBody.toString();
    }

}


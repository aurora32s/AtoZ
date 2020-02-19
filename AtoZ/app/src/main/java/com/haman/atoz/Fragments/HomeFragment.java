package com.haman.atoz.Fragments;

import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.haman.atoz.Adpater.MainAudioAdapter;
import com.haman.atoz.Adpater.MainMultiDataAdapter;
import com.haman.atoz.Adpater.MainVideoAdapter;
import com.haman.atoz.Data.MediaData;
import com.haman.atoz.Dialog.DetailedMediaDialogForAudio;
import com.haman.atoz.Dialog.DetailedMediaDialogForVideo;
import com.haman.atoz.Networking.ApiConfig;
import com.haman.atoz.Networking.AppConfig;
import com.haman.atoz.Networking.GetResponse;
import com.haman.atoz.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment implements View.OnClickListener,MediaPlayer.OnCompletionListener, View.OnLongClickListener {

    private static final String TAG = ".HomeFragment";
    private Activity context; //HomeFragment 의 context

    //Server로 부터 전달 받은 Media 데이터 리스트
    private HashMap<String,MainMultiDataAdapter> multiAdapterList = new HashMap<>();

    //LinearLayout
    private LinearLayout layoutLeft;
    private LinearLayout layoutRight;

    //View Groupd
    private ViewGroup container;

    //현재 재생중인 media의 index
    private String currentMediaId = "";
    private int currentType = -1;
    private int playing = 0; //0: 정지, 1: 재생, 2: 일시정지

    //Audio
    private MediaPlayer mediaPlayer;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){

        //해당 fragment의 context setting
        this.context = this.getActivity();
        this.container = container;

        Log.i(TAG, "Start Home fragment");
        //기본 layout 설정
        View homeLayout= inflater.inflate(R.layout.fragment_main,container,false);

        //view setting
        layoutLeft = homeLayout.findViewById(R.id.layout_left);
        layoutRight = homeLayout.findViewById(R.id.layout_right);

        //기본 길이 setting
        layoutLeft.setTag(0);
        layoutRight.setTag(0);

        //audio
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnCompletionListener(this);

        requestMediaData();
        return homeLayout;
    }

    //Server 로 Video / Audio 데이터 요청
    private void requestMediaData(){

        ApiConfig request = AppConfig.getRetrofit().create(ApiConfig.class);
        Call<GetResponse> calling = request.getPost("token");

        calling.enqueue(new Callback<GetResponse>() {
            @Override
            public void onResponse(Call<GetResponse> call, Response<GetResponse> response) {
                Log.i(TAG,"SUCCESS / "+response.body().getResponseBody().toString());

                ArrayList<MediaData> responseBody = response.body().getResponseBody();

                for(int i = 0 ; i < responseBody.size(); i++){

                    MediaData mediaData = responseBody.get(i);
                    int dataType = mediaData.getType();

                    MainMultiDataAdapter adapter;
                    if(dataType == 0){ //video
                        adapter = new MainVideoAdapter(context,container,mediaData,R.layout.item_mainlist_for_video, R.id.main_for_video,
                                R.id.item_main_video_likeNo,R.id.item_main_video_commentNo);
                        adapter.setThumbnailImage(layoutLeft,layoutRight,R.id.thumbnail_for_video);
                    }else{ //audio
                        adapter = new MainAudioAdapter(context,container,mediaData,R.layout.item_mainlist_for_audio, R.id.main_for_audio,
                                R.id.item_main_audio_likeNo,R.id.item_main_audio_commentNo);
                        adapter.setThumbnailImage(layoutLeft,layoutRight,R.id.thumbnail_for_audio);
                    }

                    adapter.getBackgroundLayout().setOnClickListener(HomeFragment.this);
                    adapter.getBackgroundLayout().setOnLongClickListener(HomeFragment.this);

                    multiAdapterList.put(mediaData.getId(),adapter);
                }
            }

            @Override
            public void onFailure(Call<GetResponse> call, Throwable t) {
                Log.i(TAG,"FAIL : "+t.toString());
            }
        });
    }

    //Function to play a song
    public void playSong(String audioUri){

        //playsong
        try{
            mediaPlayer.reset();
            String path = AppConfig.BASE_URL+"/"+audioUri;

            mediaPlayer.setDataSource(path);
            mediaPlayer.prepare();
            mediaPlayer.start();
        }catch (IOException exception){
            exception.printStackTrace();
        }
    }

    //On Song playing completed
    @Override
    public void onCompletion(MediaPlayer arg0){
        MainAudioAdapter audioAdapter = (MainAudioAdapter)multiAdapterList.get(currentMediaId);
        playSong(audioAdapter.getCurrentMediaData().getMultiUri());
    }

    //Click event
    @Override
    public void onClick(View view){
        String index = (String)view.getTag();

        MainMultiDataAdapter adapter = multiAdapterList.get(index);
        MediaData data = adapter.getCurrentMediaData();

        int type = data.getType();

        Log.i(TAG,"OnJustClick");
        if(type == 0){ //video
            MainVideoAdapter videoAdapter = (MainVideoAdapter)adapter;
            if(currentMediaId.equals(index)){ //같은 이미지 클릭
                if(playing == 1){ //재생중
                    videoAdapter.pause();
                    playing = 2; //일시정지
                }else if(playing == 2){ //일시 정지 중
                    videoAdapter.replay();
                    playing = 1; //재생
                }
            }else { //다른 이미지 클랙
                if(playing == 1 && currentType == 1){ //오디오가 이미 재생중
                    mediaPlayer.stop();
                    mediaPlayer.reset();
                }else if( (playing==1 || playing == 2) && currentType == 0){ //비디오가 이미 재생중이거나 일시정지 중
                    MainVideoAdapter previousVideo = (MainVideoAdapter)multiAdapterList.get(currentMediaId);
                    previousVideo.stop();
                }

                videoAdapter.start();
                //새로운 data 로 설정
                playing = 1;
                currentType = 0;
                currentMediaId = index;
            }
        }
        else{ //audio
            if(currentMediaId.equals(index)){ //같은 이미지 클릭
                if(playing == 1){ //재생중
                    mediaPlayer.stop();
                    playing = 0;
                }else{ //재생 안되고 있음.
                    playSong(data.getMultiUri());
                    playing = 1;
                }
            }else{ //다른 이미지 클릭
                if( playing > 0 && currentType == 0){ //비디오가 이미 재생중이거나 일시정지
                    MainVideoAdapter videoAdapter = (MainVideoAdapter)multiAdapterList.get(currentMediaId);
                    videoAdapter.stop();
                }

                playSong(data.getMultiUri());
                //새로운 data 로 설정
                playing = 1;
                currentType = 1; //audio
                currentMediaId = index;
            }
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        //다시 home fragment로 돌아왔을 경우 각 layout의 길이를 0으로 setting
        if(layoutLeft != null){
            layoutLeft.setTag(0);
            layoutRight.setTag(0);
        }
    }

    @Override
    public boolean onLongClick(View view) {

        Log.i(TAG,"OnLongClick");
        String index = (String)view.getTag();

        MainMultiDataAdapter adapter = multiAdapterList.get(index);
        MediaData data = adapter.getCurrentMediaData();

        int type = data.getType();

        if(playing > 0){ //이미 media가 재생 중
            if(currentType == 0) { //비디오가 재생중
                MainVideoAdapter videoAdapter = (MainVideoAdapter)multiAdapterList.get(currentMediaId);
                videoAdapter.stop();
            }else{ //오디오가 재새중
                mediaPlayer.stop();
                mediaPlayer.reset();
                playing = 0; //정지
            }
        }

        //media 타입에 따라 dialog에서 재생
        if(type == 0){ //video
            DetailedMediaDialogForVideo videoDialog = new DetailedMediaDialogForVideo(getContext(),data);
            videoDialog.show();
        }
        else{ //audio
            DetailedMediaDialogForAudio audioDialog = new DetailedMediaDialogForAudio(getContext(),data);
            audioDialog.show();
        }

        return true;
    }
}

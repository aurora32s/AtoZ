package com.haman.atoz.Fragment;

import android.content.Context;
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

import com.haman.atoz.Adapter.PostAdapter;
import com.haman.atoz.Data.Common;
import com.haman.atoz.Data.GetResponseData;
import com.haman.atoz.Data.PostData;
import com.haman.atoz.Networking.ApiConfig;
import com.haman.atoz.Networking.AppConfig;
import com.haman.atoz.R;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

//UPLOAD POST USER LIKE
public class AlbumFragment extends Fragment {

    private static final String TAG = ".AlbumFragment";
    private final Context context;
    private ViewGroup container;

    //LINEAR LAYOUT FOR UPLOADING POST
    private LinearLayout albumLeft, albumRight;

    //COMMON MEDIA PLAYER FOR AUDIO POST
    private MediaPlayer player;
    //CURRENTLY SELECTED POST
    private PostAdapter currentSelectedAlbum = null;

    public AlbumFragment(){this.context = getContext();}

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){

//        Log.i(TAG, "CLICK ALBUM FRAGMENT BUTTON");
        View albumLayout = inflater.inflate(R.layout.fragment_home, container, false);
        this.container = container;
        this.player = new MediaPlayer();

        //VIEW SETTING
        albumLeft = albumLayout.findViewById(R.id.home_post_left);
        albumRight = albumLayout.findViewById(R.id.home_post_right);

        //TAG = EACH LAYOUT LENGTH
        albumLeft.setTag(0);
        albumRight.setTag(0);

        //GET POST DATA FROM SERVER
        requestAlbumData();
        return albumLayout;
    }

    //GET POST DATA FROM SERVER
    private void requestAlbumData(){

        ApiConfig apiConfig = AppConfig.getRetrofit().create(ApiConfig.class);
        Call<GetResponseData> request = apiConfig.getUserAlbum(Common.getInstance().TEST_AUTH, Common.getInstance().getUserData().getEmail());

        request.enqueue(new Callback<GetResponseData>() {
            @Override
            public void onResponse(Call<GetResponseData> call, Response<GetResponseData> response) {

                if(response.body().getResponseCode() == Common.getInstance().RESPONSE_SUCCESS){

                    ArrayList<PostData> responseBody = response.body().getResponseBody();
                    Log.i(TAG, "SUCCESS TO REQUEST ALBUM DATA -> " + responseBody.size());

                    for(int i = 0 ; i < responseBody.size(); i++){

                        PostData album = responseBody.get(i);

                        PostAdapter postAdapter = new PostAdapter(getActivity(), container, album, player);
                        postAdapter.setView(albumLeft, albumRight);
                    }
                }
                else if(response.body().getResponseCode() == Common.getInstance().RESPONSE_FAIL){
                    Log.i(TAG,"FAIL TO REQUEST ALBUM DATA TO SERVER");
                    Common.getInstance().showMessage(context, R.string.FAIL_TO_GET_POST_DATA);
                }
            }
            @Override
            public void onFailure(Call<GetResponseData> call, Throwable t) {
                Log.i(TAG, "FAIL TO REQUEST ALBUM DATA -> " + t.toString());
                Common.getInstance().showMessage(context, R.string.FAIL_TO_GET_POST_DATA);
            }
        });
    }

}

package com.haman.atoz.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.haman.atoz.Adpater.MainAudioAdapter;
import com.haman.atoz.Adpater.MainMultiDataAdapter;
import com.haman.atoz.Adpater.MainVideoAdapter;
import com.haman.atoz.Adpater.UserMediaAdapter;
import com.haman.atoz.Data.Common;
import com.haman.atoz.Data.MediaData;
import com.haman.atoz.Networking.ApiConfig;
import com.haman.atoz.Networking.AppConfig;
import com.haman.atoz.Networking.GetResponse;
import com.haman.atoz.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserFragment extends Fragment{

    private static final String TAG = ".UserFragment";

    private LinearLayout userMediaList;
    private ArrayList<UserMediaAdapter> mediaDataAdapterList = new ArrayList<>();
    private ViewGroup container;

    //view
    private TextView userLikeNo;
    private TextView userCommentNo;
    private CircleImageView userProfile;
    private TextView userEmail;
    private TextView userNickname;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        //기본 layout 설정
        View userLayout= inflater.inflate(R.layout.fragment_user,container,false);
        this.container = container;

        //view
        userLikeNo = userLayout.findViewById(R.id.user_likeNo);
        userCommentNo = userLayout.findViewById(R.id.user_commentNo);
        userProfile = userLayout.findViewById(R.id.user_profile);
        userEmail = userLayout.findViewById(R.id.user_email);
        userNickname = userLayout.findViewById(R.id.user_nickname);

        userLikeNo.setText(String.valueOf(Common.getInstance().getUserData().getLikeNo()));
        userCommentNo.setText(String.valueOf(Common.getInstance().getUserData().getCommentNo()));
        Picasso.with(getActivity()).load(Common.getInstance().getUserData().getProfileUrl()).into(userProfile);
        userEmail.setText(Common.getInstance().getUserData().getEmail());
        userNickname.setText(Common.getInstance().getUserData().getNickname());

        userMediaList = userLayout.findViewById(R.id.user_listview);

        //해당 user의 게시글 list 요청
        requestMediaData();

        return userLayout;
    }

    //Server 로 Video / Audio 데이터 요청
    private void requestMediaData(){

        ApiConfig request = AppConfig.getRetrofit().create(ApiConfig.class);
        Call<GetResponse> calling = request.getUserPost("token", Common.getInstance().getUserData().getEmail());

        calling.enqueue(new Callback<GetResponse>() {
            @Override
            public void onResponse(Call<GetResponse> call, Response<GetResponse> response) {
                Log.i(TAG,"SUCCESS / "+response.body().getResponseBody().size());
                ArrayList<MediaData> responseBody = response.body().getResponseBody();

                for(int i = 0 ; i <responseBody.size() ; i++){
                    UserMediaAdapter adapter = new UserMediaAdapter(UserFragment.this.getActivity(), responseBody.get(i),
                            R.layout.item_user_list,container);
                    adapter.setMediaData();
                    adapter.addMediaData(userMediaList);
                    mediaDataAdapterList.add(adapter);
                }
            }

            @Override
            public void onFailure(Call<GetResponse> call, Throwable t) {
                Log.i(TAG,"FAIL : "+t.toString());
            }
        });
    }
}

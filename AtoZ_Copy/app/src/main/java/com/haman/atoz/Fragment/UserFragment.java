package com.haman.atoz.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.haman.atoz.Adapter.UserPostAdapter;
import com.haman.atoz.Data.Common;
import com.haman.atoz.Data.GetResponseData;
import com.haman.atoz.Data.PostData;
import com.haman.atoz.Data.UserData;
import com.haman.atoz.Dialog.DetailPostDialog;
import com.haman.atoz.Networking.ApiConfig;
import com.haman.atoz.Networking.AppConfig;
import com.haman.atoz.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserFragment extends Fragment implements AdapterView.OnItemClickListener {

    private static final String TAG = ".UserFragment";

    //LIST VIEW ADATPER
    private ArrayList<PostData> postData = new ArrayList<>();
    private UserPostAdapter userPostAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){

        View userLayout = inflater.inflate(R.layout.fragment_user, container, false);

        //SETTING VIEW
        TextView txtLikeNo = userLayout.findViewById(R.id.user_likeNo);
        TextView txtCommentNo = userLayout.findViewById(R.id.user_commentNo);
        CircleImageView profileImage = userLayout.findViewById(R.id.user_profile);
        TextView txtNickname = userLayout.findViewById(R.id.user_nickname);
        TextView txtEmail = userLayout.findViewById(R.id.user_email);

        //SETTING DATA
        UserData user = Common.getInstance().getUserData();

        txtLikeNo.setText(String.valueOf(user.getLikeNo()));
        txtCommentNo.setText(String.valueOf(user.getCommentNo()));
        Picasso.with(getActivity()).load(user.getProfile()).into(profileImage);
        txtNickname.setText(user.getNickname());
        txtEmail.setText(user.getEmail());

        //SETTING LIST VIEW
        ListView userPostList = userLayout.findViewById(R.id.user_post_list);
        userPostAdapter = new UserPostAdapter(getContext(),postData);
        userPostList.setAdapter(userPostAdapter);

        //SETTING LISTENER
        userPostList.setOnItemClickListener(this);

        //REQUEST USER POST LIST
        requestPostList();
        return userLayout;
    }

    //REQUEST USER POST LIST TO SERVER
    private void requestPostList(){

        ApiConfig apiConfig = AppConfig.getRetrofit().create(ApiConfig.class);
        Call<GetResponseData> request = apiConfig.getUserPost(Common.getInstance().TEST_AUTH,
                                                                Common.getInstance().getUserData().getEmail());

        request.enqueue(new Callback<GetResponseData>() {
            @Override
            public void onResponse(Call<GetResponseData> call, Response<GetResponseData> response) {

                //SUCCESS TO GET USER POST LIST FROM SERVER
                if(response.body().getResponseCode() == Common.getInstance().RESPONSE_SUCCESS){

                    ArrayList<PostData> responseBody = response.body().getResponseBody();
                    for(int i = 0 ; i < responseBody.size() ; i++){
                        postData.add(responseBody.get(i));
                    }

                    userPostAdapter.notifyDataSetChanged();
                }
                else if(response.body().getResponseCode() == Common.getInstance().RESPONSE_FAIL){
                    Log.i(TAG, "FAIL TO GET USER POST LIST FROM SERVER / SERVER PROBLEM");
                }
            }
            @Override
            public void onFailure(Call<GetResponseData> call, Throwable t) {
                Log.i(TAG,"FAIL TO GET USER POST LIST FROM SERVER");
            }
        });
    }

    //LIST VIEW ITEM(POST) CLICK
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        DetailPostDialog postDialog = new DetailPostDialog(getActivity(), postData.get(position));
        postDialog.show();
    }
}

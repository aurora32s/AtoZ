package com.haman.atoz.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.haman.atoz.Data.PostData;
import com.haman.atoz.Networking.AppConfig;
import com.haman.atoz.R;
import com.haman.atoz.ViewHolder.UserPostViewHolder;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class UserPostAdapter extends BaseAdapter {

    private static final String TAG = ".UserPostAdapter";

    private ArrayList<PostData> postData;
    private LayoutInflater inflater;
    private Context context;

    public UserPostAdapter(Context context, ArrayList<PostData> postData){
        this.context = context;
        this.postData = postData;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return postData.size();
    }
    @Override
    public PostData getItem(int position) {
        return postData.get(position);
    }
    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        UserPostViewHolder postViewHolder = null;

        if(convertView == null){

            postViewHolder = new UserPostViewHolder();
            convertView = this.inflater.inflate(R.layout.item_user, parent, false);

            postViewHolder.thumbnailImage = convertView.findViewById(R.id.item_user_thumbnail);
            postViewHolder.txtTitle = convertView.findViewById(R.id.item_user_title);
            postViewHolder.txtLikeNo = convertView.findViewById(R.id.item_user_likeNo);
            postViewHolder.txtCommentNo = convertView.findViewById(R.id.item_user_commentNo);

            convertView.setTag(postViewHolder);
        }else{
            postViewHolder = (UserPostViewHolder)convertView.getTag();
        }

        PostData post = getItem(position);

        Picasso.with(context).load(AppConfig.BASE_URL + "/" +post.getThumbnailUri()).into(postViewHolder.thumbnailImage);
        postViewHolder.txtTitle.setText(post.getTitle());
        postViewHolder.txtLikeNo.setText(String.valueOf(post.getLikeNo()));
        postViewHolder.txtCommentNo.setText(String.valueOf(post.getCommentNo()));

        return convertView;
    }
}

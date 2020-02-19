package com.haman.atoz.Adpater;

import android.content.Context;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.haman.atoz.Data.CommentData;
import com.haman.atoz.Networking.AppConfig;
import com.haman.atoz.R;
import com.haman.atoz.ViewHolder.CommentViewHolder;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentAdapter extends BaseAdapter {

    private static final String TAG = ".CommentAdapter";
    private Context context;
    private ArrayList<CommentData> comments;
    private LayoutInflater inflater;

    public CommentAdapter(Context context, ArrayList<CommentData> comments){
        this.comments = comments;
        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return this.comments.size();
    }

    @Override
    public CommentData getItem(int position) {
        return this.comments.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        CommentViewHolder commentViewHolder;
        if(convertView == null){
            convertView = inflater.inflate(R.layout.item_comment,parent,false);
            commentViewHolder = new CommentViewHolder();

            commentViewHolder.profile = convertView.findViewById(R.id.comment_profile);
            commentViewHolder.nickname = convertView.findViewById(R.id.comment_nickname);
            commentViewHolder.description = convertView.findViewById(R.id.comment_description);

            convertView.setTag(commentViewHolder);
        }else{
            commentViewHolder = (CommentViewHolder)convertView.getTag();
        }

        //current Data
        CommentData comment = getItem(position);

        Picasso.with(context).load(comment.getProfile()).into(commentViewHolder.profile);
        commentViewHolder.nickname.setText(comment.getNickname());
        commentViewHolder.description.setText(comment.getDescription());

        Log.i(TAG,"Nickname : "+comment.getDescription());

        return convertView;
    }
}

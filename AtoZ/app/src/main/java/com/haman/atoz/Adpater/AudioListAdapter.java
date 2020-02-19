package com.haman.atoz.Adpater;

import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.haman.atoz.R;
import com.haman.atoz.ViewHolder.AudioViewHolder;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.zip.Inflater;

public class AudioListAdapter extends BaseAdapter {

    private static final String TAG = ".AudioListAdapter";

    private ArrayList<HashMap<String,String>> audioList;
    private LayoutInflater inflater;
    private Context context;

    public AudioListAdapter(Context context, ArrayList<HashMap<String,String>> audioList){
        this.audioList = audioList;
        this.inflater = LayoutInflater.from(context);
        this.context = context;
    }

    @Override
    public int getCount() {
        return this.audioList.size();
    }

    @Override
    public HashMap<String,String> getItem(int position) {
        return audioList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        AudioViewHolder audioView;

        if(convertView == null){

            convertView = inflater.inflate(R.layout.item_audio_list,parent,false);
            audioView = new AudioViewHolder();

            audioView.imageAlbum = convertView.findViewById(R.id.image_audio);
            audioView.title = convertView.findViewById(R.id.txt_audio_title);
            audioView.artist = convertView.findViewById(R.id.txt_audio_artist);

            convertView.setTag(audioView);

        }else{
            audioView = (AudioViewHolder)convertView.getTag();
        }

        HashMap<String, String> audio = getItem(position);

        final Uri artwork = Uri.parse("content://media/external/audio/albumart");
        Uri albumArt = ContentUris.withAppendedId(artwork,Integer.parseInt(audio.get("albumImage")));

        Picasso.with(context).load(albumArt).into(audioView.imageAlbum);
        audioView.title.setText(audio.get("title"));
        audioView.artist.setText(audio.get("artist"));

        return convertView;
    }
}

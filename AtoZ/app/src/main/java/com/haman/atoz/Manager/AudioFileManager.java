package com.haman.atoz.Manager;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;

import java.util.ArrayList;
import java.util.HashMap;

public class AudioFileManager {

    private static final String TAG = ".AudioFileManager";

    private ArrayList<HashMap<String, String>> audioList = new ArrayList<>();
    private Context context;

    public AudioFileManager(Context context){
        this.context = context;
    }

    public ArrayList<HashMap<String, String>> getAudioList(){

        Cursor cursor = context.getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,null,
                null,null);


        while(cursor.moveToNext()){
            HashMap<String, String> audio = new HashMap<>();

            audio.put("title",cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)));
            audio.put("artist",cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)));
            audio.put("album",cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM)));
            audio.put("path",cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA)));
            audio.put("albumImage",cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID)));

            audioList.add(audio);
        }

        return this.audioList;
    }
}

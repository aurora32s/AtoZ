package com.haman.atoz.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.haman.atoz.Adapter.MediaAdapter;
import com.haman.atoz.Data.Common;
import com.haman.atoz.MainActivity;
import com.haman.atoz.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class UploadFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = ".UploadFragment";

    //PARENT ACTIVITY(MAIN ACTIVITY)
    private MainActivity parent;

    //VIEW
    private Button btnVideo, btnAudio;
    private RelativeLayout mediaLayout;
    private EditText editTitle, editDescription;
    private Switch setComment, setNickname, setDirectMessage, setDownload;
    private ImageView btnUpload;

    //STATE
    private int currentPostType = Common.getInstance().VIDEO;
    private boolean selectPost = false;
    private MediaAdapter mediaAdapter;

    private boolean commentable, anonymous, messagable, downloadable;

    //VIDEO / AUDIO SELECT
    private static final int SELECT_MEDIA_FILE_CODE = 2;
    private String mediaFilePath; //선택된 VIDEO/AUDIO 파일 URI

    public UploadFragment(MainActivity parent){ this.parent = parent; }

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){

        View uploadLayout = inflater.inflate(R.layout.fragment_upload, container, false);

        //SETTING VIEW
        btnVideo = uploadLayout.findViewById(R.id.upload_button_video);
        btnAudio = uploadLayout.findViewById(R.id.upload_button_audio);

        mediaLayout = uploadLayout.findViewById(R.id.upload_media_layout);
        btnUpload = uploadLayout.findViewById(R.id.upload_button);

        editTitle = uploadLayout.findViewById(R.id.upload_edit_title);
        editDescription = uploadLayout.findViewById(R.id.upload_edit_description);

        setComment = uploadLayout.findViewById(R.id.upload_switch_comment);
        setNickname = uploadLayout.findViewById(R.id.upload_switch_nickname);
        setDirectMessage = uploadLayout.findViewById(R.id.upload_switch_message);
        setDownload = uploadLayout.findViewById(R.id.upload_switch_download);

        //SETTING LISTENER
        btnVideo.setOnClickListener(this);
        btnAudio.setOnClickListener(this);

        mediaLayout.setOnClickListener(this);
        btnUpload.setOnClickListener(this);

        return uploadLayout;
    }

    @Override
    public void onClick(View view){

        if(view == btnVideo && currentPostType != Common.getInstance().VIDEO){

            changePostType(R.drawable.fragment_upload_button_on, R.drawable.fragment_upload_button_off, Common.getInstance().VIDEO);

        }else if(view == btnAudio && currentPostType != Common.getInstance().AUDIO){

            changePostType(R.drawable.fragment_upload_button_off, R.drawable.fragment_upload_button_on, Common.getInstance().AUDIO);

        }else if(view == mediaLayout){
            selectMediaFile();
        }
    }

    //게시글 타입 변경
    private void changePostType(int btnVideoResource, int btnAudioResource, int postType){

        btnVideo.setBackgroundResource(btnVideoResource);
        btnAudio.setBackgroundResource(btnAudioResource);

        //파일 선택 초기화
        if(selectPost){
            resetPostSelected();
        }
        currentPostType = postType;
    }


    //REQUEST BODY 구성
    private String getRequestBody(){

        JSONObject requestBody = new JSONObject();

        try{

            requestBody.put("commentable",commentable);
            requestBody.put("anonymous",anonymous);
            requestBody.put("messagable",messagable);
            requestBody.put("downloadable",downloadable);
            requestBody.put("userEmail",Common.getInstance().getUserData().getEmail());
            requestBody.put("media_type", currentPostType);
            requestBody.put("title",editTitle.getText());
            requestBody.put("description",editDescription.getText());

        }catch (JSONException exception){

            Log.i(TAG,"JSON EXCEPTION IN GET REQUEST BODY FUNCTION : "+exception);
        }

        return requestBody.toString();
    }
}

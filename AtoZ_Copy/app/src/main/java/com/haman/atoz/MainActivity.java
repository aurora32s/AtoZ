package com.haman.atoz;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.haman.atoz.Fragment.AlbumFragment;
import com.haman.atoz.Fragment.HomeFragment;
import com.haman.atoz.Fragment.UploadFragment;
import com.haman.atoz.Fragment.UserFragment;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = ".MainActivity";

    //NAVIGATION BUTTON
    private ImageView btnHome, btnUpload, btnAlbum, btnUser;

    //MANAGE FRAGMENT
    private FragmentManager fragmentManager;
    private int clickedBtnID;
    private HashMap<Integer, Fragment> fragments = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //TOOL BAR SETTING
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //VIEW SETTING
        btnHome = findViewById(R.id.btn_home);
        btnUpload = findViewById(R.id.btn_upload);
        btnAlbum = findViewById(R.id.btn_album);
        btnUser = findViewById(R.id.btn_user);

        //CLICK LISTENER SETTING
        btnHome.setOnClickListener(this);
        btnUpload.setOnClickListener(this);
        btnAlbum.setOnClickListener(this);
        btnUser.setOnClickListener(this);

        //INIT
        fragmentManager = getSupportFragmentManager();

        fragments.put(R.id.btn_home, new HomeFragment());
        fragments.put(R.id.btn_upload, new UploadFragment(this));
        fragments.put(R.id.btn_album, new AlbumFragment());
        fragments.put(R.id.btn_user, new UserFragment());

        clickedBtnID = R.id.btn_home;
        changeFragment();
    }

    @Override
    public void onClick(View view){

        if(clickedBtnID != view.getId()){

            clickedBtnID = view.getId();
            changeFragment();
        }
    }

    //Change Fragment is selected by user
    private void changeFragment(){
        if(fragments.containsKey(clickedBtnID)){

            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.frameLayout_main, fragments.get(clickedBtnID));
            transaction.commit();
        }
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        clickedBtnID = R.id.btn_home;
        finish();
    }

    //MOVE TO USER FRAGMENT IF UPLOADING POST TO SERVER IS SUCCEEDED IN UPLOAD FRAGMENT
    public void successPostUpload(){
        clickedBtnID = R.id.btn_user;
        changeFragment();
    }
}

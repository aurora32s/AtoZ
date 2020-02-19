package com.haman.atoz;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.haman.atoz.Data.Common;
import com.haman.atoz.Fragments.HomeFragment;
import com.haman.atoz.Fragments.UploadFragment;
import com.haman.atoz.Fragments.UserAlbumFragment;
import com.haman.atoz.Fragments.UserFragment;
import com.haman.atoz.Permission.PermissionChecker;
import com.haman.atoz.Permission.PermissionsActivity;

//Google login Activity
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = ".MainActivity";

    //check permission
    private PermissionChecker permissionChecker;

    //Fragment Manager
    private FragmentManager fragmentManager = getSupportFragmentManager();
    private int currentFragmentId = R.id.btnHome;
    private Fragment currentFragment;

    //fragment
    private HomeFragment homeFragment;
    private UploadFragment uploadFragment;
    private UserAlbumFragment albumFragment;
    private UserFragment userFragment;

    //button navigation button
    private ImageView btnHome, btnUpload, btnMyList, btnUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //permission
        permissionChecker = new PermissionChecker(this);

        //permission check
        if(permissionChecker.lacksPermissions(Common.getInstance().PERMISSIOS)){
            requestPermissions();
        }

        btnHome = findViewById(R.id.btnHome);
        btnUpload = findViewById(R.id.btnUpload);
        btnMyList = findViewById(R.id.btnMyList);
        btnUser = findViewById(R.id.btnUser);

        //set click event
        btnHome.setOnClickListener(this);
        btnUpload.setOnClickListener(this);
        btnMyList.setOnClickListener(this);
        btnUser.setOnClickListener(this);

        //fragment 초기화
        homeFragment = new HomeFragment();
        uploadFragment = new UploadFragment(this);
        albumFragment = new UserAlbumFragment();
        userFragment = new UserFragment();

        //처음 시작 시, homeFragment 부터 시작
        changeFragment(homeFragment);
    }

    @Override
    public void onClick(View view){

        if(currentFragmentId != view.getId()){
            //현재 fragment 에 메모리 정리
//            fragmentManager.popBackStackImmediate();

            //현재 보여주고 있는 fragment 에서 손을 놓음
            //후에 가비지 컬렉터가 수거
            if(view == btnHome){
//                currentFragment = homeFragment; //메인 화면
                changeFragment(homeFragment);
            }else if(view == btnUpload){
//                currentFragment = new UploadFragment(this); //게시글 업로드
                changeFragment(uploadFragment);
            }else if(view == btnMyList){
//                currentFragment = new UserAlbumFragment();
                changeFragment(albumFragment);
            }else if(view == btnUser){
//                currentFragment = new UserFragment(); //사용자 화면
                changeFragment(userFragment);
            }

//            changeFragment(currentFragment);
            currentFragmentId = view.getId();
        }
    }

    //선택한 fragment 로 변경
    private void changeFragment(Fragment fragment){
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_main,fragment);
        transaction.commit();
    }

    //request permission
    private void requestPermissions(){
        PermissionsActivity.getInstance().requestPermissions(this);
    }

    //back button click 시 완전히 앱 종료
    @Override
    public void onBackPressed(){
        super.onBackPressed();
        currentFragmentId = R.id.btnHome;
        finish();
    }

    //file upload success
    //업로드가 성공하면 user fragment로 이동
    public void mediaUploadSuccess(){
        //현재 fragment 에 메모리 정리
        fragmentManager.popBackStack();
        //현재 보여주고 있는 fragment 에서 손을 놓음
        //후에 가비지 컬렉터가 수거
        currentFragment = null;
        currentFragment = new UserFragment(); //사용자 화면

        changeFragment(currentFragment);
        currentFragmentId = btnUser.getId();
    }
}

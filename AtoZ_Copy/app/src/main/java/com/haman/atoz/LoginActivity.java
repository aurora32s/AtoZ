package com.haman.atoz;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.haman.atoz.Data.Common;
import com.haman.atoz.Data.GetResponseUserData;
import com.haman.atoz.Data.UserData;
import com.haman.atoz.Networking.ApiConfig;
import com.haman.atoz.Networking.AppConfig;
import com.haman.atoz.Permission.CheckPermission;
import com.haman.atoz.Permission.RequestPermission;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = ".LoginActivity";

    //GOOGLE LOGIN
    private SignInButton btnGoogleLogin;
    private GoogleSignInClient signInClient;

    //AUTO LOGIN SERVICE
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    //PREFERENCES
    private static final String PREF_LOGIN_KEY = "LOGIN";
    private static final String PREF_ISLOGIN_KEY = "ISLOGIN";
    private static final String PREF_EMAIL = "EMAIL";

    //USER DATA
    private static final String USER_EMAIL = "EMAIL";
    private static final String USER_NICKNAME = "NICKNAME";
    private static final String USER_PROFILE = "PROFILE";

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //CHECK PERMISSION
        CheckPermission checkPermission = new CheckPermission(LoginActivity.this);
        if(checkPermission.lacksPermissions(Common.getInstance().PERMISSIONS)){

            RequestPermission requestPermission = new RequestPermission(LoginActivity.this);
            requestPermission.requestPermission();
        }

        //AUTO LOGIN
        preferences = getSharedPreferences(PREF_LOGIN_KEY, MODE_PRIVATE);
        if(preferences.getBoolean(PREF_ISLOGIN_KEY,false)){

            HashMap<String, String> userData = new HashMap<>();
            userData.put(USER_EMAIL, preferences.getString(PREF_EMAIL,""));

            requestLogin(userData);
        }
        else{ //TRY TO GOOGLE LOGIN

            btnGoogleLogin = findViewById(R.id.btn_google_login);

            //Google Login
            GoogleSignInOptions signInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.default_web_client_id))
                    .requestEmail()
                    .build();

            signInClient = GoogleSignIn.getClient(LoginActivity.this, signInOptions);
            btnGoogleLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i(TAG,"CLICK LOGIN BUTTON");

                    //REQUEST GOOGLE LOGIN
                    Intent signInIntent = signInClient.getSignInIntent();
                    startActivityForResult(signInIntent, Common.getInstance().SIGN_IN_REQUEST_CODE);
                }
            });
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode,data);

        //GOOGLE LOGIN RESPONSE
        if(resultCode == RESULT_OK && data != null){

            if(requestCode == Common.getInstance().SIGN_IN_REQUEST_CODE){
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

                try{
                    GoogleSignInAccount account = task.getResult(ApiException.class);

                    HashMap<String, String> userData = new HashMap<>();
                    userData.put(USER_EMAIL, account.getEmail()); //GOOGLE EMAIL
                    userData.put(USER_NICKNAME, account.getGivenName()); //GOOGLE NICKNAME
                    userData.put(USER_PROFILE, account.getPhotoUrl().toString()); //PROFILE URI

                    requestLogin(userData);

                }catch (ApiException execption){
                    Log.e(TAG, "GOOGLE LOGIN EXCEPTION -> "+execption.toString());
                    Common.getInstance().showMessage(LoginActivity.this, R.string.FAIL_TO_SIGN_IN);
                }
            }

        }else{
            Log.i(TAG, "FAIL TO SIGN IN GOOGLE");
            Common.getInstance().showMessage(LoginActivity.this, R.string.FAIL_TO_SIGN_IN);
        }
    }

    //REQUEST SIGN IN TO SERVER
    private void requestLogin(HashMap<String, String> userData){

        ApiConfig apiConfig = AppConfig.getRetrofit().create(ApiConfig.class);
        Call<GetResponseUserData> requestLogin = apiConfig.signInGoogle(Common.getInstance().TEST_AUTH, userData);

        requestLogin.enqueue(new Callback<GetResponseUserData>() {
            @Override
            public void onResponse(Call<GetResponseUserData> call, Response<GetResponseUserData> response) {

                Log.i(TAG, "RESPONSE DATA : " + response.body().getResponseBody().getEmail());
                if(response.body().getResponseCode() == Common.getInstance().RESPONSE_SUCCESS){

                    //set UserData to Common
                    UserData userData = response.body().getResponseBody();
                    Common.getInstance().setUserData(userData);

                    //setting AUTO LOGIN
                    editor = preferences.edit();
                    editor.putString(PREF_EMAIL, userData.getEmail());
                    editor.putBoolean(PREF_ISLOGIN_KEY, true);
                    editor.commit();

                    //Move to Main Activity
                    Intent moveToMainView = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(moveToMainView);

                    finish();
                }
                else if(response.body().getResponseCode() == Common.getInstance().RESPONSE_FAIL){
                    Log.i(TAG, "FAIL TO SIGN IN IN SERVER");
                    Common.getInstance().showMessage(LoginActivity.this, R.string.FAIL_TO_SIGN_IN);
                }
            }

            @Override
            public void onFailure(Call<GetResponseUserData> call, Throwable t) {
                Log.i(TAG, "FAIL TO SIGN IN -> "+ t.toString());
                Common.getInstance().showMessage(LoginActivity.this, R.string.FAIL_TO_SIGN_IN);
            }
        });
    }
}

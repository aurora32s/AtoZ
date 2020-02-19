package com.haman.atoz;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.haman.atoz.Data.Common;
import com.haman.atoz.Networking.ApiConfig;
import com.haman.atoz.Networking.AppConfig;
import com.haman.atoz.Networking.GetResponse;
import com.haman.atoz.Networking.GetUserResponse;
import com.haman.atoz.Networking.PostResponse;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = ".LoginActivity";

    //google login
    private SignInButton btnGoogleLogin; //login button
    private static final int SIGN_IN_REQUEST_CODE = 100; //google login request result code
    private FirebaseAuth firebaseAuth; //firebase instance object
    private GoogleSignInClient googleSignInClient; //google api client

    //test
    private TextView email;
    private ImageView profile;

    //자동 로그인 기능
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //test view
        email = findViewById(R.id.google_email);
        profile = findViewById(R.id.google_profile);

        //자동 로그인 기능
        pref = this.getSharedPreferences("Login", Context.MODE_MULTI_PROCESS);

        if(pref.getBoolean("isLogin",false)){ //이미 한번 로그인
            HashMap<String,String> body = new HashMap<>();
            body.put("email",pref.getString("userEmail",""));
            requestLogin(body);
        }
        else{//로그인 시도
            //create firebase instance
            firebaseAuth = FirebaseAuth.getInstance();
            btnGoogleLogin = findViewById(R.id.btn_Login);

            //google login
            GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.default_web_client_id))
                    .requestEmail()
                    .build();

            googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);
            btnGoogleLogin = findViewById(R.id.btn_Login);
            btnGoogleLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i(TAG,"Login Click");
                    //request google login
                    Intent signInIntent = googleSignInClient.getSignInIntent();
                    startActivityForResult(signInIntent,SIGN_IN_REQUEST_CODE);
                }
            });
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode,data);

        //google login button response
        if(requestCode == SIGN_IN_REQUEST_CODE){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try{
                //google login success
                GoogleSignInAccount account = task.getResult(ApiException.class);

                HashMap<String, String> body = new HashMap<>();
                body.put("email",account.getEmail());
                body.put("nickname",account.getGivenName());
                body.put("profile", account.getPhotoUrl().toString());

                requestLogin(body);

            }catch (ApiException exception){

            }
        }
    }

    //로그인 요청
    private void requestLogin(HashMap<String, String> body){
        final ApiConfig request = AppConfig.getRetrofit().create(ApiConfig.class);
        Call<GetUserResponse> call = request.loginGoogle("token", body);

        call.enqueue(new Callback<GetUserResponse>() {
            @Override
            public void onResponse(Call<GetUserResponse> call, Response<GetUserResponse> response) {
                Log.i(TAG,"RESPONSE : "+response.body().getResponseBody().getEmail());
                if(response.body().getResponseCode() == Common.getInstance().REQUEST_SUCCESS){ //로그인 성공
                    Common.getInstance().setUserData(response.body().getResponseBody());

                    editor = pref.edit();
                    //로그인은 한 번만
                    editor.putString("userEmail",response.body().getResponseBody().getEmail());
                    editor.putBoolean("isLogin",true);
                    editor.commit();

                    //로그인 성공 시 메인 화면으로 이동
                    Intent moveToMainView = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(moveToMainView);

                    finish();
                }
            }

            @Override
            public void onFailure(Call<GetUserResponse> call, Throwable t) {
                Log.i(TAG, "Response Error : "+t.toString());

            }
        });


    }
}

package com.haman.atoz.Networking;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiConfig {
    //get post uploaded by users
    @GET("getPost")
    Call<GetResponse> getPost(
            @Header("Authorization") String authorization
    );

    //get user album post list
    @GET("getUserAlbum/{userId}")
    Call<GetResponse> getUserAlbum(
            @Header("Authorization") String authorization,
            @Path("userId") String userEmail
    );

    //get post only uploaded by user
    @GET("getUserPost/{userEmail}")
    Call<GetResponse> getUserPost(
            @Header("Authorization") String authorization,
            @Path("userEmail") String userEmail
    );

    //check whether I have checked 'like' this media
    @GET("checkLike/{userEmail}/{mediaId}")
    Call<PostResponse> checkLike(
            @Header("Authorization") String authorization,
            @Path("userEmail") String userEmail,
            @Path("mediaId") String mediaId
    );

    //get user data
    @GET("userData/{userEmail}")
    Call<GetUserResponse> getUserData(
            @Header("Authroziation") String authorization,
            @Path("userEmail") String userEmail
    );

    //upload post(audio / video file) to server
    @Multipart
    @POST("uploadPost")
    Call<PostResponse> uploadPost(
            @Header("Authorization") String authorization,
            @PartMap Map<String, RequestBody> requestBody
//            @Part MultipartBody.Part file,
//            @Part("description") RequestBody description
    );

    //add user album media list
    @PUT("changeAlbumState")
    Call<PostResponse> addUserAlbum(
            @Header("Authorization") String authorization,
            @Body HashMap<String,String> requestBody
    );

    //check 'like' this media
    @PUT("changeLikeState")
    Call<PostResponse> addLike(
            @Header("Authorization") String authorization,
            @Body HashMap<String,String> requestBody
    );

    //google login
    @PUT("signIn/google")
    Call<GetUserResponse> loginGoogle(
            @Header("Authorization") String authorization,
            @Body HashMap<String,String> requestBody
    );
}

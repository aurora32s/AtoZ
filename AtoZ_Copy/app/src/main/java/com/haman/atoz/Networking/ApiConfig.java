package com.haman.atoz.Networking;

import com.haman.atoz.Data.GetResponseUserData;
import com.haman.atoz.Data.GetResponseData;
import com.haman.atoz.Data.PostResponseData;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ApiConfig {

    //REQUEST LOGIN TO SERVER
    @PUT("signIn/google")
    Call<GetResponseUserData> signInGoogle(
            @Header("Authorization") String auth,
            @Body HashMap<String, String> requestBody
    );

    //REQUEST CHANGING LIKE STATE
    @PUT("changeLikeState")
    Call<PostResponseData> requestLike(
            @Header("Authorization") String auth,
            @Body HashMap<String, String> requestBody
    );

    //ADD POST TO ALBUM
    @PUT("changeAlbumState")
    Call<PostResponseData> requestAddAblum(
            @Header("Authorization") String auth,
            @Body HashMap<String, String> requestBody
    );

    //GET POST DATA UPLOADED BY USERS
    @GET("getPost")
    Call<GetResponseData> getPostData(
            @Header("Authorization") String auth
    );

    //GET ALBUM DATA USER LIKE
    @GET("getUserAlbum/{userId}")
    Call<GetResponseData> getUserAlbum(
            @Header("Authorization") String auth,
            @Path("userId") String email
    );

    //GET SPECIFIC USER DATA
    @GET("userData/{userEmail}")
    Call<GetResponseUserData> getUserData(
            @Header("Authorization") String auth,
            @Path("userEmail") String email
    );

    //CHECK WHETHER USER HAVE CHECKED 'LIKE' THIS POST
    @GET("checkLike/{userEmail}/{mediaId}")
    Call<PostResponseData> getLikeInfo(
            @Header("Authorization") String auth,
            @Path("userEmail") String email,
            @Path("mediaId") String postId
    );

    //REQUEST USER POST TO SERVER
    @GET("getUserPost/{userEmail}")
    Call<GetResponseData> getUserPost(
            @Header("Authorization") String auth,
            @Path("userEmail") String email
    );
}

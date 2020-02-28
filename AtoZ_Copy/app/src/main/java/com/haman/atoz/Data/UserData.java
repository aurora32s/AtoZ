package com.haman.atoz.Data;

import com.google.gson.annotations.SerializedName;

//USER DATA FROM SERVER
public class UserData {

    @SerializedName("email") String email;
    @SerializedName("nickname") String nickname;
    @SerializedName("profile") String profile;
    @SerializedName("like_no") int likeNo;
    @SerializedName("comment_no") int commentNo;

    public String getEmail(){return this.email;}
    public String getNickname(){return this.nickname;}
    public String getProfile(){return this.profile;}
    public int getLikeNo(){return this.likeNo;}
    public int getCommentNo(){return this.commentNo;}
}

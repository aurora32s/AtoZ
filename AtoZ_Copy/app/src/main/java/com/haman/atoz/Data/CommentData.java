package com.haman.atoz.Data;

import com.google.gson.annotations.SerializedName;

//댓글 DATA FROM SERVER
public class CommentData {

    @SerializedName("nickname") String nickname; //업로드한 사용자 nickname
    @SerializedName("profile") String profile; //업로드한 사용자 프로필 사진
    @SerializedName("comment") String comment; //댓글

    public CommentData(String nickname, String profile, String comment){
        this.nickname = nickname;
        this.profile = profile;
        this.comment = comment;
    }

    public String getNickname(){return this.nickname;}
    public String getProfile(){return this.profile;}
    public String getComment(){return this.comment;}
}

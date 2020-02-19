package com.haman.atoz.Data;

import com.google.gson.annotations.SerializedName;

public class UserData {
    @SerializedName("email") String email;
    @SerializedName("nickname") String nickname;
    @SerializedName("profile") String profileUrl;
    @SerializedName("like_no") int likeNo;
    @SerializedName("comment_no") int commentNo;

    public UserData(String email, String nickname, String profileUrl, int likeNo, int commentNo) {
        this.email = email;
        this.nickname = nickname;
        this.profileUrl = profileUrl;
        this.likeNo = likeNo;
        this.commentNo = commentNo;
    }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }
    public String getProfileUrl() { return profileUrl; }
    public void setProfileUrl(String profileUrl) { this.profileUrl = profileUrl; }
    public int getLikeNo() { return likeNo; }
    public void setLikeNo(int likeNo) { this.likeNo = likeNo; }
    public int getCommentNo() { return commentNo; }
    public void setCommentNo(int commentNo) { this.commentNo = commentNo; }
}


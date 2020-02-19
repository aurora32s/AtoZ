package com.haman.atoz.Data;

import com.google.gson.annotations.SerializedName;

public class CommentData {

    @SerializedName("nickname") String nickname;
    @SerializedName("profile") String profile;
    @SerializedName("description") String description;

    public CommentData(String nickname, String profile, String description){
        this.nickname = nickname;
        this.profile = profile;
        this.description = description;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}

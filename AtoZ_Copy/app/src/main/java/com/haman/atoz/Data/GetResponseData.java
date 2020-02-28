package com.haman.atoz.Data;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

//RESPONSE DATA FROM SERVER
public class GetResponseData {

    @SerializedName("responseCode") int responseCode;
    @SerializedName("responseBody") ArrayList<PostData> responseBody;

    public int getResponseCode(){return this.responseCode;}
    public ArrayList<PostData> getResponseBody(){return this.responseBody;}
}

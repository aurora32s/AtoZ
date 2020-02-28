package com.haman.atoz.Data;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Date;

//POST DATA UPLOADED BY USERS
public class PostData {

    @SerializedName("_id") String id; //POST UNIQUE ID
    @SerializedName("user_id") String userEmail; //USER EMAIL WHO UPLOAD
    @SerializedName("media_type") int type; //0 : VIDEO, 1 : AUDIO
    @SerializedName("thumbnail") String thumbnailUri; //POST THUMBNAIL SERVER URI
    @SerializedName("media") String postUri; //POST SERVER URI
    @SerializedName("title") String title; //POST TITLE
    @SerializedName("description") String description; //ABOUT POST
    @SerializedName("commentable") boolean commentable; //WHETHER CAN MAKE COMMENT OR NOT
    @SerializedName("anonymous") boolean anonymous;
    @SerializedName("messagable") boolean messagable;
    @SerializedName("downloadable") boolean downloadable;
    @SerializedName("like_no") int likeNo;
    @SerializedName("comment_no") int commentNo;
    @SerializedName("date") Date date;
    @SerializedName("comment") ArrayList<CommentData> comments;

    public PostData(String id, String userEmail, int type, String thumbnailUri,
                    String postUri, String title, String description, boolean commentable, boolean anonymous,
                    boolean messagable, boolean downloadable, int likeNo, int commentNo, Date date, ArrayList<CommentData> comments){
        this.id = id;
        this.userEmail = userEmail;
        this.thumbnailUri = thumbnailUri;
        this.postUri = postUri;
        this.title = title;
        this.description = description;
        this.commentable = commentable;
        this.anonymous = anonymous;
        this.messagable = messagable;
        this.downloadable = downloadable;
        this.likeNo = likeNo;
        this.commentNo = commentNo;
        this.date = date;
        this.comments = comments;
    }

    public String getId(){return this.id;}
    public String getUserEmail(){return this.userEmail;}
    public int getType(){return this.type;}
    public String getThumbnailUri(){return this.thumbnailUri;}
    public String getPostUri(){return this.postUri;}
    public String getTitle(){return this.title;}
    public String getDescription(){return this.description;}
    public boolean isCommentable(){return this.commentable;}
    public boolean isAnonymous(){return this.anonymous;}
    public boolean isMessagable(){return this.messagable;}
    public boolean isDownloadable(){return this.downloadable;}
    public int getLikeNo(){return this.likeNo;}
    public int getCommentNo(){return this.commentNo;}
    public Date getDate(){return this.date;}
    public ArrayList<CommentData> getComments(){return this.comments;}
}

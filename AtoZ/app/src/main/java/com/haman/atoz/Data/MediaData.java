package com.haman.atoz.Data;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Date;

public class MediaData {

    @SerializedName("_id") String id;
    @SerializedName("user_id") String userEmail;
    @SerializedName("media_type") int type;
    @SerializedName("thumbnail") String imageUri;
    @SerializedName("media") String multiUri;
    @SerializedName("title") String title;
    @SerializedName("description") String description;
    @SerializedName("commentable") boolean commentable;
    @SerializedName("anonymouse") boolean anonymous;
    @SerializedName("messagable") boolean messagable;
    @SerializedName("downloadable") boolean downloadable;
    @SerializedName("like_no") int likeNo;
    @SerializedName("comment_no") int commentNo;
    @SerializedName("date") Date date;
    @SerializedName("comment") ArrayList<CommentData> comments;

    public MediaData(String id, String userEmail, int type, String imageUri, String multiUri,
                     String title, String description, boolean commentable, boolean anonymous,
                     boolean messagable, boolean downloadable, int likeNo, int commentNo, Date date, ArrayList<CommentData> comments){
        this.id = id;
        this.userEmail = userEmail;
        this.type = type;
        this.imageUri = imageUri;
        this.multiUri = multiUri;
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
    public int getType(){return this.type;}
    public String getImageUri(){return this.imageUri;}
    public String getMultiUri(){return this.multiUri;}
    public String getUserId() { return userEmail; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public boolean isCommentable() { return commentable; }
    public boolean isAnonymous() { return anonymous; }
    public boolean isMessagable() { return messagable; }
    public boolean isDownloadable() { return downloadable; }
    public int getLikeNo() { return likeNo; }
    public int getCommentNo() { return commentNo; }
    public Date getDate() { return date; }
    public ArrayList<CommentData> getComments(){return this.comments;}
    public void setComments(ArrayList<CommentData> comments){this.comments = comments;}
    public CommentData getCommentItem(int position){return comments.get(position);}
    public void setCommentItem(CommentData commentItem){this.comments.add(commentItem);}

}

package com.restaurant.tasteflow;

public class Reviews {
    private String userId;
    private String userName;
    private double userRating;
    private String comment;

    public Reviews(String userId, String userName, double userRating, String comment) {
        this.userId = userId;
        this.userName = userName;
        this.userRating = userRating;
        this.comment = comment;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public double getUserRating() {
        return userRating;
    }

    public void setUserRating(double userRating) {
        this.userRating = userRating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}

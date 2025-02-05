package com.restaurant.tasteflow;

import java.util.List;

public class Product {

    private String productId;
    private String category;
    private String productImg;
    private String productName;
    private double productRating;
    private double productPrice;
    private String productContains;
    private List<String> ingredients;
    private String termsAndConditionsOfStorage;
    private List<Reviews> reviews;
    private boolean available;


    public Product(String productId, String category, String productImg, String productName, double productRating, double productPrice, String productContains, List<String> ingredients, String termsAndConditionsOfStorage, List<Reviews> reviews, boolean available) {
        this.productId = productId;
        this.category = category;
        this.productImg = productImg;
        this.productName = productName;
        this.productRating = productRating;
        this.productPrice = productPrice;
        this.productContains = productContains;
        this.ingredients = ingredients;
        this.termsAndConditionsOfStorage = termsAndConditionsOfStorage;
        this.reviews = reviews;
        this.available = available;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getProductImg() {
        return productImg;
    }

    public void setProductImg(String productImg) {
        this.productImg = productImg;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public double getProductRating() {
        return productRating;
    }

    public void setProductRating(double productRating) {
        this.productRating = productRating;
    }

    public double getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(double productPrice) {
        this.productPrice = productPrice;
    }

    public String getProductContains() {
        return productContains;
    }

    public void setProductContains(String productContains) {
        this.productContains = productContains;
    }

    public List<String> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<String> ingredients) {
        this.ingredients = ingredients;
    }

    public String getTermsAndConditionsOfStorage() {
        return termsAndConditionsOfStorage;
    }

    public void setTermsAndConditionsOfStorage(String termsAndConditionsOfStorage) {
        this.termsAndConditionsOfStorage = termsAndConditionsOfStorage;
    }

    public List<Reviews> getReviews() {
        return reviews;
    }

    public void setReviews(List<Reviews> reviews) {
        this.reviews = reviews;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }
}

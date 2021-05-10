package com.unitedwebspace.punchcard.models;

/**
 * Created by sonback123456 on 4/16/2018.
 */

public class Card {
    int _idx = 0;
    int _bidx = 0;
    int punches = 3;
    int businessHoles = 3;
    int rewards = 0;
    String cardColor = "";
    String logo = "";
    String backgroundImage = "";
    int cardNumbers = 0;
    String businessFont = "";
    String rewardFont = "";
    String businessName = "";
    String rewardName = "";

    String name = "";
    String email = "";
    String phoneNumber = "";
    String websiteUrl = "";
    String socialMediaLink = "";
    String notes = "";

    public Card(){

    }

    public void setBusinessHoles(int businessHoles) {
        this.businessHoles = businessHoles;
    }

    public int getBusinessHoles() {
        return businessHoles;
    }

    public void set_bidx(int _bidx) {
        this._bidx = _bidx;
    }

    public void setBackgroundImage(String backgroundImage) {
        this.backgroundImage = backgroundImage;
    }

    public void setCardNumbers(int cardNumbers) {
        this.cardNumbers = cardNumbers;
    }

    public int get_bidx() {
        return _bidx;
    }

    public String getBackgroundImage() {
        return backgroundImage;
    }

    public int getCardNumbers() {
        return cardNumbers;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setWebsiteUrl(String websiteUrl) {
        this.websiteUrl = websiteUrl;
    }

    public void setSocialMediaLink(String socialMediaLink) {
        this.socialMediaLink = socialMediaLink;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getWebsiteUrl() {
        return websiteUrl;
    }

    public String getSocialMediaLink() {
        return socialMediaLink;
    }

    public String getNotes() {
        return notes;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    public void setRewardName(String rewardName) {
        this.rewardName = rewardName;
    }

    public String getBusinessName() {
        return businessName;
    }

    public String getRewardName() {
        return rewardName;
    }

    public int get_idx() {
        return _idx;
    }

    public void set_idx(int _idx) {
        this._idx = _idx;
    }

    public void setPunches(int punches) {
        this.punches = punches;
    }

    public void setRewards(int rewards) {
        this.rewards = rewards;
    }

    public void setCardColor(String cardColor) {
        this.cardColor = cardColor;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public void setBusinessFont(String businessFont) {
        this.businessFont = businessFont;
    }

    public void setRewardFont(String rewardFont) {
        this.rewardFont = rewardFont;
    }

    public int getPunches() {
        return punches;
    }

    public int getRewards() {
        return rewards;
    }

    public String getCardColor() {
        return cardColor;
    }

    public String getLogo() {
        return logo;
    }

    public String getBusinessFont() {
        return businessFont;
    }

    public String getRewardFont() {
        return rewardFont;
    }
}

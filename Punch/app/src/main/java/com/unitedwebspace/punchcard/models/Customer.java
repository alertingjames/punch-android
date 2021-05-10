package com.unitedwebspace.punchcard.models;

/**
 * Created by sonback123456 on 4/17/2018.
 */

public class Customer {
    int _idx = 0;
    String name = "";
    String email = "";
    int punches = 3;
    int rewards = 0;
    String cardColor = "";
    String logo = "";
    String businessFont = "";
    String rewardFont = "";
    String businessName = "";
    String rewardName = "";

    public Customer(){

    }

    public void set_idx(int _idx) {
        this._idx = _idx;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    public void setRewardName(String rewardName) {
        this.rewardName = rewardName;
    }

    public int get_idx() {
        return _idx;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
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

    public String getBusinessName() {
        return businessName;
    }

    public String getRewardName() {
        return rewardName;
    }
}

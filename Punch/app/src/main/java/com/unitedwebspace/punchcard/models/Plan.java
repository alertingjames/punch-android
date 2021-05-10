package com.unitedwebspace.punchcard.models;

/**
 * Created by sonback123456 on 4/20/2018.
 */

public class Plan {
    int _idx = 0;
    int plan = 0;
    int cards = 0;
    float amount = 0.0f;
    String productID = "";

    public Plan(){

    }

    public void setProductID(String productID) {
        this.productID = productID;
    }

    public String getProductID() {
        return productID;
    }

    public void setCards(int cards) {
        this.cards = cards;
    }

    public int getCards() {
        return cards;
    }

    public void set_idx(int _idx) {
        this._idx = _idx;
    }

    public void setPlan(int plan) {
        this.plan = plan;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public int get_idx() {
        return _idx;
    }

    public int getPlan() {
        return plan;
    }

    public float getAmount() {
        return amount;
    }
}

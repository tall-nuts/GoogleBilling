package com.google.billing.model;

import com.android.billingclient.api.Purchase;
import java.io.Serializable;

/**
 * @author gaopengfei on 2019/08/15.
 */
public class PurchaseInfo implements Serializable {

    private String developerPayload;
    private String orderId;
    private String originalJson;
    private String packageName;
    private int purchaseState;
    private long purchaseTime;
    private String purchaseToken;
    private String signature;
    private String sku;

    public PurchaseInfo() {
    }

    public PurchaseInfo(Purchase purchase) {
        developerPayload = purchase.getDeveloperPayload();
        orderId = purchase.getOrderId();
        originalJson = purchase.getOriginalJson();
        packageName = purchase.getPackageName();
        purchaseState = purchase.getPurchaseState();
        purchaseTime = purchase.getPurchaseTime();
        purchaseToken = purchase.getPurchaseToken();
        signature = purchase.getSignature();
        sku = purchase.getSku();
    }

    public String getDeveloperPayload() {
        return developerPayload;
    }

    public void setDeveloperPayload(String developerPayload) {
        this.developerPayload = developerPayload;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getOriginalJson() {
        return originalJson;
    }

    public void setOriginalJson(String originalJson) {
        this.originalJson = originalJson;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public int getPurchaseState() {
        return purchaseState;
    }

    public void setPurchaseState(int purchaseState) {
        this.purchaseState = purchaseState;
    }

    public long getPurchaseTime() {
        return purchaseTime;
    }

    public void setPurchaseTime(long purchaseTime) {
        this.purchaseTime = purchaseTime;
    }

    public String getPurchaseToken() {
        return purchaseToken;
    }

    public void setPurchaseToken(String purchaseToken) {
        this.purchaseToken = purchaseToken;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    @Override
    public String toString() {
        return "PurchaseInfo{" +
                "developerPayload='" + developerPayload + '\'' +
                ", orderId='" + orderId + '\'' +
                ", originalJson='" + originalJson + '\'' +
                ", packageName='" + packageName + '\'' +
                ", purchaseState=" + purchaseState +
                ", purchaseTime=" + purchaseTime +
                ", purchaseToken='" + purchaseToken + '\'' +
                ", signature='" + signature + '\'' +
                ", sku='" + sku + '\'' +
                '}';
    }
}

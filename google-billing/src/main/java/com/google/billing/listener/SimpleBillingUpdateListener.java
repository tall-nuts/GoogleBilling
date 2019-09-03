package com.google.billing.listener;

import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.SkuDetails;
import java.util.List;

/**
 * @author gaopengfei on 2019/08/14.
 */
public abstract class SimpleBillingUpdateListener extends BaseBillingUpdateListener {

    @Override
    public void onQuerySkuDetailSuccess(List<SkuDetails> skuDetailsList) {

    }

    @Override
    public void onQuerySkuDetailFailure(int errorCode, String message) {

    }

    @Override
    public void onConsumeFinished(String token, BillingResult result) {

    }

    @Override
    public void onAcknowledgePurchaseResponse(BillingResult result) {

    }

    @Override
    public abstract void onBillingClientSetupFinished();

    @Override
    public abstract void onPurchasesUpdated(List<Purchase> purchases);

    @Override
    public abstract void onPurchasesCancel();

    @Override
    public abstract void onPurchasesFailure(int errorCode, String message);
}

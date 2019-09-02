package com.google.billing.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.Purchase;
import com.google.billing.model.PurchaseInfo;

import java.util.List;

import static com.google.billing.constant.Constant.ACTION_BILLING;

/**
 * @author gaopengfei on 2019/08/15.
 */
public abstract class BillingPurchasesReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (ACTION_BILLING.equals(action)){
            int code = intent.getIntExtra("code", 0);
            String message = intent.getStringExtra("message");
            if (code == BillingClient.BillingResponseCode.OK){
                PurchaseInfo purchaseInfo = (PurchaseInfo) intent.getSerializableExtra("data");
                onPurchasesUpdated(purchaseInfo);
            }else if (code == BillingClient.BillingResponseCode.USER_CANCELED){
                onPurchasesCancel();
            }else{
                onPurchasesFailure(code, message);
            }
        }
    }

    public abstract void onPurchasesUpdated(PurchaseInfo purchaseInfo);

    public abstract void onPurchasesCancel();

    public abstract void onPurchasesFailure(int errorCode, String message);
}
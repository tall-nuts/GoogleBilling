package com.google.billing;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;
import com.google.billing.constant.Constant;
import com.google.billing.listener.BaseBillingUpdateListener;
import com.google.billing.receiver.BillingPurchasesReceiver;
import com.google.billing.ui.PurchasesActivity;
import com.google.billing.utils.LogUtils;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

/**
 * @author gaopengfei on 2019/05/06.
 */
public class BillingManager implements PurchasesUpdatedListener {

    private BillingClient mBillingClient;
    private boolean mIsServiceConnected;
    private WeakReference<Activity> weakReference;
    private BaseBillingUpdateListener billingUpdatesListener;
    private BillingPurchasesReceiver billingPurchasesReceiver;

    public BillingManager(Activity activity, BaseBillingUpdateListener billingUpdatesListener) {
        this.weakReference = new WeakReference<>(activity);
        this.billingUpdatesListener = billingUpdatesListener;
    }

    public BillingManager(Activity activity, BillingPurchasesReceiver billingPurchasesReceiver) {
        this.weakReference = new WeakReference<>(activity);
        this.billingPurchasesReceiver = billingPurchasesReceiver;
        IntentFilter intentFilter = new IntentFilter(Constant.ACTION_BILLING);
        LocalBroadcastManager.getInstance(activity).registerReceiver(billingPurchasesReceiver, intentFilter);
    }

    /**
     * 连接谷歌商店(异步)
     */
    public void startServiceConnection(final Runnable runnable) {
        mBillingClient = BillingClient
                .newBuilder(weakReference.get().getApplicationContext())
                .enablePendingPurchases()
                .setListener(this)
                .build();
        mBillingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(BillingResult billingResult) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    // The BillingClient is ready. You can query purchases here.
                    if (billingUpdatesListener != null) {
                        billingUpdatesListener.onBillingClientSetupFinished();
                    }
                    mIsServiceConnected = true;
                    LogUtils.e("Google Play connect success!");
                    if (runnable != null) {
                        runnable.run();
                    }
                }
            }

            @Override
            public void onBillingServiceDisconnected() {
                // Try to restart the connection on the next request to
                // Google Play by calling the startConnection() method.
                mIsServiceConnected = false;
                LogUtils.e("Google Play connect fail!");
            }
        });
    }

    /**
     * 异步查询商品信息
     *
     * @param skuId 商品唯一ID
     * @param billingType 商品类型 详见{@link com.android.billingclient.api.BillingClient.SkuType}
     */
    public void querySkuDetailAsyn(String skuId, final String billingType) {
        LogUtils.e("异步查询商品详情-->[" + skuId + ",type:" + billingType + "]");
        List<String> skuList = new ArrayList<>();
        skuList.add(skuId);
        SkuDetailsParams.Builder params = SkuDetailsParams.newBuilder();
        params.setSkusList(skuList).setType(billingType);
        mBillingClient.querySkuDetailsAsync(params.build(),
                new SkuDetailsResponseListener() {
                    @Override
                    public void onSkuDetailsResponse(BillingResult billingResult, List<SkuDetails> skuDetailsList) {
                        // Process the result.
                        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && skuDetailsList != null) {
                            if (billingUpdatesListener != null) {
                                billingUpdatesListener.onQuerySkuDetailSuccess(skuDetailsList);
                            }
                            if (!skuDetailsList.isEmpty()) {
                                for (SkuDetails skuDetails : skuDetailsList) {
                                    LogUtils.e("异步查询商品详情成功--->[skuDetails:" + skuDetails.toString() + "]");
                                }
                            }
                        } else {
                            if (billingUpdatesListener != null) {
                                billingUpdatesListener.onQuerySkuDetailFailure(billingResult.getResponseCode(), billingResult.getDebugMessage());
                            }
                        }
                    }
                });
    }

    /**
     * <p>
     * 急速购买
     * 商品查询成功后直接进入购买流程，如果查询商品失败则直接执行{@link BaseBillingUpdateListener#onPurchasesFailure(int, String)}
     * </p>
     *
     * @param skuId 商品ID
     * @param billingType 商品类型
     */
    public void quicknessPurchase(String skuId, String billingType) {
        LogUtils.e("异步查询商品详情-->[" + skuId + ",type:" + billingType + "]");
        List<String> skuList = new ArrayList<>();
        skuList.add(skuId);
        SkuDetailsParams.Builder params = SkuDetailsParams.newBuilder();
        params.setSkusList(skuList).setType(billingType);
        mBillingClient.querySkuDetailsAsync(params.build(),
                new SkuDetailsResponseListener() {
                    @Override
                    public void onSkuDetailsResponse(BillingResult billingResult, List<SkuDetails> skuDetailsList) {
                        // Process the result.
                        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && skuDetailsList != null) {
                            if (!skuDetailsList.isEmpty()) {
                                for (SkuDetails skuDetails : skuDetailsList) {
                                    // 发起内购
                                    purchase(skuDetails);
                                    LogUtils.e("查询商品详情成功--->[skuDetails:" + skuDetails.toString() + "]");
                                }
                            }
                        } else {
                            if (billingUpdatesListener != null) {
                                billingUpdatesListener.onPurchasesFailure(billingResult.getResponseCode(), billingResult.getDebugMessage());
                            }
                        }
                    }
                });
    }

    public void quicknessPurchase(String url) {
        Intent intent = new Intent(weakReference.get(), PurchasesActivity.class);
        intent.putExtra("url", url);
        weakReference.get().startActivity(intent);
    }

    /**
     * 发起内购
     *
     * @param skuDetails 商品详情
     */
    private void purchase(final SkuDetails skuDetails) {
        // Retrieve a value for "skuDetails" by calling querySkuDetailsAsync().
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                BillingFlowParams flowParams = BillingFlowParams.newBuilder()
                        .setSkuDetails(skuDetails)
                        .build();
                int responseCode = mBillingClient.launchBillingFlow(weakReference.get(), flowParams).getResponseCode();
                LogUtils.e("支付响应码::--->[responseCode:" + responseCode + "]");
            }
        };
        executeServiceRequest(runnable);
    }

    /**
     * 连接断开重试策略
     */
    private void executeServiceRequest(Runnable runnable) {
        if (mIsServiceConnected) {
            runnable.run();
        } else {
            startServiceConnection(runnable);
        }
    }

    /**
     * 购买交易更新
     */
    @Override
    public void onPurchasesUpdated(BillingResult billingResult, @Nullable List<Purchase> purchases) {
        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && purchases != null) {
            if (billingUpdatesListener != null) {
                billingUpdatesListener.onPurchasesUpdated(purchases);
            }
            LogUtils.e("支付成功 --> [code："
                    + billingResult.getResponseCode() + ",message：" + billingResult.getDebugMessage() + "]");
        } else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.USER_CANCELED) {
            // Handle an error caused by a user cancelling the purchase flow.
            if (billingUpdatesListener != null) {
                billingUpdatesListener.onPurchasesCancel();
            }
            LogUtils.e("支付取消 --> [code："
                    + billingResult.getResponseCode() + ",message：" + billingResult.getDebugMessage() + "]");
        } else {
            // Handle any other error codes.
            if (billingUpdatesListener != null) {
                billingUpdatesListener.onPurchasesFailure(billingResult.getResponseCode(), billingResult.getDebugMessage());
            }
            LogUtils.e("支付出错 --> [code："
                    + billingResult.getResponseCode() + ",message：" + billingResult.getDebugMessage() + "]");
        }
    }

    /**
     * 回收资源
     */
    public void destroy() {
        LogUtils.d("Destroying the manager.");
        if (mBillingClient != null && mBillingClient.isReady()) {
            mBillingClient.endConnection();
            mBillingClient = null;
        }
        if (billingPurchasesReceiver != null) {
            LocalBroadcastManager.getInstance(weakReference.get()).unregisterReceiver(billingPurchasesReceiver);
        }
    }
}

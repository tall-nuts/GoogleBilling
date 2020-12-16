package cn.imeina.googlebilling;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.Purchase;
import com.google.billing.BillingManager;
import com.google.billing.listener.BaseBillingUpdateListener;
import com.google.billing.listener.SimpleBillingUpdateListener;
import com.google.billing.model.PurchaseInfo;
import com.google.billing.receiver.BillingPurchasesReceiver;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private BillingManager billingManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.btn_pay1).setOnClickListener(this);
        findViewById(R.id.btn_pay2).setOnClickListener(this);
    }

    // Google原生支付回调
    BaseBillingUpdateListener billingUpdateListener = new SimpleBillingUpdateListener() {
        @Override
        public void onBillingClientSetupFinished() {
            if (billingManager != null) {
                billingManager.launchBillingFlow("90days", BillingClient.SkuType.SUBS);
            }
        }

        @Override
        public void onPurchasesUpdated(List<Purchase> purchases) {
            Toast.makeText(MainActivity.this, "购买成功", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "购买成功：" + purchases.get(0).toString());
        }

        @Override
        public void onPurchasesCancel() {
            Toast.makeText(MainActivity.this, "取消购买", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "取消购买");
        }

        @Override
        public void onPurchasesFailure(int errorCode, String message) {
            Toast.makeText(MainActivity.this,
                    "购买失败[code：" + errorCode + ",message：" + message + "]", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "购买失败[code：" + errorCode + ",message：" + message + "]");
        }
    };

    // H5方式支付回调
    BillingPurchasesReceiver billingPurchasesReceiver = new BillingPurchasesReceiver() {
        @Override
        public void onPurchasesUpdated(PurchaseInfo purchaseInfo) {
            Toast.makeText(MainActivity.this, "购买成功", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "购买成功：" + purchaseInfo.toString());
        }

        @Override
        public void onPurchasesCancel() {

            Toast.makeText(MainActivity.this, "取消购买", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "取消购买");
        }

        @Override
        public void onPurchasesFailure(int errorCode, String message) {
            Toast.makeText(MainActivity.this,
                    "购买失败[code：" + errorCode + ",message：" + message + "]", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "购买失败[code：" + errorCode + ",message：" + message + "]");
        }
    };

    /**
     * 原生支付
     */
    public void pay1() {
        billingManager = new BillingManager(this, billingUpdateListener);
        billingManager.startServiceConnection(null);
    }

    /**
     * H5支付
     */
    public void pay2() {
        BillingManager billingManager = new BillingManager(this, billingPurchasesReceiver);
        billingManager.quicknessPurchase("");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_pay1:
                pay1();
                break;
            case R.id.btn_pay2:
                pay2();
                break;
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (billingManager != null){
            billingManager.destroy();
        }
    }
}

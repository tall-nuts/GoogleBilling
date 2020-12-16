package com.google.billing.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.net.http.SslError;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewStub;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.Purchase;
import com.google.billing.BillingManager;
import com.google.billing.R;
import com.google.billing.constant.Constant;
import com.google.billing.databinding.ActivityPurchasesBinding;
import com.google.billing.listener.SimpleBillingUpdateListener;
import com.google.billing.model.PurchaseInfo;
import com.google.billing.utils.LogUtils;
import com.google.billing.utils.NetworkUtils;
import java.util.List;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

/**
 * @author gaopengfei on 2019/08/15.
 */
public class PurchasesActivity extends AppCompatActivity {

    private ActivityPurchasesBinding binding;

    /**
     * 是否已经加载过
     */
    private boolean isLoaded;
    /**
     * 谷歌内购工具类
     */
    private static BillingManager billingManager;
    /**
     * 链接地址
     */
    private String url;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_purchases);
        initView();
        initData();
    }

    @SuppressLint("SetJavaScriptEnabled")
    public void initView() {
        //避免视频闪屏和透明问题
        getWindow().setFormat(PixelFormat.TRANSLUCENT);

        //WebSettings
        WebSettings settings = binding.wb.getSettings();
        //设置页面自适应屏幕
        settings.setUseWideViewPort(true);
        //缩放至屏幕的大小
        settings.setLoadWithOverviewMode(true);
        settings.setDomStorageEnabled(true);
        //设置允许访问文件
        settings.setAllowFileAccess(true);

        //禁止自动缩放
        settings.setUseWideViewPort(false);
        settings.setSupportZoom(false);
        settings.setBuiltInZoomControls(false);
        settings.setUseWideViewPort(false);

        //隐藏WebView缩放按钮
        settings.setDisplayZoomControls(false);
        //开启JavaScript
        settings.setJavaScriptEnabled(true);
        //开启APP缓存
        settings.setAppCacheEnabled(true);
        //禁止开启多窗口
        settings.setSupportMultipleWindows(true);
        //设置默认缓存模式
        settings.setCacheMode(WebSettings.LOAD_DEFAULT);

        // 添加JS与Native交互
        binding.wb.addJavascriptInterface(new JsBridgerHandler(this), "jsBridgerHandler");

        //支持网页内获取焦点
        binding.wb.setFocusable(true);
        binding.wb.requestFocus();

        //禁止长按复制
        binding.wb.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return true;
            }
        });

        binding.wb.setWebChromeClient(new WebChromeClient() {

            @Override
            public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                return super.onJsAlert(view, url, message, result);
            }

            @Override
            public void onProgressChanged(WebView webView, int progress) {
                if (progress == 100) {
                    isComplete(true);
                }
                super.onProgressChanged(webView, progress);
            }
        });

        binding.wb.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView webView, String url) {
                if (!url.contains("tel:")) {
                    //页面上有数字会导致连接电话
                    webView.loadUrl(url);
                    LogUtils.e("url:" + url);
                }
                return true;
            }

            @Override
            public void onPageFinished(WebView webView, String s) {
                binding.tvTitle.setText(webView.getTitle() + "");
                isComplete(true);
            }

            @Override
            public void onPageStarted(WebView webView, String s, Bitmap bitmap) {
                isLoaded = true;
                startLoading();
            }

            @Override
            public void onReceivedError(WebView webView, int i, String s, String s1) {
                isComplete(false);
            }

            @Override
            public void onReceivedSslError(WebView webView, final SslErrorHandler sslErrorHandler, SslError sslError) {
                // 遇到证书错误让用户选择是否继续？不处理Google Play会拒审
                AlertDialog.Builder builder = new AlertDialog.Builder(PurchasesActivity.this);
                builder.setMessage("The certificate may not be secure, whether to continue?");
                builder.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sslErrorHandler.proceed();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sslErrorHandler.cancel();
                    }
                });
                builder.setOnKeyListener(new DialogInterface.OnKeyListener() {
                    @Override
                    public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                        if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                            sslErrorHandler.cancel();
                            dialog.dismiss();
                            return true;
                        }
                        return false;
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        binding.itvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        binding.networkError.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refresh();
            }
        });
    }

    /**
     * 加载数据
     */
    public void initData() {
        binding.tvTitle.setText(R.string.loading);
        Intent intent = getIntent();
        url = intent.getStringExtra("url");
        if (TextUtils.isEmpty(url)) {
            Toast.makeText(this, R.string.parameter_error, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        //加载
        loadUrl();
    }

    /**
     * 加载准备
     */
    private void startLoading() {
        binding.loading.setVisibility(View.VISIBLE);
    }

    /**
     * 加载完毕
     */
    private void isComplete(boolean isComplete) {
        binding.loading.setVisibility(View.GONE);
        if (isComplete) {
            showNetworkStatus(false);
            binding.wb.setVisibility(View.VISIBLE);
        } else {
            if (!NetworkUtils.isNetworkConnected(this)) {
                binding.wb.setVisibility(View.GONE);
                showNetworkStatus(true);
            } else {
                showNetworkStatus(false);
                binding.wb.setVisibility(View.VISIBLE);
            }
        }
    }

    /**
     * 设置网络状态UI
     *
     * @param visible 是否显示网络错误布局
     */
    private void showNetworkStatus(boolean visible) {
        binding.networkError.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    /**
     * 加载页面
     */
    private void loadUrl() {
        if (isLoaded) {
            //重新加载
            binding.wb.reload();
            LogUtils.e("WebReload...");
        } else {
            if (!TextUtils.isEmpty(url)) {
                binding.wb.loadUrl(url);
            }
            LogUtils.e("WebLoad...");
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            //返回WebView的上一页面
            if (binding.wb.canGoBack()) {
                binding.wb.goBack();
            } else {
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        binding.ctlRoot.removeView(binding.wb);
        super.onDestroy();
        if (billingManager != null) {
            billingManager.destroy();
        }
    }

    public void refresh() {
        showNetworkStatus(false);
        loadUrl();
    }

    /**
     * Js与Native交互处理器
     */
    public static class JsBridgerHandler {

        private final Activity activity;

        public JsBridgerHandler(Activity activity) {
            this.activity = activity;
        }

        /**
         * 购买按钮点击
         *
         * @param skuId 商品ID
         * @param billingType 计费类型
         */
        @JavascriptInterface
        public void onPurchaseClick(final String skuId, final String billingType) {
            billingManager = new BillingManager(activity, new SimpleBillingUpdateListener() {

                @Override
                public void onBillingClientSetupFinished() {
                    billingManager.launchBillingFlow(skuId, billingType);
                }

                @Override
                public void onPurchasesUpdated(List<Purchase> purchases) {
                    if (purchases != null && !purchases.isEmpty()) {
                        Purchase purchase = purchases.get(0);
                        PurchaseInfo purchaseInfo = new PurchaseInfo(purchase);
                        Intent intent = new Intent();
                        intent.setAction(Constant.ACTION_BILLING);
                        intent.putExtra("code", BillingClient.BillingResponseCode.OK);
                        intent.putExtra("message", "Purchase success!");
                        intent.putExtra("data", purchaseInfo);
                        LocalBroadcastManager.getInstance(activity).sendBroadcast(intent);
                        // 消耗商品
                        billingManager.consumeAsync(purchase.getPurchaseToken());
                    }
                }

                @Override
                public void onPurchasesCancel() {
                    Intent intent = new Intent();
                    intent.setAction(Constant.ACTION_BILLING);
                    intent.putExtra("code", BillingClient.BillingResponseCode.USER_CANCELED);
                    intent.putExtra("message", "Purchase cancel!");
                    LocalBroadcastManager.getInstance(activity).sendBroadcast(intent);
                }

                @Override
                public void onPurchasesFailure(int errorCode, String message) {
                    Intent intent = new Intent();
                    intent.setAction(Constant.ACTION_BILLING);
                    intent.putExtra("code", errorCode);
                    intent.putExtra(message, message);
                    LocalBroadcastManager.getInstance(activity).sendBroadcast(intent);
                }
            });
            billingManager.startServiceConnection(null);
            LogUtils.e("skuId:" + skuId + ", billingType:" + billingType);
        }
    }
}
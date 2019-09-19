# Google-Billing

:rocket: 谷歌应用内支付（Google Billing 3）二次封装

[谷歌应用内支付文档](https://developer.android.com/google/play/billing/billing_overview.html?hl=zh-CN)


![支付方式一](https://github.com/pengfeigao/GoogleBilling/tree/master/screenshots/billing01.png) 
![支付方式二](https://github.com/pengfeigao/GoogleBilling/tree/master/screenshots/images/billing02.png)
![支付方式一](https://github.com/pengfeigao/GoogleBilling/tree/master/screenshots/images/billing03.png) 
![支付方式二](https://github.com/pengfeigao/GoogleBilling/tree/master/screenshots/images/billing04.png)
![支付方式一](https://github.com/pengfeigao/GoogleBilling/tree/master/screenshots/images/billing05.png) 

### CHANGELOG

### [V1.0.2] - [2019-09-19]

- 增加缓存购买信息查询并确认购买方法

### [V1.0.1] - [2019-09-03]

- Change: 添加消耗型和非消耗型商品失效方法

### [V1.0.0] - [2019-09-02]

- :tada: 初版。

***

### 集成

- 在app build.gradle添加依赖

```
    implementation 'cn.imeina:google-billing:1.0.0'
```

### 使用

- SDK包含了2种支付方式。一种是直接调用谷歌原生应用内支付，一种是传递url进行H5支付

1. 谷歌应用内支付

```

prvate BillingManager billingManager;

// 1. 创建回调
BaseBillingUpdateListener billingUpdateListener = new SimpleBillingUpdateListener() {
    @Override
    public void onBillingClientSetupFinished() {
        // 在与Google Play建立好连接后，进行支付
        if (billingManager != null) {
            billingManager.quicknessPurchase("7days", BillingClient.SkuType.SUBS);
        }
    }

    @Override
    public void onPurchasesUpdated(List<Purchase> purchases) {
        // 此处为支付成功回调，可以拿到支付成功的商品调用我们自己的业务接口来对商品购买状态进行二次校验
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

// 2. 进行与Google Play连接
public void googleBilling(){
    billingManager = new BillingManager(this, billingUpdateListener);
    billingManager.startServiceConnection(null);
}

// 3. 释放资源
@Override
protected void onDestroy() {
    super.onDestroy();
    if (billingManager != null){
        billingManager.destroy();
    }
}
```

2. H5支付（包含国外第三方支付）

```
prvate BillingManager billingManager;

BillingPurchasesReceiver billingPurchasesReceiver = new BillingPurchasesReceiver() {

    @Override
    public void onPurchasesUpdated(PurchaseInfo purchaseInfo) {
        // 此处为支付成功回调，可以拿到支付成功的商品调用我们自己的业务接口来对商品购买状态进行二次校验
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

// 2. 发起H5支付
public void purchase() {
    BillingManager billingManager = new BillingManager(this, billingPurchasesReceiver);
    billingManager.quicknessPurchase("<三方H5支付网址>");
}

// 3. 释放资源
@Override
protected void onDestroy() {
    super.onDestroy();
    if (billingManager != null){
        billingManager.destroy();
    }
}
```

> 说明：
>     对于原生支付，无论是进行商品查询操作还是购买操作，必须是在与Google Play建立连接后进行操作。即在收到onBillingClientSetupFinished()回调后进行操作。
>     对于H5支付，URL可以是我们业务的链接地址(比如我们的VIP支付项页面)，也可以直接是国外第三方支付（参数完整的）链接地址。
>     谷歌支付需要提前在谷歌开发者平台配置测试账号以及其他配置，才能进行SDK调用。由于该库是从公司项目抽离。demo没有使用项目包名，运行无法得到预期效果。

- 混淆

TODO 待添加...
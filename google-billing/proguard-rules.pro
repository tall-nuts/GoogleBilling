# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

# 指定代码的压缩级别
-optimizationpasses 5
# 不使用大小写混合的类名
-dontusemixedcaseclassnames
# 不跳过library中非public的类
-dontskipnonpubliclibraryclasses
# 打印混淆的详细信息
-verbose
# 不进行优化（默认关闭） 建议使用此选项，因为根据proguard-android-optimize.txt中的描述，优化可能会造成一些潜在风险，不能保证在所有版本的Dalvik上都正常运行
-dontoptimize
# 不进行预校验  （这个预校验是作用在Java平台上的，Android平台上不需要这项功能，去掉之后还可以加快混淆速度）
-dontpreverify
# 保留注解中的参数
-keepattributes *Annotation*
# 不混淆下面两个类，接入Google Service使需要用到下面的类
-keep public class com.google.vending.licensing.ILicensingService
-keep public class com.android.vending.licensing.ILicensingService
# 不混淆任何包含native方法的类的类名和方法名
-keepclasseswithmembernames class * {
    native <methods>;
}
# 不混淆任何view中的setter和getter方法，因为属性动画中需要实现相应的setter和getter方法
-keepclassmembers public class * extends android.view.View {
   void set*(***);
   *** get*();
}
# 不混淆Activity中参数为View的方法，防止在XML中给View设置点击事件时无效（android:onClick=""）
-keepclassmembers class * extends android.app.Activity {
   public void *(android.view.View);
}
# 不混淆枚举中的values()和valueOf()方法
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
# 不混淆Paracelable和CREATOR字段，否则Paracelable机制将无法工作
-keepclassmembers class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator CREATOR;
}
# 不混淆Serializable
-keepnames class * implements java.io.Serializable{
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}
# 不混淆R文件的所有静态成员
-keepclassmembers class **.R$* {
    public static <fields>;
}
# 忽略support包下的警告，高版本代码兼容下的警告，可以直接忽略
-dontwarn android.support.**
# Understand the @Keep support annotation.（保持Keep注释）
-keep class androidx.annotation.Keep

-keep @androidx.annotation.Keep class * {*;}

-keepclasseswithmembers class * {
    @androidx.annotation.Keep <methods>;
}
-keep class com.example.android.InitUtils{ public *;}
-keepclasseswithmembers class * {
    @androidx.annotation.Keep <fields>;
}

-keepclasseswithmembers class * {
    @androidx.annotation.Keep <init>(...);
}
# 保护泛型
-keepattributes Signature, InnerClasses


######################### 通用的保护项目代码不被混淆的配置 #########################

-keep class com.google.billing.model.** { *; }
-keep class com.android.vending.billing.** { *; }
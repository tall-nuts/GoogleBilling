package com.google.billing.widget;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import androidx.appcompat.widget.AppCompatTextView;

/**
 * @author gaopengfei on 2019/08/15.
 */
public class ITextView extends AppCompatTextView {

    public ITextView(Context context) {
        this(context, null);
    }

    public ITextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ITextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        Typeface font = Typeface.createFromAsset(context.getAssets(), "iconfont.ttf");
        setTypeface(font);
        invalidate();
    }
}

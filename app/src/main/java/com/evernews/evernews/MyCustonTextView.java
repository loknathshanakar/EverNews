package com.evernews.evernews;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by Loknath Shankar on 3/13/2016.
 */
public class MyCustonTextView  extends TextView{
    public MyCustonTextView(Context context,AttributeSet attrs){
        super(context,attrs);
        this.setTypeface(Typeface.createFromAsset(context.getAssets(),"fonts/roboto_light.ttf"));
    }
}

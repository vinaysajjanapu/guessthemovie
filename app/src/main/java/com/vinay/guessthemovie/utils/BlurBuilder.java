package com.vinay.guessthemovie.utils;

/**
 * Created by vinaysajjanapu on 2/2/17.
 */

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;


public class BlurBuilder {
    private static final float BITMAP_SCALE = .5f;
    private static final float BLUR_RADIUS = 5f;

    private static volatile BlurBuilder Instance = null;

    public static BlurBuilder getInstance() {
        BlurBuilder localInstance = Instance;
        if (localInstance == null) {
            synchronized (BlurBuilder.class) {
                localInstance = Instance;
                if (localInstance == null) {
                    Instance = localInstance = new BlurBuilder();
                }
            }
        }
        return localInstance;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public Bitmap blur_bitmap(Context context, Bitmap image) {
        int width = Math.round(image.getWidth() * BITMAP_SCALE);
        int height = Math.round(image.getHeight() * BITMAP_SCALE);

        Bitmap inputBitmap = Bitmap.createScaledBitmap(image, width, height, false);
        Bitmap outputBitmap = Bitmap.createBitmap(inputBitmap);

        RenderScript rs = RenderScript.create(context);
        ScriptIntrinsicBlur theIntrinsic = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
        Allocation tmpIn = Allocation.createFromBitmap(rs, inputBitmap);
        Allocation tmpOut = Allocation.createFromBitmap(rs, outputBitmap);
        theIntrinsic.setRadius(BLUR_RADIUS);
        theIntrinsic.setInput(tmpIn);
        theIntrinsic.forEach(tmpOut);
        tmpOut.copyTo(outputBitmap);

        return outputBitmap;
    }

    public Drawable drawable_img(Bitmap default_bg, Context context) {
        Bitmap temp_input = default_bg.copy(Bitmap.Config.ARGB_4444, true);
        Bitmap input_to_blur = Bitmap.createScaledBitmap(temp_input, 300, 300, false);
        Bitmap blurredBitmap = blur_bitmap(context, input_to_blur);
        Drawable dr = new BitmapDrawable(blurredBitmap);
        default_bg = null;
        blurredBitmap = null;
        input_to_blur = null;
        temp_input = null;
        return dr;
    }

}

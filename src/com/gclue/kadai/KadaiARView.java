package com.gclue.kadai;

import android.content.Context;
import android.content.res.Resources;
import android.view.View;

public class KadaiARView extends View
{
	private static final String TAG = "ARView";
    public KadaiARView(Context context)
    {
        super(context);
        prepareImages();
    }
    private void prepareImages()
    {
        Resources r = getResources();
    }
}

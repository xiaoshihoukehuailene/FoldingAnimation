package com.zz.foldinganimation.foldlayout;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;

import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.drawerlayout.widget.DrawerLayout;


public class FoldDrawerLayout extends DrawerLayout {
    public FoldDrawerLayout(Context context) {
        super(context);
    }

    public FoldDrawerLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FoldDrawerLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        int childCount = getChildCount();
        for (int i=0; i<childCount; i++){
            View child = getChildAt(i);
            if (isDrawerView(child)){
               FoldLayout foldingLayout = new FoldLayout(getContext());
                foldingLayout.setAnchor(1);
                removeView(child);
                foldingLayout.addView(child);
                ViewGroup.LayoutParams layPar = child.getLayoutParams();
                addView(foldingLayout, i, layPar);
            }
        }

        addDrawerListener(new DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                if (drawerView instanceof FoldLayout) {
                    FoldLayout foldLayout = ((FoldLayout) drawerView);
                    foldLayout.setFactor(slideOffset);
                }
            }

            @Override
            public void onDrawerOpened(View drawerView) {

            }

            @Override
            public void onDrawerClosed(View drawerView) {

            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });
    }

    private boolean isDrawerView(View child){
        int gravity = ((LayoutParams)child.getLayoutParams()).gravity;
        int absGravity = GravityCompat.getAbsoluteGravity(gravity, ViewCompat.getLayoutDirection(child));
        return (absGravity & (Gravity.LEFT | Gravity.RIGHT)) != 0;
    }

}

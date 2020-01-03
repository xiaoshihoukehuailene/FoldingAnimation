package com.zz.folding;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.zz.foldinganimation.foldlayout.FoldLayout;

public class MainActivity extends AppCompatActivity {
    private FoldLayout folding_loyout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        folding_loyout = findViewById(R.id.folding_loyout);
        folding_loyout.setFactor((float) 0.8);


    }
}

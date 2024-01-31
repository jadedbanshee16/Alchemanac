package com.example.alchemanac;

import android.graphics.Color;
import android.os.Build;

import androidx.annotation.RequiresApi;

@RequiresApi(api = Build.VERSION_CODES.O)
public enum AlchemyRarity {
    common(Color.argb(150, 200,200,200)),
    uncommon(Color.argb(150, 150, 200, 100)),
    rare(Color.argb(150, 100,150,200)),
    legendary(Color.argb(150, 200,100,150)),
    otherworldy(Color.argb(150, 200,150,200));
    final int col;
    AlchemyRarity(int i) {
        this.col = i;
    }
}


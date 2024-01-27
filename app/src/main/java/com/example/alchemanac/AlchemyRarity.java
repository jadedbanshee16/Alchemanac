package com.example.alchemanac;

import android.graphics.Color;
import android.os.Build;

import androidx.annotation.RequiresApi;

@RequiresApi(api = Build.VERSION_CODES.O)
public enum AlchemyRarity {
    common(Color.rgb(200,200,200)),
    uncommon(Color.rgb(150, 200, 100)),
    rare(Color.rgb(100,150,200)),
    legendary(Color.rgb(200,100,150)),
    otherworldy(Color.rgb(200,150,200));
    final int col;
    AlchemyRarity(int i) {
        this.col = i;
    }
}


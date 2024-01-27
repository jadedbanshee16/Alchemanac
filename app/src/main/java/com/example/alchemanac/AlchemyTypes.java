package com.example.alchemanac;

import android.graphics.drawable.Drawable;

import java.io.InputStream;
import android.content.Context;
import android.os.Build;

import androidx.annotation.RequiresApi;

@RequiresApi(api = Build.VERSION_CODES.O)
public enum AlchemyTypes {
    flora(0),
    fauna(1),
    mineral(2),
    special(3);

    final int img;

    AlchemyTypes(int i) {
        if(i == 0){
            this.img = R.drawable.flora_picture;
        } else if(i == 1){
            this.img = R.drawable.fauna_picture;
        } else if(i == 2){
            this.img = R.drawable.mineral_picture;
        } else if (i == 3){
            this.img = R.drawable.special_picture;
        } else {
            this.img = R.drawable.special_picture;
        }
    }
}


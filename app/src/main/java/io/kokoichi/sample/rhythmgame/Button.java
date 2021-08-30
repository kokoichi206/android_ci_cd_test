package io.kokoichi.sample.rhythmgame;


import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import static io.kokoichi.sample.rhythmgame.GameView.screenRatioX;
import static io.kokoichi.sample.rhythmgame.GameView.screenRatioY;

public class Button {

    Bitmap button;
    int length =54;
    int startX = 2000;
    int startY = 100;

    Button(Resources res) {

        button = BitmapFactory.decodeResource(res, R.drawable.pause_button);

        length = (int) (length * screenRatioX);

        startX = (int) (startX * screenRatioX);
        startY = (int) (startY * screenRatioY);

        button = Bitmap.createScaledBitmap(button, length, length, false);

    }
}

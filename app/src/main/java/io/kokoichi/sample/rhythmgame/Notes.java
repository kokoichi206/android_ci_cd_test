package io.kokoichi.sample.rhythmgame;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import static io.kokoichi.sample.rhythmgame.GameView.screenRatioX;

public class Notes {

    public static final int OFFSET_DEAD = 30;
    Bitmap notes;
    int x, y, length = 210, yLimit;
    public static int lifeTimeMilliSec = 300;
    int age = 0;
    int OFFSET = 180;   // Overshoot milli sec
    boolean isAlive;

    Notes(Resources res) {

        notes = BitmapFactory.decodeResource(res, R.drawable.notes);

        length = (int) (length * screenRatioX);

        notes = Bitmap.createScaledBitmap(notes, length, length, false);

        yLimit -= length;

        isAlive = true;
    }

}

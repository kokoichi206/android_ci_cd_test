package io.kokoichi.sample.rhythmgame;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.os.Looper;
import android.util.Log;

import static io.kokoichi.sample.rhythmgame.GameView.screenRatioX;
import static io.kokoichi.sample.rhythmgame.GameView.screenRatioY;

public class HpBar {

    int x = 100;
    int y = 80;
    int edge = 3;
    int width = 400;
    int current_width;
    int height = 80;
    Bitmap hp_bar, max_hp_bar;
    int max_hp;
    int current_hp;

    HpBar(Resources res) {

        hp_bar = BitmapFactory.decodeResource(res, R.drawable.hp_bar);
        max_hp_bar = BitmapFactory.decodeResource(res, R.drawable.max_hp_bar);

        x = (int) (x * screenRatioX);
        y = (int) (y * screenRatioY);

        width = (int) (width * screenRatioX);
        height = (int) (height * screenRatioY);

        max_hp_bar = Bitmap.createScaledBitmap(max_hp_bar, width, height, false);
        hp_bar = Bitmap.createScaledBitmap(hp_bar, width - 2 * edge, height - 2 * edge, false);
    }

    void update() {

        int new_width = width * current_hp / max_hp;
        hp_bar = Bitmap.createScaledBitmap(hp_bar, new_width - 2 * edge, height - 2 * edge, false);

        Log.d("hoge", String.valueOf(Looper.myLooper()));
    }
}

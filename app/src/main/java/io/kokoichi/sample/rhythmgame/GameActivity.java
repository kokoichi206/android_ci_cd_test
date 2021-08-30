package io.kokoichi.sample.rhythmgame;

import android.graphics.Point;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class GameActivity extends AppCompatActivity {

    public GameView gameView;
    public Point point;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        point = new Point();
        getWindowManager().getDefaultDisplay().getSize(point);

        gameView = new GameView(GameActivity.this, point.x, point.y);

        setContentView(gameView);
    }

    @Override
    protected void onPause() {

        super.onPause();

        // `finish()` method is for the following case.
        //      pause button -> home button
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        gameView.resume();
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();

        // Do something before killing this GameActivity
        if (MyMediaPlayer.player != null) {
            MyMediaPlayer.player.release();
        }
    }
}

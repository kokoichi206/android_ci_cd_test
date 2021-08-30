package io.kokoichi.sample.rhythmgame;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.Random;

public class GameView extends SurfaceView implements Runnable {

    String TAG = GameView.class.getSimpleName();

    // CONSTANTS related game playing
    private static final int SLEEP_TIME = Math.round(1000 / 60);
    private static final int ONE_LOOP_TIME = 20; // MilliSecond
    private int NOTES_NUM = 5;  // type 1-NOTES_NUM
    private final int CANVAS_TEXT_SIZE = 128;
    private final int CANVAS_TEXT_SIZE_SMALL = 84;
    private static final int NEW_NOTES_START_Y = 128;

    protected enum NotesTimings {
        PERFECT,
        GREAT,
        GOOD;
    }

    private static final int DISTANCE_PERFECT = 1500;
    private static final int DISTANCE_GREAT = 2000;
    private static final int DISTANCE_GOOD = 3000;
    private static final int TIMING_INFO_AGE = 15;
    private static final float DEFAULT_SCREEN_SIZE_X = 1920f;
    private static final float DEFAULT_SCREEN_SIZE_Y = 1080f;

    public static float screenRatioX, screenRatioY;

    protected Thread thread;
    private boolean isPlaying;
    protected Paint paint;
    protected Paint sPaint;
    private int screenX, screenY;
    protected GameActivity activity;
    private Random random;

    protected Background background;
    protected Circle[] circles;
    protected ArrayList<Notes> notesList;
    protected Position[] positions;
    protected Button button;
    protected HpBar hpBar;

    public MyMediaPlayer myPlayer;
    private double[] dropTiming;
    private double musicStartingTime, musicEndingTime;
    private int num_bar;

    private int combo = 0;
    private int maxCombo = 0;
    protected Info info;      // information like "GOOD","PERFECT"

    private long loopStartedAt;
    private int notesIndex;
    private double nextNotesTiming;

    private final Handler handler;
    AlertDialog dialog;
    private AlertDialog.Builder builder;

    private class Info {
        int age;            // the unit is loop count
        String message;        // message like "GOOD","PERFECT"
    }

    // Position to put Circle[]
    private class Position {
        int x, y;
    }

    public GameView(GameActivity activity, int screenX, int screenY) {
        super(activity);

        this.activity = activity;

        this.screenX = screenX;
        this.screenY = screenY;

        // FIXME
        screenRatioX = DEFAULT_SCREEN_SIZE_X / screenX;
        screenRatioY = DEFAULT_SCREEN_SIZE_Y / screenY;

        // Calculate notes position settings
        positions = new Position[NOTES_NUM];
        for (int i = 0; i < NOTES_NUM; i++) {

            Position position = new Position();
            // FIXME: remove magic number
            position.x = screenX / 2 + (int) ((i - 2) * screenRatioX * 300);
            position.y = screenY - (int) (screenRatioY * (300 + 12 * Math.pow(Math.abs(i - 2), 3)));
            positions[i] = position;

        }

        // Background init
        background = new Background(screenX, screenY, getResources());

        // Circle init
        circles = new Circle[NOTES_NUM];

        for (int i = 0; i < NOTES_NUM; i++) {

            Circle circle = new Circle(getResources());
            // FIXME: remove magic number
            float center = NOTES_NUM / 2;
            circle.x = positions[i].x;
            circle.y = positions[i].y;
            circles[i] = circle;

        }

        // Notes init
        notesList = new ArrayList<>();

        paint = new Paint();
        paint.setTextSize(CANVAS_TEXT_SIZE);
        paint.setColor(Color.BLACK);

        // FIXME: There should be better ways
        sPaint = new Paint();       // for SMALL text
        sPaint.setTextSize(CANVAS_TEXT_SIZE_SMALL);
        sPaint.setColor(Color.GRAY);

        info = new Info();
        info.age = 0;
        info.message = "";

        random = new Random();

        // Sound init
        // CHOOSE ONE MUSIC
        // All time related variables are milliseconds
        myPlayer = new MyMediaPlayer(activity, R.raw.kimigayo, this);
        musicStartingTime = 5.108866213151927 * 1000;
        musicEndingTime = 45.24492063492064 * 1000;
        dropTiming = new double[]{0,
                1, 1, 1, 1, 1, 1, 2,
                1, 1, 1, 0.5, 0.5, 1, 1, 1, 1,
                1, 1, 2, 1, 1, 2,
                1, 1, 1, 1, 1.5, 0.5, 2,
                1, 1, 2, 1, 1, 1, 1,
                1, 0.5, 0.5, 2};
        num_bar = 11 * 4;

        // Time between appearance and the clickable zone
        int timeToCircle = Notes.lifeTimeMilliSec;
        dropTiming[0] += musicStartingTime - timeToCircle;

        // Convert one bar of music sheet to milliseconds
        double one_bar = (musicEndingTime - musicStartingTime) / num_bar;
        for (int i = 1; i < dropTiming.length; i++) {
            dropTiming[i] = dropTiming[i - 1] + dropTiming[i] * one_bar;
        }

        // HP Bar init
        hpBar = new HpBar(getResources());
        // FIXME: Who should have this info(max_hp)?
        hpBar.max_hp = 10;
        hpBar.current_hp = hpBar.max_hp;

        button = new Button(getResources());

        handler = new Handler();
    }

    @Override
    public void run() {

        myPlayer.player.start();
        myPlayer.player.setOnCompletionListener(myPlayer);

        // set the params for count the timing only when FIRST called
        if (loopStartedAt == 0) {
            loopStartedAt = System.currentTimeMillis();
            notesIndex = 0;
            nextNotesTiming = dropTiming[notesIndex];
        }

        Log.d(TAG, "loop started at " + loopStartedAt);

        while (isPlaying) {

            update();
            draw();
//            sleep();

            // drop the notes when the time comes
            if (System.currentTimeMillis() - loopStartedAt > nextNotesTiming) {

                // pass the type (NOT index)
                int notesType = random.nextInt(NOTES_NUM) + 1;
                if (notesType >= 1 && notesType <= NOTES_NUM) {
                    newNotes(notesType);
                } else {
                    newNotes(1);
                }

                notesIndex += 1;
                if (notesIndex < dropTiming.length) {
                    nextNotesTiming = dropTiming[notesIndex];
                } else {
                    nextNotesTiming *= 2;
                }

            }
        }
    }

    private void sleep() {
        try {
            Thread.sleep(SLEEP_TIME);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void update() {
        // move notes
        ArrayList<Notes> trashNotes = new ArrayList<>();
        for (Notes notes : notesList) {
            notes.age += ONE_LOOP_TIME;
            notes.y += notes.yLimit * ONE_LOOP_TIME / notes.lifeTimeMilliSec;

            if (notes.isAlive) {
                if (notes.age > notes.lifeTimeMilliSec + notes.OFFSET_DEAD) {
                    maxCombo = (combo > maxCombo) ? combo : maxCombo;
                    combo = 0;

                    hpBar.current_hp -= 1;
                    // Check the current HP > 0 <=> Is still alive ?
                    // MAYBE: Separate "alive check" and "update func"
                    if (hpBar.current_hp > 0) {
                        hpBar.update();
                    } else {
                        gameOverDialog();
                        isPlaying = false;
                    }

                    notes.isAlive = false;
                }
            }

            if (notes.age > (notes.lifeTimeMilliSec + notes.OFFSET)) {
                trashNotes.add(notes);
            }
        }
        if (trashNotes.size() > 0) {
            for (Notes notes : trashNotes) {
                notesList.remove(notes);
            }
        }

        // info: decrease age
        if (info.age > 0) {
            info.age -= 1;
        }

        return;
    }

    private void draw() {
        // draw background first
        Canvas canvas = getHolder().lockCanvas();
        if (canvas == null) {
            return;
        }
        canvas.drawBitmap(background.background, background.x, background.y, paint);

        // draw Birds
        for (Circle circle : circles) {
            canvas.drawBitmap(circle.circle, circle.x, circle.y, paint);
        }

        // draw Notes
        for (Notes notes : notesList) {
            canvas.drawBitmap(notes.notes, notes.x, notes.y, paint);
        }

        // draw combo if combo > 0
        if (combo > 0) {
            drawTextCenter(canvas, combo + "", screenX / 2f, 164, paint);
        }

        // draw info if message exists
        if (info.age > 0) {
            drawTextCenter(canvas, info.message + "", screenX / 2f, 328, sPaint);
        }

        // draw Button
        canvas.drawBitmap(button.button, button.startX, button.startY, paint);

        // draw HP Bar
        canvas.drawBitmap(hpBar.max_hp_bar, hpBar.x, hpBar.y, paint);
        canvas.drawBitmap(hpBar.hp_bar, hpBar.x + hpBar.edge, hpBar.y + hpBar.edge, paint);

        getHolder().unlockCanvasAndPost(canvas);
        return;
    }

    public void resume() {
        isPlaying = true;
        thread = new Thread(this);
        thread.start();
    }

    public void pause() {
        try {
            isPlaying = false;
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param type position type (1-NOTES_NUM)
     */
    public void newNotes(int type) {

        int index = type - 1;

        Notes notes = new Notes(getResources());
        notes.x = positions[index].x;
        notes.y = NEW_NOTES_START_Y;
        notes.yLimit = positions[index].y + notes.OFFSET;   // a little bit overshoot
        notesList.add(notes);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        float touchedX = event.getX();
        float touchedY = event.getY();

        // Check the Stop Button was Tapped
        if (isStopButtonTapped(touchedX, touchedY)) {
            Log.d(TAG, "The stop-button is clicked");

            if (event.getAction() == MotionEvent.ACTION_UP) {
                returnHomeCheck();
            }
            return true;
        }

        // return if touched point is out of circle area
        int index = getCircleIndex(touchedX, touchedY);
        if (index == -1) {
            return true;
        }
        // adjust touched point to the center of the circle
        touchedX = circles[index].x + circles[index].length / 2;
        touchedY = circles[index].y + circles[index].length / 2;

        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:

                Notes touchedNotes = null;

                for (Notes notes : notesList) {

                    double dist = Math.pow(notes.x + notes.length / 2 - touchedX, 2)
                            + Math.pow(notes.y + notes.length / 2 - touchedY, 2);
                    if (dist < DISTANCE_PERFECT) {
                        touchedNotes = notes;
                        updateInfo(String.valueOf(NotesTimings.PERFECT), TIMING_INFO_AGE);
                    } else if (dist < DISTANCE_GREAT) {
                        touchedNotes = notes;
                        updateInfo(String.valueOf(NotesTimings.GREAT), TIMING_INFO_AGE);
                    } else if (dist < DISTANCE_GOOD) {
                        touchedNotes = notes;
                        updateInfo(String.valueOf(NotesTimings.GOOD), TIMING_INFO_AGE);
                    }
                }

                if (touchedNotes != null) {
                    notesList.remove(touchedNotes);
                    combo += 1;
                }

                break;
        }

        return true;
    }

    private void drawTextCenter(Canvas canvas, String text, float x, float y, Paint paint) {
        float width = paint.measureText(text);
        float startX = x - width / 2;
        float startY = y - (paint.getFontMetrics().descent + paint.getFontMetrics().ascent) / 2;
        canvas.drawText(text, startX, startY, paint);
    }

    private void updateInfo(String msg, int age) {
        info.age = age;
        info.message = msg;
    }

    //
    // Index range 0 ~ NOTES_NUM-1
    //
    protected int getCircleIndex(float touchedX, float touchedY) {

        int index = -1;
        for (int i = 0; i < NOTES_NUM; i++) {
            if (((touchedX > circles[i].x) && (touchedX < circles[i].x + circles[i].length)) &&
                    ((touchedY > circles[i].y) && (touchedY < circles[i].y + circles[i].length))) {
                index = i;
            }
        }

        return index;
    }

    /**
     * Check if the stop button is tapped
     * MAYBE: Generalize this function !
     *
     * @return
     */
    protected boolean isStopButtonTapped(float touchedX, float touchedY) {
        // How roughly the button can be clicked
        double RATIO = 2.5;
        return (((touchedX > button.startX - button.length * (RATIO - 1)) && (touchedX < button.startX + button.length * RATIO)) &&
                ((touchedY > button.startY - button.length * (RATIO - 1)) && (touchedY < button.startY + button.length * RATIO)));
    }

    public int getMaxCombo() {
        return maxCombo;
    }

    public void returnHomeCheck() {

        long dialogStartedAt = System.currentTimeMillis();

        // Pause music
        int musicLength = 0;
        if (MyMediaPlayer.player != null) {
            if (MyMediaPlayer.player.isPlaying()) {
                MyMediaPlayer.player.pause();
                musicLength = MyMediaPlayer.player.getCurrentPosition();
            }
        }
        pause();

        //
        // Make a dialog to check whether you really wanna go back home
        //
        builder = new AlertDialog.Builder(activity);

        // 2. Set the dialog characteristics
        builder.setMessage(R.string.pause_dialog_message)
                .setTitle(R.string.pause_dialog_title);
        // OK button = CONTINUE // CAUTION
        final int finalMusicLength = musicLength;
        builder.setPositiveButton(R.string.pause_dialog_continue, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked OK button
                MyMediaPlayer.player.start();
                MyMediaPlayer.player.seekTo(finalMusicLength);

                long dialogEndedAt = System.currentTimeMillis();

                // Shift the started time to adjust the dropping start timing.
                loopStartedAt += dialogEndedAt - dialogStartedAt;
                resume();
            }
        });
        // NG button = QUIT  // CAUTION
        builder.setNegativeButton(R.string.pause_dialog_quit, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog
                returnHome();
            }
        });

        // 3. Make a dialog
        dialog = builder.create();

        dialog.show();
    }

    public void returnHome() {
        Intent intent = new Intent(activity, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra(MainActivity.INTENT_KEY_MAX_COMBO, getMaxCombo());
        activity.finish();
    }

    protected void gameOverDialog() {

        // Release music
        if (MyMediaPlayer.player != null) {
            if (MyMediaPlayer.player.isPlaying()) {
                MyMediaPlayer.player.pause();
            }
        }

        //
        // Make a dialog to check whether you really wanna go back home
        //
        // 1. Instantiate a builder
        builder = new AlertDialog.Builder(activity);

        // 2. Set the dialog characteristics
        builder.setMessage(R.string.dead_dialog_message)
                .setTitle(R.string.dead_dialog_title);
        // OK button
        builder.setPositiveButton(R.string.dead_dialog_ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                dialog.dismiss();
                activity.finish();

            }
        });

        showDialog(builder);
    }

    void showDialog(AlertDialog.Builder builder) {
        handler.post(() -> {

            dialog = builder.create();
            dialog.show();

        });
    }

}

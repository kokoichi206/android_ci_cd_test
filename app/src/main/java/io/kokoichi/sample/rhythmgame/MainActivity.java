package io.kokoichi.sample.rhythmgame;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    String TAG = GameView.class.getSimpleName();

    private static final int REQUEST_CODE_1 = 1;
    private DatabaseHelper dbHelper = new DatabaseHelper(this);

    public static final String INTENT_KEY_MAX_COMBO = "maxCombo";

    private int max_combo;

    public Me me;
    int max_bar_width, max_exp;

    // SharedPreference settings
    SharedPreferences data;
    String prefName = "UserData";
    public enum PrefKeys {
        Rank(1),
        Experience(0),
        ;

        private final int defaultValue;

        private PrefKeys(int val) {
            this.defaultValue = val;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.play).setOnClickListener((view) -> {
            startActivityForResult(new Intent(MainActivity.this, GameActivity.class), REQUEST_CODE_1);
        });

        // Init the Me class (Manage user data)
        me = new Me();
        max_bar_width = findViewById(R.id.exp_bar).getLayoutParams().width;
        max_exp = 4;

        deleteRecord(getString(R.string.music_1));
        insertCombo(getString(R.string.music_1), 0);

        // Get the max combo
        max_combo = getHighCombo();

        // Display the max combo
        String message = String.format(getString(R.string.display_high_score), max_combo);
        TextView combo_text = findViewById(R.id.display_high_score);
        combo_text.setText(message);

        // Init SharedPref and get value
        data = getSharedPreferences(prefName, MODE_PRIVATE);
        int rank = data.getInt(PrefKeys.Rank.toString(), PrefKeys.Rank.defaultValue);
        int exp = data.getInt(PrefKeys.Experience.toString(), PrefKeys.Experience.defaultValue);

        me.exp = exp;
        me.rank = rank;

        // Update the display
        changeExpBarSize();
        changeRank();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            // Return from gameActivity
            case (REQUEST_CODE_1):
                Log.d(TAG, "Return Home from Game Play Activity");

                // Case: Clear the game
                if (resultCode == RESULT_OK) {

                    // Add Experience, 
                    me.exp += 1;
                    if (me.exp >= max_exp) {
                        me.exp -= max_exp;
                        me.rank += 1;
                        changeRank();
                    }
                    changeExpBarSize();

                    int max_combo_last_game = data.getIntExtra(INTENT_KEY_MAX_COMBO, 0);
                    if (max_combo_last_game > max_combo) {
                        Log.d(TAG, "The max_combo is updated");
                        max_combo = max_combo_last_game;
                        updateRecord(getString(R.string.music_1), max_combo);
                        displayMaxCombo();
                    }
                } else if (resultCode == RESULT_CANCELED) {
                } else {
                }
                break;
            default:
                break;
        }
        // Save PrefKeys to SharedPreferences
        savePref();
    }

    public void savePref() {
        SharedPreferences dataPref = getSharedPreferences(prefName, MODE_PRIVATE);
        SharedPreferences.Editor editor = dataPref.edit();

        editor.putInt(PrefKeys.Rank.toString(), me.rank);
        editor.putInt(PrefKeys.Experience.toString(), me.exp);
        editor.commit();
        //        editor.apply();
    }

    protected void changeRank() {

        TextView mTextView = findViewById(R.id.display_rank_num);
        mTextView.setText(Integer.toString(me.rank));
    }

    private void displayMaxCombo() {
        // Display the max combo
        String message = String.format(getString(R.string.display_high_score), max_combo);
        TextView combo_text = findViewById(R.id.display_high_score);
        combo_text.setText(message);
    }

    @Override
    protected void onDestroy() {

        dbHelper.close();
        super.onDestroy();
    }

    /**
     * Get music high score (combo) from local db
     * return -1 if there is no record
     *
     * @return
     */
    protected int getHighCombo() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        int combo = 0;
        Cursor cursor = null;
        try {
            cursor = db.rawQuery("select * from PlayRecords where name = ?", new String[]{getString(R.string.music_1)});
            int id[] = new int[cursor.getCount()];
            int i = 0;
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();

                int idx = cursor.getColumnIndex(dbHelper.COLUMN_COMBO);
                combo = Integer.parseInt(cursor.getString(idx));
            } else {
                combo = -1;
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return combo;
    }

    /**
     * @param music
     * @param combo
     * @return the number of lines (if not found, return -1)
     */
    protected long insertCombo(String music, int combo) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(dbHelper.COLUMN_MUSIC_NAME, music);
        values.put(dbHelper.COLUMN_COMBO, combo);

        return db.insert(dbHelper.TABLE_NAME, null, values);
    }

    protected boolean deleteRecord(String music) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // True if a record existed and was successfully deleted.
        return db.delete(dbHelper.TABLE_NAME, dbHelper.COLUMN_MUSIC_NAME + " = '" + music + "'", null) == 1;
    }

    protected long updateRecord(String music, int combo) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(dbHelper.COLUMN_MUSIC_NAME, music);
        values.put(dbHelper.COLUMN_COMBO, combo);

        String whereClause = dbHelper.COLUMN_MUSIC_NAME + " = '" + music + "'";

        return db.update(dbHelper.TABLE_NAME, values, whereClause, null);
    }

    /**
     * Resize the exp bar with the current_exp
     */
    protected void changeExpBarSize() {
        // Gets ImageView
        ImageView layout = findViewById(R.id.exp_bar);
        // Gets the layout params that will allow you to resize the layout
        ViewGroup.LayoutParams params = layout.getLayoutParams();
        // Changes the height and width to the specified *pixels*
        params.width = max_bar_width * me.exp / max_exp;

        layout.setLayoutParams(params);
    }
}
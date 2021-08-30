package io.kokoichi.sample.rhythmgame;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.test.rule.ActivityTestRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class MainActivityTest {

    // To test the activity

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<MainActivity>(MainActivity.class);

    private MainActivity mActivity = null;

    private int[] ids = {
            R.id.background_home,
            R.id.play,
            R.id.display_high_score,
            R.id.max_exp_bar,
            R.id.exp_bar,
            R.id.exp_txt,
            R.id.display_rank_num,
            R.id.display_rank_txt,
    };

    // To preserve current data
    int current_max_combo;

    @Before
    public void setUp() throws Exception {

        mActivity = mActivityTestRule.getActivity();

        current_max_combo = mActivity.getHighCombo();
    }

    @Test
    public void isMeStateValid() {

        assertTrue(mActivity.me.rank >= 1);
        assertTrue(mActivity.me.exp >= 0);

    }

    @Test
    public void testLaunch() {

        for (int id: ids) {
            View view = mActivity.findViewById(id);
            assertNotNull(view);
        }
    }

    @Test
    public void testInit() {

        assertNotNull(mActivity.me);

    }

//    @UiThreadTest
    @Test
    public void changeRank() {

        mActivity.runOnUiThread(
                new Runnable() {
                @Override
                public void run() {

                    // Change rank to 255
                    mActivity.me.rank = 255;
                    mActivity.changeRank();

                    // Compare with the actual displayed number
                    TextView view = mActivity.findViewById(R.id.display_rank_num);
                    int displayedRank = Integer.parseInt((String) view.getText());
                    assertEquals(255, displayedRank);
                }
            }
        );
    }

    @Test
    public void getHighCombo() {

        int testCombo = mActivity.getHighCombo();
        assertTrue(testCombo >= 0);
    }

    @Test
    public void updateRecord() {

        int[] combos = {1, 11, 121, 12321, 11111111};

        for (int combo: combos) {
            mActivity.updateRecord(mActivity.getString(R.string.music_1), combo);
            assertTrue(combo == mActivity.getHighCombo());
        }
    }

    @Test
    public void deleteRecord() {

        assertTrue(mActivity.deleteRecord(mActivity.getString(R.string.music_1)));

        int noCombo = mActivity.getHighCombo();
        // -1 is the expected value
        // Maybe this is not good implement
        assertEquals(-1, noCombo);
    }

    @Test
    public void changeExpBarSize() {

        int[] test_exp = {0, mActivity.max_exp};

        ImageView layout = mActivity.findViewById(R.id.max_exp_bar);
        ViewGroup.LayoutParams params = layout.getLayoutParams();
        int max_width = params.width;

        int edge = 6;
        int actual_max_width = max_width - edge;

        mActivity.runOnUiThread(
                new Runnable() {
                    @Override
                    public void run() {

                        for (int exp: test_exp) {

                            mActivity.me.exp = exp;
                            mActivity.changeExpBarSize();

                            ImageView layout = mActivity.findViewById(R.id.exp_bar);
                            ViewGroup.LayoutParams params = layout.getLayoutParams();
                            int width = params.width;

                            assertTrue(width < actual_max_width);
//                            assertTrue(Math.abs((double) (width / actual_max_width) - (double) (mActivity.me.exp / mActivity.max_exp)) < 0.1);

                        }
                    }
                }
        );
    }

    @After
    public void tearDown() throws Exception {

        mActivity.updateRecord(mActivity.getString(R.string.music_1), current_max_combo);

        mActivity = null;

    }
}
package io.kokoichi.sample.rhythmgame;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.isDialog;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import androidx.test.espresso.ViewAction;
import androidx.test.espresso.action.GeneralClickAction;
import androidx.test.espresso.action.Press;
import androidx.test.espresso.action.Tap;
import androidx.test.espresso.intent.Intents;
import androidx.test.rule.ActivityTestRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class GameActivityUITest {

    @Rule
    public ActivityTestRule<GameActivity> mActivityGameTestRule = new ActivityTestRule<>(GameActivity.class);

    private GameActivity mActivityGame = null;

    @Before
    public void setUp() {

        Intents.init();
        mActivityGame = mActivityGameTestRule.getActivity();

    }

    @Test
    public void testPauseButton() {

        // Click the pause button
        Button button = mActivityGame.gameView.button;
        onView(isRoot()).perform(clickXY(button.startX, button.startY));

        // Check if the pause dialog is showing
        onView(withText(R.string.pause_dialog_message))
                .inRoot(isDialog())
                .check(matches(isDisplayed()));
    }

    @Test
    public void testContinueButtonInPauseDialog() {

        // Click the pause button
        Button button = mActivityGame.gameView.button;
        onView(isRoot()).perform(clickXY(button.startX, button.startY));

        // Click the continue button
        onView(withText(R.string.pause_dialog_continue))
                .inRoot(isDialog())
                .perform(click());

        // Check if the activity is not destroying
        assertFalse(mActivityGameTestRule.getActivity().isFinishing());
    }

    @Test
    public void testQuitButtonInPauseDialog() {

        // Click the pause button
        Button button = mActivityGame.gameView.button;
        onView(isRoot()).perform(clickXY(button.startX, button.startY));

        // Click the quit button
        onView(withText(R.string.pause_dialog_quit))
                .inRoot(isDialog())
                .perform(click());

        // Check if the activity is destroying
        assertTrue(mActivityGame.isFinishing());

    }

    @Test
    public void testDeadDialog() {

        // Reduce hp
        mActivityGame.gameView.hpBar.current_hp = 1;

        // the dead dialog is displayed
        onView(withText(R.string.dead_dialog_message))
                .inRoot(isDialog())
                .check(matches(isDisplayed()));

        // Click the ok button in the dialog
        onView(withText(R.string.dead_dialog_ok))
                .perform(click());

        // Check if the GameActivity finishes
        assertTrue(mActivityGame.isFinishing());

    }

    @After
    public void tearDown() throws Exception {

        mActivityGame = null;
        Intents.release();

    }

    //
    // Custom click function using (x, y) to click
    //
    public static ViewAction clickXY(final int x, final int y) {
        return new GeneralClickAction(
                Tap.SINGLE,
                view -> {

                    final int[] screenPos = new int[2];
                    view.getLocationOnScreen(screenPos);

                    final float screenX = screenPos[0] + x;
                    final float screenY = screenPos[1] + y;
                    float[] coordinates = {screenX, screenY};

                    return coordinates;
                },
                Press.FINGER);
    }
}

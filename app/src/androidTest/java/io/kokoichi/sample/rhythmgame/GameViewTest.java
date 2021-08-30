package io.kokoichi.sample.rhythmgame;

import androidx.test.annotation.UiThreadTest;
import androidx.test.rule.ActivityTestRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static android.content.DialogInterface.BUTTON_POSITIVE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class GameViewTest {

    @Rule
    public ActivityTestRule<GameActivity> mActivityGameTestRule = new ActivityTestRule<GameActivity>(GameActivity.class);

    private GameActivity mActivityGame = null;

    private GameView gameView;

    class RelativePosition {
        int rel_x, rel_y;

        RelativePosition(int x, int y) {
            rel_x = x;
            rel_y = y;
        }
    }

    @Before
    public void setUp() throws Exception {

        mActivityGame = mActivityGameTestRule.getActivity();
        gameView = mActivityGame.gameView;

    }

    @Test
    public void testInstanceNotNull() {

        // There Objects have to be initialized in onCreate
        Object[] objectsInGameView = {
                gameView.activity,
                gameView.positions,
                gameView.notesList,
                gameView.button,
                gameView.hpBar,
                gameView.myPlayer,
                gameView.paint,
                gameView.sPaint,
                gameView.thread,
                gameView.background,
                gameView.circles,
                gameView.info,
        };

        for (Object object : objectsInGameView) {
            assertNotNull(object);
        }
    }

    @Test
    public void newNotes() {

        int num_before = gameView.notesList.size();
        gameView.newNotes(2);
        int num_after = gameView.notesList.size();
        assertEquals(1, num_after - num_before);

    }

    @Test
    public void getCircleIndex() {


        for (int i = 0; i < gameView.circles.length; i++) {

            Circle circle = gameView.circles[i];
            assertNotNull(circle);
            int startX = circle.x;
            int startY = circle.y;
            int width = circle.length;
            int height = circle.length;

            int edge = 1;

            RelativePosition[] verifyRelativePositions = {
                    new RelativePosition(edge, edge),
                    new RelativePosition(width / 2, height / 2),
                    new RelativePosition(width - edge, height - edge),
            };

            for (RelativePosition relativePosition : verifyRelativePositions) {

                float x = startX + relativePosition.rel_x;
                float y = startY + relativePosition.rel_y;
                int circleIndex = gameView.getCircleIndex(x, y);
                assertEquals("x position: " + relativePosition.rel_x, i, circleIndex);
            }
        }

        // Out of range test: expected -1 as a return value
        int startX = 0;
        int startY = 0;
        RelativePosition[] verifyRelativePositions = {
                new RelativePosition(20, 20),
                new RelativePosition(400, 20),
                new RelativePosition(-4, 5),
                new RelativePosition(4, -50),
                new RelativePosition(15000, 200000),    // large Num
        };
        for (RelativePosition relativePosition : verifyRelativePositions) {

            float x = startX + relativePosition.rel_x;
            float y = startY + relativePosition.rel_y;
            int circleIndex = gameView.getCircleIndex(x, y);
            assertEquals("x position: " + relativePosition.rel_x, -1, circleIndex);
        }
    }

    @Test
    public void isStopButtonTapped() {

        int startX = gameView.button.startX;
        int startY = gameView.button.startY;
        int width = gameView.button.length;
        int height = gameView.button.length;

        // Assertions expected to be true
        RelativePosition[] expectedTruePositions = {
                new RelativePosition(0, 0),
                new RelativePosition(width / 2, height / 2),
                new RelativePosition(width, height),
                new RelativePosition(width, height),
        };
        for (RelativePosition relativePosition : expectedTruePositions) {

            float x = startX + relativePosition.rel_x;
            float y = startY + relativePosition.rel_y;
            int circleIndex = gameView.getCircleIndex(x, y);
            assertTrue(gameView.isStopButtonTapped(x, y));
        }

        // Assertions expected to be false
        int topX = 0;
        int leftY = 0;
        RelativePosition[] expectedFalsePositions = {
                new RelativePosition(-100, startY),
                new RelativePosition(startX, -100),
                new RelativePosition(20, 20),
                new RelativePosition(15000, 200000),    // large Num
        };
        for (RelativePosition relativePosition : expectedFalsePositions) {

            float x = topX + relativePosition.rel_x;
            float y = leftY + relativePosition.rel_y;
            int circleIndex = gameView.getCircleIndex(x, y);
            assertFalse(gameView.isStopButtonTapped(x, y));
        }
    }

    @Test
    @UiThreadTest
    public void returnHomeCheck() {

        gameView.returnHomeCheck();
        assertNotNull(gameView.dialog);
        assertTrue(gameView.dialog.isShowing());

    }

    /**
     * CAUTION:
     * This gameOver dialog does not (cannot) run on UI Thread,
     * so there should not be a UiThreadTest annotation
     */
    @Test
    public void gameOverDialog() {

        gameView.gameOverDialog();

        // Check the existence of a dialog
        assertNotNull(gameView.dialog);
        assertTrue(gameView.dialog.isShowing());


        // TODO:
        // How to check the title and message ?

        gameView.dialog.dismiss();
    }

    @After
    public void tearDown() throws Exception {

        mActivityGame = null;
        gameView = null;
    }
}
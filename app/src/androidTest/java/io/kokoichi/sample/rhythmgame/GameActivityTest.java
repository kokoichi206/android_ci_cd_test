package io.kokoichi.sample.rhythmgame;

import androidx.test.rule.ActivityTestRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;

public class GameActivityTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityMainTestRule = new ActivityTestRule<MainActivity>(MainActivity.class);

    private MainActivity mActivityMain = null;

    @Rule
    public ActivityTestRule<GameActivity> mActivityGameTestRule = new ActivityTestRule<GameActivity>(GameActivity.class);

    private GameActivity mActivityGame = null;

    @Before
    public void setUp() throws Exception {

        mActivityMain = mActivityMainTestRule.getActivity();
        mActivityGame = mActivityGameTestRule.getActivity();

    }

    @Test
    public void testInit() {

        assertNotNull(mActivityGame.gameView);

    }

    @After
    public void tearDown() throws Exception {

        mActivityMain = null;
        mActivityGame = null;
        
    }
}
package io.kokoichi.sample.rhythmgame;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.Intents.intending;
import static androidx.test.espresso.intent.matcher.IntentMatchers.toPackage;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.Assert.assertEquals;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class MainActivityUITest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<MainActivity>(MainActivity.class);

    @Before
    public void setUp() {
        Intents.init();
    }

    @After
    public void after() {
        Intents.release();
    }

    @Test
    public void useAppContext() {

        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        assertEquals("io.kokoichi.sample.rhythmgame", appContext.getPackageName());
    }

    //
    // CHECK:
    //  The physical device is NOT locked.
    //
    @Test
    public void isPlayButtonClickable() {

        onView(withId(R.id.play)).perform(click());

    }

    //
    // CHECK:
    //  The physical device is NOT locked.
    //
    @Test
    public void onActivityResultTest() {
        //
        // According to the ActivityResult, the max_combo will be updated if needed.
        // Check this using espresso.intent.Intents.intending
        //

        // Is this always zero ?
        onView(withId(R.id.display_high_score)).check(matches(withText("max combo: 0")));

        // The activity is mocked
        Intent resultData = new Intent();
        resultData.putExtra(MainActivity.INTENT_KEY_MAX_COMBO, 123);
        android.app.Instrumentation.ActivityResult result =
                new android.app.Instrumentation.ActivityResult(Activity.RESULT_OK, resultData);
        intending(toPackage("io.kokoichi.sample.rhythmgame")).respondWith(result);

        // Click the start button
        onView(withId(R.id.play)).perform(click());

        // Check whether other activity is called.
        intended(toPackage("io.kokoichi.sample.rhythmgame"));

        // Check whether the max_combo is updated according to the ACTIVITY RESULT or not.
        onView(withId(R.id.display_high_score)).check(matches(withText("max combo: 123")));
    }
}
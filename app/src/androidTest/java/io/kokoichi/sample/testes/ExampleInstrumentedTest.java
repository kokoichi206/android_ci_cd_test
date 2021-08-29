package io.kokoichi.sample.testes;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.content.Context;

import androidx.constraintlayout.utils.widget.MockView;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<MainActivity>(MainActivity.class);

    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        assertEquals("io.kokoichi.sample.testes", appContext.getPackageName());
//
//        List mockList = Mockito.mock();
//
//        mockList.add("apple");
//        assertEquals("apple", mockList.get(0));
    }
    
    @Test
    public void isButtonClickable() {
        onView(withId(R.id.button)).perform(click());
    }

    @Test
    public void helloWorld() {
        onView(withId(R.id.harowa)).check(matches(withText("Hello World!")));
    }

    @Test
    public void inputType() {
        onView(withId(R.id.editTextTextPersonName)).perform(typeText("Gebo gebo"));
    }
}
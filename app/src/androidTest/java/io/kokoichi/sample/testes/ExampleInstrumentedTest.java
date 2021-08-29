package io.kokoichi.sample.testes;

import android.content.Context;

import androidx.constraintlayout.utils.widget.MockView;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
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
}
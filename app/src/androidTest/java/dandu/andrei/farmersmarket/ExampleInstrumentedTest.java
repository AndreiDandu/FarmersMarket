package dandu.andrei.farmersmarket;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

/**
 * Instrumented accout_user_info_layout, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() {
        // Context of the app under accout_user_info_layout.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("dandu.andrei.farmersmarket", appContext.getPackageName());
    }
}

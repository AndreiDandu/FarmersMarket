package dandu.andrei.farmersmarket;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import dandu.andrei.farmersmarket.Main.MainActivity;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
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
    @Rule
    public ActivityTestRule<MainActivity> mya = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void clickSkipToMain(){
        onView(withId(R.id.skip_btn_id)).perform(click());
    }
}

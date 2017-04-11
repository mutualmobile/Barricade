package com.mutualmobile.barricade.sample;

import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

/**
 * Espresso tests for the UI
 */
@RunWith(AndroidJUnit4.class) @LargeTest public class MainActivityTest {

  @Rule public ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule<>(MainActivity.class);

  @Test public void verifyEnablingBarricade() {
    onView(withId(R.id.barricade_switch)).perform(click());
    onView(withId(R.id.get_joke_button)).perform(click());
    onView(withId(R.id.joke_text)).check(matches(withText("Gordon Ramsay features Chuck Norris' Toaster Strudel recipe in all of his restaurants.")));
  }
}
package com.mutualmobile.barricade.sample;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.IdlingResource;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;
import com.jakewharton.espresso.OkHttp3IdlingResource;
import com.mutualmobile.barricade.Barricade;
import com.mutualmobile.barricade.BarricadeConfig;
import com.mutualmobile.barricade.sample.api.util.ApiUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.not;

/**
 * Contains Espresso tests for the UI. Using Barricade, we can set a response for an endpoint, exercise the API call and verify the UI behavior.
 */
@RunWith(AndroidJUnit4.class) @LargeTest public class MainActivityTest {

  @Rule public ActivityTestRule<MainActivity> activityTestRule = new ActivityTestRule<>(MainActivity.class);

  private static final String JOKE_FROM_BARRICADE = "When Chuck Norris was a kid, he made his mom eat her vegetables";

  @Before public void setup() {
    IdlingResource resource = OkHttp3IdlingResource.create("OkHttp", ApiUtils.getOkHttpClient());
    Espresso.registerIdlingResources(resource);
  }

  @After public void teardown() {
    Barricade.getInstance().reset();
  }

  @Test public void verifyEnablingBarricade() {
    onView(withId(R.id.barricade_switch)).perform(click());
    onView(withId(R.id.get_joke_button)).perform(click());
    onView(withId(R.id.joke_text)).check(matches(withText(JOKE_FROM_BARRICADE)));
  }

  @Test public void verifyDisablingBarricade() {
    onView(withId(R.id.get_joke_button)).perform(click());
    onView(withId(R.id.joke_text)).check(matches(not(withText(JOKE_FROM_BARRICADE))));
  }

  @Test public void verifyFailure() {
    Barricade.getInstance().setResponse(BarricadeConfig.Endpoints.RANDOM, BarricadeConfig.Responses.Random.FAILURE);
    onView(withId(R.id.barricade_switch)).perform(click());
    onView(withId(R.id.get_joke_button)).perform(click());
    onView(withId(R.id.joke_text)).check(matches(withText("Request failed : 401")));
  }
}
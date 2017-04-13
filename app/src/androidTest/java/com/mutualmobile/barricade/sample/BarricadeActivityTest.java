package com.mutualmobile.barricade.sample;

import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import com.mutualmobile.barricade.Barricade;
import com.mutualmobile.barricade.BarricadeConfig;
import com.mutualmobile.barricade.activity.BarricadeActivity;
import com.mutualmobile.barricade.response.BarricadeResponse;
import com.mutualmobile.barricade.response.BarricadeResponseSet;
import com.mutualmobile.barricade.utils.AndroidAssetFileManager;
import java.util.Map;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.hasDescendant;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.mutualmobile.barricade.sample.utils.RecyclerViewMatcher.withRecyclerView;

/**
 * Contains Espresso tests for the Barricade Activity UI. We verify the response and it's corresponding response sets in the UI. Functionality of
 * delay and reset is verified as well
 */

@RunWith(AndroidJUnit4.class) @LargeTest public class BarricadeActivityTest {

  @Rule public ActivityTestRule<BarricadeActivity> activityTestRule = new ActivityTestRule<>(BarricadeActivity.class);

  private static Barricade barricade;

  @BeforeClass public static void setup() {
    barricade = new Barricade.Builder(BarricadeConfig.getInstance(), new AndroidAssetFileManager(InstrumentationRegistry.getTargetContext())).
        install();
  }

  @Test public void verifyEndpoints() {
    int count = 0;
    for (String endpoint : barricade.getConfig().keySet()) {
      onView(withRecyclerView(com.mutualmobile.barricade.R.id.endpoint_rv).atPosition(count)).check(matches(hasDescendant(withText(endpoint))));
      count++;
    }
  }

  @Test public void verifyResponsesForEndpoints() {
    int endpointCount = 0;
    Map<String, BarricadeResponseSet> hashMap = barricade.getConfig();
    for (String endpoint : hashMap.keySet()) {
      int responseCount = 0;
      onView(withId(R.id.endpoint_rv)).perform(RecyclerViewActions.actionOnItemAtPosition(endpointCount, click()));
      for (BarricadeResponse response : hashMap.get(endpoint).responses) {
        onView(withRecyclerView(com.mutualmobile.barricade.R.id.endpoint_responses_rv).atPosition(responseCount)).check(
            matches(hasDescendant(withText(response.responseFileName))));
        responseCount++;
      }
      Espresso.pressBack();
    }
  }

  @Test public void verifyDelayTimeDialogShowsCorrectDelay() {
    onView(withId(R.id.menu_delay)).perform(click());
    onView(withId(R.id.delay_value_edittext)).check(matches(withText(Long.toString(barricade.getDelay()))));
    onView(withId(android.R.id.button1)).check(matches(withText(R.string.set)));
    onView(withId(android.R.id.button2)).check(matches(withText(R.string.cancel)));
  }

  @Test public void verifyResetDialogIsDisplayed() {
    onView(withId(R.id.menu_reset)).perform(click());
    onView(withText(R.string.reset_message)).check(matches(isDisplayed()));
    onView(withId(android.R.id.button1)).check(matches(withText(R.string.yes)));
    onView(withId(android.R.id.button2)).check(matches(withText(R.string.no)));
  }
}

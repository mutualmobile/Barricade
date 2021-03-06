package com.mutualmobile.barricade.sample.utils;

import android.content.res.Resources;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

/**
 * Used for Matching contents of a RecyclerView item by position
 */
public class RecyclerViewMatcher {
  private final int recyclerViewId;

  public RecyclerViewMatcher(int recyclerViewId) {
    this.recyclerViewId = recyclerViewId;
  }

  public Matcher<View> atPosition(final int position) {
    return atPositionOnView(position, -1);
  }

  private Matcher<View> atPositionOnView(final int position, final int targetViewId) {

    return new TypeSafeMatcher<View>() {
      Resources resources = null;
      View childView;

      public void describeTo(Description description) {
        String idDescription = Integer.toString(recyclerViewId);
        if (this.resources != null) {
          try {
            idDescription = this.resources.getResourceName(recyclerViewId);
          } catch (Resources.NotFoundException var4) {
            idDescription = String.format("%s (resource name not found)", recyclerViewId);
          }
        }

        description.appendText("with id: " + idDescription);
      }

      public boolean matchesSafely(View view) {

        this.resources = view.getResources();

        if (childView == null) {
          RecyclerView recyclerView = (RecyclerView) view.getRootView().findViewById(recyclerViewId);
          if (recyclerView != null && recyclerView.getId() == recyclerViewId) {
            childView = recyclerView.findViewHolderForAdapterPosition(position).itemView;
          } else {
            return false;
          }
        }

        if (targetViewId == -1) {
          return view == childView;
        } else {
          View targetView = childView.findViewById(targetViewId);
          return view == targetView;
        }
      }
    };
  }

  public static RecyclerViewMatcher withRecyclerView(final int recyclerViewId) {
    return new RecyclerViewMatcher(recyclerViewId);
  }
}
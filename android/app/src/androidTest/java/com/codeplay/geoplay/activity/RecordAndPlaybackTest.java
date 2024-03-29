package com.codeplay.geoplay.activity;


import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.preference.PreferenceManager;
import androidx.test.espresso.ViewInteraction;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;
import androidx.test.rule.GrantPermissionRule;
import androidx.test.runner.AndroidJUnit4;

import com.codeplay.geoplay.R;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.os.SystemClock.sleep;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.pressBack;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class RecordAndPlaybackTest {

	@Rule
	public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

	@Rule
	public GrantPermissionRule mGrantPermissionRule =
			GrantPermissionRule.grant(
					"android.permission.ACCESS_FINE_LOCATION",
					"android.permission.ACCESS_COARSE_LOCATION",
					"android.permission.INTERNET",
					"android.permission.CAMERA",
					"android.permission.READ_EXTERNAL_STORAGE",
					"android.permission.RECORD_AUDIO",
					"android.permission.WRITE_EXTERNAL_STORAGE");

	@Before
	public void setPreferences() {
		Context targetContext = getInstrumentation().getTargetContext();
		SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(targetContext).edit();
		editor.putBoolean("SHOW_TUTORIAL_2", false).apply();
		editor.putBoolean("SHOW_TUTORIAL_1", false).apply();
	}

	@Test
	public void mainActivityTest2() {
		ViewInteraction bottomNavigationItemView = onView(
				allOf(withId(R.id.nav_record), withContentDescription("Record"),
						childAtPosition(
								childAtPosition(
										withId(R.id.bottom_navigation),
										0),
								1),
						isDisplayed()));
		bottomNavigationItemView.perform(click());

		sleep(1000);

		ViewInteraction appCompatImageView = onView(
				allOf(withId(R.id.btnStart),
						childAtPosition(
								allOf(withId(R.id.bottom_sheet),
										childAtPosition(
												withClassName(is("androidx.coordinatorlayout.widget.CoordinatorLayout")),
												1)),
								0),
						isDisplayed()));
		appCompatImageView.perform(click());

		sleep(5000);

		ViewInteraction appCompatImageView2 = onView(
				allOf(withId(R.id.btnStop),
						childAtPosition(
								allOf(withId(R.id.bottom_sheet),
										childAtPosition(
												withClassName(is("androidx.coordinatorlayout.widget.CoordinatorLayout")),
												1)),
								1),
						isDisplayed()));
		appCompatImageView2.perform(click());

        sleep(1000);

		pressBack();

		ViewInteraction recyclerView = onView(
				allOf(withId(R.id.lstVideos),
						childAtPosition(
								withClassName(is("android.widget.LinearLayout")),
								1)));
		recyclerView.perform(actionOnItemAtPosition(0, click()));

        sleep(6000);

		pressBack();
	}

	private static Matcher<View> childAtPosition(
			final Matcher<View> parentMatcher, final int position) {

		return new TypeSafeMatcher<View>() {
			@Override
			public void describeTo(Description description) {
				description.appendText("Child at position " + position + " in parent ");
				parentMatcher.describeTo(description);
			}

			@Override
			public boolean matchesSafely(View view) {
				ViewParent parent = view.getParent();
				return parent instanceof ViewGroup && parentMatcher.matches(parent)
						&& view.equals(((ViewGroup) parent).getChildAt(position));
			}
		};
	}
}

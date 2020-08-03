package com.codeplay.geoplay.activity;


import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.preference.PreferenceManager;
import androidx.test.espresso.DataInteraction;
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

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anything;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class MainActivityTest {

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
		PreferenceManager.getDefaultSharedPreferences(targetContext).edit()
				.putBoolean("SHOW_TUTORIAL_1", false)
				.putBoolean("SHOW_TUTORIAL_2", false)
				.apply();
	}

	@Test
	public void mainActivityTest() {
		ViewInteraction bottomNavigationItemView = onView(
				allOf(withId(R.id.nav_settings), withContentDescription("Settings"),
						childAtPosition(
								childAtPosition(
										withId(R.id.bottom_navigation),
										0),
								2),
						isDisplayed()));
		bottomNavigationItemView.perform(click());

		ViewInteraction recyclerView = onView(
				allOf(withId(R.id.recycler_view),
						childAtPosition(
								withId(android.R.id.list_container),
								0)));
		recyclerView.perform(actionOnItemAtPosition(1, click()));

		DataInteraction appCompatCheckedTextView = onData(anything())
				.inAdapterView(allOf(withId(R.id.select_dialog_listview),
						childAtPosition(
								withId(R.id.contentPanel),
								0)))
				.atPosition(3);
		appCompatCheckedTextView.perform(click());

		ViewInteraction recyclerView2 = onView(
				allOf(withId(R.id.recycler_view),
						childAtPosition(
								withId(android.R.id.list_container),
								0)));
		recyclerView2.perform(actionOnItemAtPosition(3, click()));

		ViewInteraction recyclerView3 = onView(
				allOf(withId(R.id.recycler_view),
						childAtPosition(
								withId(android.R.id.list_container),
								0)));
		recyclerView3.perform(actionOnItemAtPosition(4, click()));

		ViewInteraction appCompatButton = onView(
				allOf(withId(android.R.id.button1), withText("OK"),
						childAtPosition(
								childAtPosition(
										withId(R.id.buttonPanel),
										0),
								3)));
		appCompatButton.perform(scrollTo(), click());

		ViewInteraction recyclerView4 = onView(
				allOf(withId(R.id.recycler_view),
						childAtPosition(
								withId(android.R.id.list_container),
								0)));
		recyclerView4.perform(actionOnItemAtPosition(7, click()));

		DataInteraction appCompatCheckedTextView2 = onData(anything())
				.inAdapterView(allOf(withId(R.id.select_dialog_listview),
						childAtPosition(
								withId(R.id.contentPanel),
								0)))
				.atPosition(1);
		appCompatCheckedTextView2.perform(click());

		ViewInteraction recyclerView5 = onView(
				allOf(withId(R.id.recycler_view),
						childAtPosition(
								withId(android.R.id.list_container),
								0)));
		recyclerView5.perform(actionOnItemAtPosition(13, click()));
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

package com.codeplay.geoplay.activity;


import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.intent.Intents;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;
import androidx.test.rule.GrantPermissionRule;
import androidx.test.runner.AndroidJUnit4;

import com.codeplay.geoplay.R;
import com.google.firebase.auth.FirebaseAuth;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.os.SystemClock.sleep;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.pressImeActionButton;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class LoginTest {

	@Rule
	public ActivityTestRule<LoginActivity> mActivityTestRule = new ActivityTestRule<>(LoginActivity.class);


	@Rule
	public GrantPermissionRule mGrantPermissionRule =
			GrantPermissionRule.grant(
					"android.permission.ACCESS_FINE_LOCATION",
					"android.permission.ACCESS_COARSE_LOCATION",
					"android.permission.CAMERA",
					"android.permission.RECORD_AUDIO",
					"android.permission.READ_EXTERNAL_STORAGE",
					"android.permission.WRITE_EXTERNAL_STORAGE");



	@Test
	public void loginActivitiesTest() {
		ViewInteraction appCompatEditText = onView(
				allOf(withId(R.id.phone),
						isDisplayed()));
		appCompatEditText.perform(replaceText("9999988888"), closeSoftKeyboard());

		ViewInteraction appCompatButton = onView(
				allOf(withId(R.id.send_otp), withText("SEND OTP"),
						childAtPosition(
								childAtPosition(
										withClassName(is("android.widget.RelativeLayout")),
										1),
								3),
						isDisplayed()));
		appCompatButton.perform(click());

		sleep(3000);

		ViewInteraction appCompatEditText3 = onView(
				allOf(withId(R.id.et_otp),
						isDisplayed()));
		appCompatEditText3.perform(replaceText("123456"), closeSoftKeyboard());

		ViewInteraction appCompatEditText4 = onView(
				allOf(withId(R.id.name),
						isDisplayed()));
		appCompatEditText4.perform(replaceText("test_user"), closeSoftKeyboard());


		Intents.init();
		ViewInteraction appCompatButton2 = onView(
				allOf(withId(R.id.verify), withText("VERIFY"),
						isDisplayed()));
		appCompatButton2.perform(click());

		sleep(2000);

		intended(hasComponent(MainActivity.class.getName()));
		Intents.release();

		assert FirebaseAuth.getInstance().getCurrentUser() != null;
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

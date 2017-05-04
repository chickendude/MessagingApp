package com.teamtreehouse.ribbit.ui;


import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.teamtreehouse.ribbit.R;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.notNullValue;

/**
 * Created by crater-windoze on 5/5/2017.
 */
@RunWith(AndroidJUnit4.class)
public class LoginActivityTest {
	@Rule
	public ActivityTestRule<LoginActivity> activityTestRule = new ActivityTestRule<LoginActivity>(LoginActivity.class);

	@Test
	public void successfulLoginLogsIn() throws Exception {
		// Arrange
		String username = "ben";
		String password = "test";
		onView(withId(R.id.usernameField)).perform(typeText(username));
		onView(withId(R.id.passwordField)).perform(typeText(password));

		// Act
		onView(withId(R.id.loginButton)).perform(click());

		// Assert
		onView(withId(R.id.pager_title_strip)).check(matches(notNullValue()));
	}

	@Test
	public void unsuccessfulLoginFails() throws Exception {
		// Arrange
		String username = "benz";
		String password = "test";
		onView(withId(R.id.usernameField)).perform(typeText(username));
		onView(withId(R.id.passwordField)).perform(typeText(password));

		// Act
		onView(withId(R.id.loginButton)).perform(click());

		// Assert
		onView(withText("Oops!")).check(matches(isDisplayed()));
	}
}
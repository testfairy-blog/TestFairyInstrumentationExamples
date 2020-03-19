package com.testfairy.instrumentation.utils;

import android.content.Context;

import com.testfairy.SessionStateListener;
import com.testfairy.TestFairy;

import org.junit.Assert;

import java.util.Map;

import androidx.test.platform.app.InstrumentationRegistry;

/**
 * Utilities to integrate TestFairy into a test suite
 */
public class TestFairyInstrumentationUtil {
	/**
	 * App server endpoint
	 */
	public static String APP_SERVER_ENDPOINT;

	/**
	 * App token
	 */
	public static String APP_TOKEN;


	/**
	 * Setup app token globally for all tests
	 * @param appToken
	 */
	public static void setup(String appToken) {
		APP_TOKEN = appToken;
		APP_SERVER_ENDPOINT = "https://app.testfairy.com/services/";
	}

	/**
	 * Setup app token and app server endpoint globally for all tests
	 * @param appToken
	 * @param appServerEndpoint
	 */
	public static void setup(String appToken, String appServerEndpoint) {
		APP_TOKEN = appToken;
		APP_SERVER_ENDPOINT = appServerEndpoint;
	}

	/**
	 * Wrapper interface to run custom code in a TestFairy instrumentation
	 */
	public interface InstrumentationWrapper {
		void onRecord();
	}

	/**
	 * Calls provided wrapper inside a `TestFairy.begin()` - `TestFairy.stop()` block.
	 *
	 * @param wrapper
	 * @throws RuntimeException
	 */
	public static void wrapInstrumentation(String name, InstrumentationWrapper wrapper) throws RuntimeException {
		wrapInstrumentation(name, wrapper, null);
	}

	/**
	 * Calls provided wrapper inside a `TestFairy.begin()` - `TestFairy.stop()` block.
	 *
	 * @param wrapper
	 * @param options
	 * @throws RuntimeException
	 */
	public static void wrapInstrumentation(String name, InstrumentationWrapper wrapper, Map<String, String> options) throws RuntimeException {
		try {
			startInstrumentation(name, options);
			wrapper.onRecord();
		} finally {
			stopInstrumentation();
		}
	}

	/**
	 * Starts an instrumentation.
	 */
	public static void startInstrumentation(String name) {
		startInstrumentation(name, null);
	}

	/**
	 * Starts an instrumentation.
	 * @param options
	 */
	public static void startInstrumentation(String name, Map<String, String> options) {
		stopInstrumentation();

		final Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
		final ValueHolder<Boolean> sessionStartedOrFailed = new ValueHolder<>(false);

		TestFairy.setServerEndpoint(APP_SERVER_ENDPOINT);
		TestFairy.addSessionStateListener(new SessionStateListener() {
			@Override
			public void onSessionStarted(String s) {
				sessionStartedOrFailed.value = true;
			}
		});

		if (name != null) {
			TestFairy.setUserId(name);
		}

		if (options != null) {
			TestFairy.begin(context, APP_TOKEN, options);
		} else {
			TestFairy.begin(context, APP_TOKEN);
		}

		waitUntilValueHolderIsTrue(sessionStartedOrFailed, 10);

		if (!sessionStartedOrFailed.value) {
			Assert.fail("Session could not be started.");
		}

		safeSleep(1000);
	}

	/**
	 * Stops an instrumentation
	 */
	public static void stopInstrumentation() {
		TestFairy.stop();
	}

	/**
	 * Safely sleep to pause test thread.
	 * @param ms
	 */
	public static void safeSleep(long ms) {
		try {
			Thread.sleep(ms);
		} catch (InterruptedException ie) {
			// ignored
		}
	}

	private static void waitUntilValueHolderIsTrue(ValueHolder<Boolean> valueHolder, int timeoutInSeconds) {
		for (int i = 0; i < timeoutInSeconds; i++) {
			if (valueHolder.value) {
				return;
			}

			safeSleep(1000);
		}
	}

	private static class ValueHolder<T> {
		public volatile T value;

		public ValueHolder(T value) {
			this.value = value;
		}
	}
}

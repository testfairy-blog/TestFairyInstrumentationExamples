package com.testfairy.instrumentation.utils;

import android.content.Context;
import android.util.Log;

import com.testfairy.SessionStateListener;
import com.testfairy.TestFairy;

import org.junit.Assert;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import androidx.test.platform.app.InstrumentationRegistry;

/**
 * Utilities to integrate TestFairy into a test suite
 */
public class TestFairyInstrumentationUtil {

	private static final String TAG = "TestFairyInstrumentation";

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
		} catch (Throwable t) {
			logThrowable(t);
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

		Log.i(TAG, "Your TestFairy session: " +
				shortenSessionUrl(TestFairy.getSessionUrl())
		);

		Log.i(TAG, "Instrumentation session url: " + TestFairy.getSessionUrl());
	}

	/**
	 * Stops an instrumentation
	 */
	public static void stopInstrumentation() {
		if (TestFairy.getSessionUrl() != null) {
			Log.i(TAG, "Your TestFairy session: " +
					shortenSessionUrl(TestFairy.getSessionUrl())
			);

			Log.i(TAG, "Instrumentation session url: " + TestFairy.getSessionUrl());
		}

		TestFairy.stop();

		waitForTestFairyThreadsStop(10000);
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

	public static void logThrowable(Throwable t) {
		TestFairy.logThrowable(t);
		Log.e(TAG, "TestFairy detected an exception: ", t);
	}

	////////////////////////////////////////////

	private static final Pattern sessionUrlPattern = Pattern.compile("https:\\/\\/[\\w\\._-]+\\/projects\\/(\\d+)-[\\w-\\._]+\\/builds\\/(\\d+)\\/sessions\\/(\\d+)");

	private static boolean notEmpty(String str) {
		return str != null && str.length() > 0;
	}

	private static String shortenSessionUrl(String sessionUrl) {
		if (sessionUrl == null) return "Unrecorded session";

		try {
			Matcher matcher = sessionUrlPattern.matcher(sessionUrl);

			if (matcher.matches()) {
				String project = matcher.group(1);
				String build = matcher.group(2);
				String session = matcher.group(3);

				if (notEmpty(project) && notEmpty(build) && notEmpty(session)) {
					return String.format("https://tsfr.io/s/%s/%s/%s", project, build, session);
				}

				throw new NullPointerException("There are empty groups in the session url: " +
						String.format("https://tsfr.io/s/%s/%s/%s", project, build, session));
			} else {
				throw new IllegalArgumentException("Session url is unrecognized: " + sessionUrl);
			}
		} catch (Throwable t) {
			Log.w(TAG, "Session url cannot be shortened.", t);
			return sessionUrl;
		}
	}

	private static void waitForTestFairyThreadsStop(int timeToWait) {
		long end = System.currentTimeMillis() + timeToWait * 1000;
		while (System.currentTimeMillis() < end) {
			int count = countTestFairyAliveThreads();
			if (count == 0) {
				// matched expected number of threads
				return;
			}

			safeSleep(1000);
		}

		Assert.assertEquals(0, countTestFairyAliveThreads());
	}

	private static int countTestFairyAliveThreads() {
		List<String> stillAlive = new ArrayList<>();

		List<Thread> threads = getTestFairyAliveThreads();
		for (Thread thread : threads) {
			if (thread.getName().contains("testfairy-log-watchdog") || thread.getName().contains("TestFairyAndroidJUnitRunner")) {
				// "testfairy-log-watchdog" is a one time Runnable no need to kill.
				continue;
			}

			stillAlive.add(thread.getName());
		}

		String threadNames = "";
		if (stillAlive.size() > 0) {
			threadNames += stillAlive.toString() + ", ";
		}

		return stillAlive.size();
	}

	private static List<Thread> getTestFairyAliveThreads() {
		Set<Thread> threadSet = Thread.getAllStackTraces().keySet();
		Thread[] allThreads = threadSet.toArray(new Thread[threadSet.size()]);

		ArrayList<Thread> threads = new ArrayList<>();
		for (Thread thread : allThreads) {
			if (thread.getName().contains("testfairy") && thread.isAlive() && !thread.getName().contains("TestFairyAndroidJUnitRunner")) {
				threads.add(thread);
			}
		}

		return threads;
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

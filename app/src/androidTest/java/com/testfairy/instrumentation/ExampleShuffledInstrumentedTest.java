package com.testfairy.instrumentation;

import android.util.Log;

import com.testfairy.instrumentation.runner.TestFairyShuffledAndroidJUnit4ClassRunner;

import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Run your tests with `TestFairyShuffledAndroidJUnit4ClassRunner` if you
 * want execute your `@Test` cases in a randomized order.
 */
@RunWith(TestFairyShuffledAndroidJUnit4ClassRunner.class)
public class ExampleShuffledInstrumentedTest extends ExampleTestsBase {

	private static final String TAG = "ExampleShuffledInstrumentedTest";

	@Test
	public void a() {
		Log.d(TAG, "Running a");
	}

	@Test
	public void b() {
		Log.d(TAG, "Running b");
	}

	@Test
	public void c() {
		Log.d(TAG, "Running c");
	}

	@Test
	public void d() {
		Log.d(TAG, "Running d");
	}
}

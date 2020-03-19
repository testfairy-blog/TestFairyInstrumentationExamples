package com.testfairy.instrumentation.runner;

import android.util.Log;

import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Class runner which runs its tests in a TestFairy session. Not being able to
 * create a TestFairy session is considered a failure for the running test case.
 * Tests are run in a randomized order.
 */
public class TestFairyShuffledAndroidJUnit4ClassRunner extends TestFairyAndroidJUnit4ClassRunner {

	private static final String TAG = "TestFairyShuffledAndroidJUnit4ClassRunner";

	public TestFairyShuffledAndroidJUnit4ClassRunner(Class<?> klass) throws InitializationError {
		super(klass);
	}

	@Override
	protected List<FrameworkMethod> computeTestMethods() {
		List<FrameworkMethod> methods = new ArrayList<>(super.computeTestMethods());

		if (getName() != null) {
			Log.d(TAG, "Shuffling " + getName());
		}
		Collections.shuffle(methods);

		return methods;
	}
}

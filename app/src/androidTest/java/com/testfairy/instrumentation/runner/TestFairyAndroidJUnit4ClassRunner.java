package com.testfairy.instrumentation.runner;

import com.testfairy.instrumentation.utils.TestFairyInstrumentationUtil;

import org.junit.runner.Description;
import org.junit.runner.notification.RunListener;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;

import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;

/**
 * Class runner which runs its tests in a TestFairy session. Not being able to
 * create a TestFairy session is considered a failure for the running test case.
 */
public class TestFairyAndroidJUnit4ClassRunner extends AndroidJUnit4ClassRunner {

	private static final String TAG = "TestFairyAndroidJUnit4ClassRunner";

	public TestFairyAndroidJUnit4ClassRunner(Class<?> klass) throws InitializationError {
		super(klass);
	}

	@Override
	protected void runChild(FrameworkMethod method, RunNotifier notifier) {
		notifier.addListener(runListener);
		super.runChild(method, notifier);
		notifier.removeListener(runListener);
	}

	private final RunListener runListener = new RunListener() {
		@Override
		public void testStarted(Description description) throws Exception {
			super.testStarted(description);
			TestFairyInstrumentationUtil.startInstrumentation();
		}

		@Override
		public void testFinished(Description description) throws Exception {
			super.testFinished(description);
			TestFairyInstrumentationUtil.stopInstrumentation();
		}
	};
}

package com.testfairy.instrumentation.runner;

import android.util.Log;

import com.testfairy.TestFairy;
import com.testfairy.instrumentation.utils.TestFairyInstrumentationUtil;

import org.junit.runner.Description;
import org.junit.runner.notification.Failure;
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
			String name = null;

			String className = description.getClassName();
			String methodName = description.getMethodName();
			String displayName = description.getDisplayName();

			if (className != null && methodName != null) {
				String[] split = className.split("\\.");
				name = split[split.length - 1] + "." + methodName;
			} else if (displayName != null) {
				name = displayName;
			} else {
				name = description.toString();
			}

			TestFairyInstrumentationUtil.startInstrumentation(name);

			super.testStarted(description);
		}

		@Override
		public void testAssumptionFailure(Failure failure) {
			TestFairyInstrumentationUtil.logThrowable(failure.getException());

			super.testAssumptionFailure(failure);
		}

		@Override
		public void testFailure(Failure failure) throws Exception {
			TestFairyInstrumentationUtil.logThrowable(failure.getException());

			super.testFailure(failure);
		}

		@Override
		public void testFinished(Description description) throws Exception {
			TestFairyInstrumentationUtil.stopInstrumentation();

			super.testFinished(description);

		}
	};
}

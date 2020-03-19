package com.testfairy.instrumentation;

import android.content.Context;

import com.testfairy.instrumentation.utils.TestFairyInstrumentationUtil;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

import androidx.test.platform.app.InstrumentationRegistry;

import static org.junit.Assert.assertEquals;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleOptionallyInstrumentedTest extends ExampleTestsBase {

	@Rule
	public TestName testName = new TestName();

	@Test
	public void testAppContextPackageNameWithoutTestFairy() {
		// Context of the app under test.
		Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();

		assertEquals("com.testfairy.instrumentation", appContext.getPackageName());
	}

	@Test
	public void testAppContextPackageNameWithTestFairy() {
		String name = String.format("%s.%s", getClass().getSimpleName(), testName.getMethodName());
		TestFairyInstrumentationUtil.wrapInstrumentation(name, new TestFairyInstrumentationUtil.InstrumentationWrapper() {
			@Override
			public void onRecord() {
				// Context of the app under test.
				Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();

				assertEquals("com.testfairy.instrumentation", appContext.getPackageName());
			}
		});
	}
}

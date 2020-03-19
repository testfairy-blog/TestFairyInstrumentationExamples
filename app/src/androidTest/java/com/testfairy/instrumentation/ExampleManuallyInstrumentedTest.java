package com.testfairy.instrumentation;

import android.content.Context;

import com.testfairy.instrumentation.utils.TestFairyInstrumentationUtil;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

import androidx.test.platform.app.InstrumentationRegistry;

import static org.junit.Assert.assertEquals;

/**
 * Manual instrumentation invoke examples.
 *
 * You can define what an instrumentation is by manually calling
 * `TestFairyInstrumentationUtil.startInstrumentation()` and
 * `TestFairyInstrumentationUtil.stopInstrumentation()`
 * wherever you need.
 */

public class ExampleManuallyInstrumentedTest extends ExampleTestsBase {

	@Rule
	public TestName testName = new TestName();

	@Before
	public void before() {
		TestFairyInstrumentationUtil.startInstrumentation(String.format("%s.%s", getClass().getSimpleName(), testName.getMethodName()));
	}

	@After
	public void after() {
		TestFairyInstrumentationUtil.stopInstrumentation();
	}

	@Test
	public void testAppContextPackageName() throws InterruptedException {
		// Context of the app under test.
		Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();

		assertEquals("com.testfairy.instrumentation", appContext.getPackageName());

		for (int i = 0; i < 3; i++) {
			Thread.sleep(3000);
		}
	}
}

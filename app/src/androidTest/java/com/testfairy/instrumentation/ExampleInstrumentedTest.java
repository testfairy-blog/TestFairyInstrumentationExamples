package com.testfairy.instrumentation;

import android.content.Context;

import com.testfairy.instrumentation.runner.TestFairyAndroidJUnit4ClassRunner;

import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.platform.app.InstrumentationRegistry;

import static org.junit.Assert.assertEquals;

/**
 * Most basic TestFairy instrumentation. Run your test class with
 * `TestFairyAndroidJUnit4ClassRunner` to get a session per `@Test` case.
 * Tests will fail if TestFairy fails.
 */
@RunWith(TestFairyAndroidJUnit4ClassRunner.class)
public class ExampleInstrumentedTest extends ExampleTestsBase {

	@Test
	public void testAppContextPackageNameAutomatically() {
		// Context of the app under test.
		Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();

		assertEquals("com.testfairy.instrumentation", appContext.getPackageName() + ".wrong");
	}
}

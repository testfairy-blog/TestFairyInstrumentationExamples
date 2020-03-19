package com.testfairy.instrumentation;

import android.content.Context;

import com.testfairy.instrumentation.runner.TestFairyAndroidJUnit4ClassRunner;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

/**
 * Most basic TestFairy instrumentation. Run your test class with
 * `TestFairyAndroidJUnit4ClassRunner` to get a session per `@Test` case.
 * Tests will fail if TestFairy fails.
 */
@RunWith(TestFairyAndroidJUnit4ClassRunner.class)
public class ExampleInstrumentedTest extends ExampleTestsBase {

	@Test
	public void testAppContextPackageName() {
		// Context of the app under test.
		Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();

		assertEquals("com.testfairy.instrumentation", appContext.getPackageName());
	}
}

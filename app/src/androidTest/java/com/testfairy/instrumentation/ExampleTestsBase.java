package com.testfairy.instrumentation;

import com.testfairy.instrumentation.utils.TestFairyInstrumentationUtil;

/**
 * Make sure your instrumentations are initialized with your app token.
 * On-prem can also provide app server endpoint as an optional parameter.
 */
public class ExampleTestsBase {

	public ExampleTestsBase() {
		TestFairyInstrumentationUtil.setup("SDK-gLeZiE9i");
	}
}

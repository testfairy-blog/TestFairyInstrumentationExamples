# TestFairyInstrumentationExamples

This repository showcases TestFairy usage in an Android instrumentation test.

Following examples are provided:

* [Basic instrumentation with a class runner](https://github.com/testfairy-blog/TestFairyInstrumentationExamples/blob/master/app/src/androidTest/java/com/testfairy/instrumentation/ExampleInstrumentedTest.java)
* [Shuffled instrumentation with a class runner](https://github.com/testfairy-blog/TestFairyInstrumentationExamples/blob/master/app/src/androidTest/java/com/testfairy/instrumentation/ExampleShuffledInstrumentedTest.java)
* [Wrapped instrumentations](https://github.com/testfairy-blog/TestFairyInstrumentationExamples/blob/master/app/src/androidTest/java/com/testfairy/instrumentation/ExampleOptionallyInstrumentedTest.java)
* [Manual instrumentation](https://github.com/testfairy-blog/TestFairyInstrumentationExamples/blob/master/app/src/androidTest/java/com/testfairy/instrumentation/ExampleManuallyInstrumentedTest.java)

You can find the companion docs and blog post [here](https://blog.testfairy.com/device-farm-instrumentation-with-testfairy-android-sdk/).

## Installation

1. Add these to *app/build.gradle*

```
dependencies {
    androidTestImplementation 'androidx.test.ext:junit:1.1.1'
    androidTestImplementation 'androidx.test.espresso:espresso-core:3.2.0'
    androidTestImplementation 'testfairy:testfairy-android-sdk:1.+@aar'
}
```

2. Copy [these](https://github.com/testfairy-blog/TestFairyInstrumentationExamples/tree/master/app/src/androidTest/java/com/testfairy/instrumentation) into your project. 
3. Modify [ExampleTestBase.java](https://github.com/testfairy-blog/TestFairyInstrumentationExamples/blob/master/app/src/androidTest/java/com/testfairy/instrumentation/ExampleTestsBase.java) to set your app token.

It is advised that you should consider these files not as dependencies but as starting points for your custom use cases. Therefore, further modifications to the contents of these files are highly encouraged. We'd love it if you sent a pull requests with suggestions and bugfixes.

  

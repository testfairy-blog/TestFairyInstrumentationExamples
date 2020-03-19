package com.testfairy.instrumentation.runner;

import android.Manifest;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;

import com.testfairy.instrumentation.utils.TestFairyInstrumentationUtil;

import java.lang.reflect.Method;

import androidx.test.espresso.IdlingPolicies;
import androidx.test.espresso.IdlingPolicy;
import androidx.test.runner.AndroidJUnitRunner;

/**
 * JUnit runner to initialize test application in a useful manner. Usage is optional
 * for non UI related tests.
 */
public final class TestFairyAndroidJUnitRunner extends AndroidJUnitRunner {

	private static final String TAG = "TestFairyAndroidJUnitRunner";

	@Override
	public void onCreate(Bundle arguments) {
		super.onCreate(arguments);

		Log.d(TAG, "Created.");
	}

	@Override
	public void onDestroy() {
		// Allow TestFairy to send remaining sessions to server if forgotten.
		TestFairyInstrumentationUtil.stopInstrumentation();

		super.onDestroy();

		Log.d(TAG, "Destroyed.");
	}

	@Override
	public void onStart() {
		super.onStart();

		runOnMainSync(new Runnable() {
			@Override
			public void run() {
				try {
					Context app = TestFairyAndroidJUnitRunner.this.getTargetContext().getApplicationContext();

					TestFairyAndroidJUnitRunner.this.disableAnimations(app);

					String name = TestFairyAndroidJUnitRunner.class.getSimpleName();
					unlockScreen(app, name);
					keepSceenAwake(app, name);

					IdlingPolicy dynamicIdlingResourceErrorPolicy = IdlingPolicies.getDynamicIdlingResourceErrorPolicy();
					IdlingPolicies.setIdlingResourceTimeout(dynamicIdlingResourceErrorPolicy.getIdleTimeout() * 5, dynamicIdlingResourceErrorPolicy.getIdleTimeoutUnit());
					IdlingPolicies.setMasterPolicyTimeout(dynamicIdlingResourceErrorPolicy.getIdleTimeout() * 5, dynamicIdlingResourceErrorPolicy.getIdleTimeoutUnit());
				} catch (Throwable t) {
					Log.e(TAG, "Exception during TestFairyAndroidJUnitRunner.onStart()", t);
				}
			}
		});
	}

	private void keepSceenAwake(Context app, String name) {
		PowerManager power = (PowerManager) app.getSystemService(Context.POWER_SERVICE);
		power.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.ON_AFTER_RELEASE, name)
				.acquire();
	}

	private void unlockScreen(Context app, String name) {
		KeyguardManager keyguard = (KeyguardManager) app.getSystemService(Context.KEYGUARD_SERVICE);
		keyguard.newKeyguardLock(name).disableKeyguard();
	}

	private void disableAnimations(Context context) {
		int permStatus = context.checkCallingOrSelfPermission(Manifest.permission.SET_ANIMATION_SCALE);
		if (permStatus == PackageManager.PERMISSION_GRANTED) {
			setSystemAnimationsScale(0.0f);
		}
	}

	private void enableAnimations(Context context) {
		int permStatus = context.checkCallingOrSelfPermission(Manifest.permission.SET_ANIMATION_SCALE);
		if (permStatus == PackageManager.PERMISSION_GRANTED) {
			setSystemAnimationsScale(1.0f);
		}
	}

	private void setSystemAnimationsScale(float animationScale) {
		try {
			Class windowManagerStubClazz = Class.forName("android.view.IWindowManager$Stub");
			Method asInterface = windowManagerStubClazz.getDeclaredMethod("asInterface", IBinder.class);
			Class serviceManagerClazz = Class.forName("android.os.ServiceManager");
			Method getService = serviceManagerClazz.getDeclaredMethod("getService", String.class);
			Class windowManagerClazz = Class.forName("android.view.IWindowManager");
			Method setAnimationScales = windowManagerClazz.getDeclaredMethod("setAnimationScales", float[].class);
			Method getAnimationScales = windowManagerClazz.getDeclaredMethod("getAnimationScales");

			IBinder windowManagerBinder = (IBinder) getService.invoke(null, "window");
			Object windowManagerObj = asInterface.invoke(null, windowManagerBinder);
			float[] currentScales = (float[]) getAnimationScales.invoke(windowManagerObj);
			for (int i = 0; i < currentScales.length; i++) {
				currentScales[i] = animationScale;
			}
			setAnimationScales.invoke(windowManagerObj, new Object[]{currentScales});
			Log.d(TAG, "Changed permissions of animations");
		} catch (Throwable e) {
			Log.e(TAG, String.format("Could not change animation scale to %s :'(", animationScale));
		}
	}
} 

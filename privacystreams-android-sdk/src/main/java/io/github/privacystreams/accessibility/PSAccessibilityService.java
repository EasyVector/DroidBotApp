package io.github.privacystreams.accessibility;

import android.accessibilityservice.AccessibilityService;
import android.content.Intent;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.HashSet;
import java.util.Set;

import io.github.privacystreams.core.Callback;
import io.github.privacystreams.core.Item;
import io.github.privacystreams.core.UQI;
import io.github.privacystreams.core.purposes.Purpose;

/**
 * PrivacyStreams accessibility service
 */
public class PSAccessibilityService extends AccessibilityService {
    private static final String TAG = "PSAccessibilityService";
    private static Set<AccEventProvider> accEventProviders = new HashSet();
    public static boolean enabled = false;
    private UQI uqi;

    /* access modifiers changed from: protected */
    public void onServiceConnected() {
        enabled = true;
        super.onServiceConnected();
        this.uqi = new UQI(this);
        this.uqi.getData(AccEvent.asUpdates(), Purpose.FEATURE("DroidBot accessibility event bridge")).logOverSocket("AccEvent").forEach(new Callback<Item>() {
            /* access modifiers changed from: protected */
            public void onInput(Item input) {
            }
        });
    }

    public boolean onUnbind(Intent intent) {
        enabled = false;
        this.uqi.stopAll();
        return super.onUnbind(intent);
    }

    public void onAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        if (accessibilityEvent != null) {
            AccessibilityNodeInfo rootNode = getRootInActiveWindow();
            for (AccEventProvider provider : accEventProviders) {
                provider.handleAccessibilityEvent(accessibilityEvent, rootNode);
            }
        }
    }

    public void onInterrupt() {
    }

    static void registerProvider(AccEventProvider provider) {
        if (provider != null) {
            accEventProviders.add(provider);
        }
    }

    static void unregisterProvider(AccEventProvider provider) {
        if (provider != null && accEventProviders.contains(provider)) {
            accEventProviders.remove(provider);
        }
    }
}

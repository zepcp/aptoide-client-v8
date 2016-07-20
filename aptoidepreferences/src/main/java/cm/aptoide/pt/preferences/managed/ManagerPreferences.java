/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 07/07/2016.
 */

package cm.aptoide.pt.preferences.managed;

import android.preference.PreferenceManager;

import cm.aptoide.pt.preferences.Application;

/**
 * Created by neuro on 21-04-2016.
 */
public class ManagerPreferences {

	public static boolean getHWSpecsFilter() {
		return Preferences.get().getBoolean(ManagedKeys.HWSPECS_FILTER, true);
	}

	public static int getLastPushNotificationId() {
		return Preferences.get().getInt(ManagedKeys.LAST_PUSH_NOTIFICATION_ID, 0);
	}

	public static void setLastPushNotificationId(int notificationId) {
		Preferences.get().edit().putInt(ManagedKeys.LAST_PUSH_NOTIFICATION_ID, notificationId).apply();
	}

	public static boolean getAnimationsEnabledStatus() {
		return PreferenceManager.getDefaultSharedPreferences(Application.getContext()).getBoolean(ManagedKeys.ANIMATIONS_ENABLED, true);
	}

	public static boolean isAutoUpdateEnable() {
		return PreferenceManager.getDefaultSharedPreferences(Application.getContext()).getBoolean(ManagedKeys.CHECK_AUTO_UPDATE, true);
	}

	public static boolean isAllwaysUpdate() {
		return PreferenceManager.getDefaultSharedPreferences(Application.getContext()).getBoolean(ManagedKeys.PREF_ALWAYS_UPDATE, false);
	}

	public static int getLastUpdates() {
		return PreferenceManager.getDefaultSharedPreferences(Application.getContext()).getInt(ManagedKeys.LAST_UPDATES_KEY, 0);
	}

	public static void setLastUpdates(int lastUpdates) {
		Preferences.get().edit().putInt(ManagedKeys.LAST_UPDATES_KEY, lastUpdates).apply();
	}

	public static long getCacheLimit() {
		String chacheLimit = PreferenceManager.getDefaultSharedPreferences(Application.getContext()).getString(ManagedKeys.MAX_FILE_CACHE, "200");
		try {
			return Long.parseLong(chacheLimit);
		} catch (Exception e) {
			return 200;
		}
	}
}

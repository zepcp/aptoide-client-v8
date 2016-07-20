/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 06/07/2016.
 */

package cm.aptoide.pt.v8engine;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.Iterator;
import java.util.List;

import cm.aptoide.pt.model.v7.Event;
import cm.aptoide.pt.model.v7.store.GetStore;
import cm.aptoide.pt.model.v7.store.GetStoreTabs;
import cm.aptoide.pt.v8engine.fragment.implementations.AppsTimelineFragment;
import cm.aptoide.pt.v8engine.fragment.implementations.DownloadsFragment;
import cm.aptoide.pt.v8engine.fragment.implementations.StoreTabGridRecyclerFragment;
import cm.aptoide.pt.v8engine.fragment.implementations.SubscribedStoresFragment;
import cm.aptoide.pt.v8engine.fragment.implementations.UpdatesFragment;

/**
 * Created by neuro on 28-04-2016.
 */
public class StorePagerAdapter extends FragmentStatePagerAdapter {

	private final List<GetStoreTabs.Tab> tabs;
	private String storeTheme;

	public StorePagerAdapter(FragmentManager fm, GetStore getStore) {
		super(fm);
		tabs = getStore.getNodes().getTabs().getList();
		if (getStore.getNodes().getMeta().getData().getId() != 15) {
			storeTheme = getStore.getNodes().getMeta().getData().getAppearance().getTheme();
		}
		validateGetStore();
	}

	private void validateGetStore() {
		Iterator<GetStoreTabs.Tab> iterator = tabs.iterator();
		while (iterator.hasNext()) {
			GetStoreTabs.Tab next = iterator.next();

			if (next.getEvent().getName() == null || next.getEvent().getType() == null) {
				iterator.remove();
			}
		}
	}

	@Override
	public Fragment getItem(int position) {

		GetStoreTabs.Tab tab = tabs.get(position);
		Event event = tab.getEvent();

		switch (event.getType()) {
			case API:
				return caseAPI(tab);
			case CLIENT:
				return caseClient(event);
			default:
				// Safe to throw exception as the tab should be filtered prior to getting here.
				throw new RuntimeException("Fragment type not implemented!");
		}
	}

	private Fragment caseAPI(GetStoreTabs.Tab tab) {
		Event event = tab.getEvent();
		switch (event.getName()) {
			case getUserTimeline:
				return AppsTimelineFragment.newInstance(event.getAction());
			default:
				return StoreTabGridRecyclerFragment.newInstance(event, tab.getLabel(), storeTheme);
		}
	}

	private Fragment caseClient(Event event) {
		switch (event.getName()) {
			case myStores:
				return SubscribedStoresFragment.newInstance();
			case myUpdates:
				return UpdatesFragment.newInstance();
			case myDownloads:
				return DownloadsFragment.newInstance();
			default:
				// Safe to throw exception as the tab should be filtered prior to getting here.
				throw new RuntimeException("Fragment type not implemented!");
		}
	}

	@Override
	public int getCount() {
		return tabs.size();
	}

	@Override
	public CharSequence getPageTitle(int position) {
		return tabs.get(position).getLabel();
	}
}

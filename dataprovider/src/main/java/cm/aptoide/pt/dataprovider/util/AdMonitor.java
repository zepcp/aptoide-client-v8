package cm.aptoide.pt.dataprovider.util;

import android.content.Context;
import android.content.Intent;

import com.facebook.stetho.json.ObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.io.IOException;

import cm.aptoide.pt.dataprovider.DataProvider;
import cm.aptoide.pt.dataprovider.model.MinimalAd;
import cm.aptoide.pt.model.v2.GetAdsResponse;
import cm.aptoide.pt.networkclient.WebService;

/**
 * Created by daria on 7/29/2016.
 */
public class AdMonitor {

	public static void sendDataToAdMonitor(long adId, String action) {

		Intent intent = new Intent();
		intent.setAction(action);
		intent.setPackage("com.example.daria.admonitor");
		intent.putExtra(action, adId);

		//intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
		Context c = DataProvider.getContext();
		c.sendBroadcast(intent);
	}

	public static void sendGetAdsToMonitor(String parsedGetAdsResponse) {

		Intent intent = new Intent();
		intent.setAction("getAds");
		intent.setPackage("com.example.daria.admonitor");
		intent.putExtra("getAds", parsedGetAdsResponse);

		Context c = DataProvider.getContext();
		c.sendBroadcast(intent);
	}


}

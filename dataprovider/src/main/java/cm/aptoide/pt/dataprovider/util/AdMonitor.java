package cm.aptoide.pt.dataprovider.util;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;

import com.fasterxml.jackson.core.JsonProcessingException;

import java.io.IOException;

import cm.aptoide.pt.dataprovider.DataProvider;
import cm.aptoide.pt.dataprovider.model.MinimalAd;
import cm.aptoide.pt.dataprovider.ws.v2.aptwords.GetAdsRequest;
import cm.aptoide.pt.model.v2.GetAdsResponse;
import cm.aptoide.pt.networkclient.WebService;

/**
 * Created by daria on 7/29/2016.
 */
public class AdMonitor {

	public static void sendDataToAdMonitor(long adId, String action) {

		Context c = DataProvider.getContext();
		//launchAdMonitor(c);

		Intent intent = new Intent();
		intent.setAction(action);
		intent.setPackage("com.example.daria.admonitor");
		intent.putExtra(action, adId);

		//intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);

		c.sendBroadcast(intent);
	}

	public static void sendCpcCpiCpdToMonitor(long adId, String action, String response){
		Context c = DataProvider.getContext();
		//launchAdMonitor(c);

		Intent intent = new Intent();
		intent.setAction(action);
		intent.setPackage("com.example.daria.admonitor");
		intent.putExtra(action, adId);

		if( response != null){
			intent.putExtra(action+"Response", response);
		}
		//intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);

		c.sendBroadcast(intent);
	}

	public static void sendGetAdsToMonitor(String parsedGetAdsResponse, String getAdsRequest, String location) {

		Context c = DataProvider.getContext();

		//launchAdMonitor(c);

		Log.i("Tag", "GetADs");
		Intent intent = new Intent();
		intent.setAction("getAds");
		intent.setPackage("com.example.daria.admonitor");
		intent.putExtra("getAds", parsedGetAdsResponse);
		intent.putExtra("getAdsRequest", getAdsRequest);
		intent.putExtra("location", location);

		c.sendBroadcast(intent);
	}

	private static void launchAdMonitor(Context c) {
		Intent launchIntent = c.getPackageManager().getLaunchIntentForPackage("com.example.daria.admonitor");
		if (launchIntent != null) {
			c.startActivity(launchIntent);//null pointer check in case package name was not found
		}
	}

	public static void sendReferrerToAdMonitor(long adId, String referrer, String referrerExtracted) {
		Context c = DataProvider.getContext();
		//launchAdMonitor(c);

		Intent intent = new Intent();
		intent.setAction(referrerExtracted);
		intent.setPackage("com.example.daria.admonitor");
		intent.putExtra(referrerExtracted, adId);


		if( referrer != null){
			intent.putExtra("referrer", referrer);
		}
		//intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);

		c.sendBroadcast(intent);
	}
}

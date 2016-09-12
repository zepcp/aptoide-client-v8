/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 04/08/2016.
 */

package cm.aptoide.pt.dataprovider.ws.v2.aptwords;

import java.util.Map;

import cm.aptoide.pt.dataprovider.DataProvider;
import cm.aptoide.pt.dataprovider.repository.IdsRepository;
import cm.aptoide.pt.dataprovider.ws.Api;
import cm.aptoide.pt.model.v2.GetAdsResponse;
import cm.aptoide.pt.networkclient.WebService;
import cm.aptoide.pt.networkclient.okhttp.OkHttpClientFactory;
import cm.aptoide.pt.networkclient.util.HashMapNotNull;
import cm.aptoide.pt.preferences.secure.SecurePreferencesImplementation;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import rx.Observable;

/**
 * Created by neuro on 08-06-2016.
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class ad extends Aptwords<GetAdsResponse> {

	private String categories;
	private String excludedNetworks;

	public ad() {
		super(OkHttpClientFactory.getSingletonClient(), WebService.getDefaultConverter(), new IdsRepository(SecurePreferencesImplementation.getInstance(), DataProvider.getContext()));
	}

	@Override
	protected Observable<GetAdsResponse> loadDataFromNetwork(Interfaces interfaces, boolean bypassCache) {

		Map<String,String> parameters = new HashMapNotNull<>();

		parameters.put("q", Api.Q);
		parameters.put("lang", Api.LANG);

		return interfaces.getAds(parameters);
	}
}

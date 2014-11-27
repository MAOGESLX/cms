package com.webpagebytes.cms.cache.local;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import com.webpagebytes.cms.cache.WPBMessagesCache;
import com.webpagebytes.cms.cmsdata.WPBFile;
import com.webpagebytes.cms.cmsdata.WPBMessage;
import com.webpagebytes.cms.datautility.WPBAdminDataStorage;
import com.webpagebytes.cms.datautility.WPBAdminDataStorageFactory;
import com.webpagebytes.cms.exception.WPBIOException;

public class WPBLocalMessagesCache implements WPBMessagesCache {
	long cacheFingerPrint = 0;
	Map<String, Map<String, String>> cacheMessages;
	
	private WPBAdminDataStorage dataStorage;
	private static final Object lock = new Object();

	public WPBLocalMessagesCache()
	{
		dataStorage = WPBAdminDataStorageFactory.getInstance();
		try
		{
			if (dataStorage != null)
			{
				Refresh();
			}
		} catch (WPBIOException e)
		{
			
		}
	}
	
	private String lcidFromLocale(Locale locale)
	{
		return (locale.getCountry().length()>0) ? (locale.getLanguage() + "_" + locale.getCountry()): locale.getLanguage();  
	}

	public Map<String, String> getAllMessages(Locale locale) throws WPBIOException
	{
		String lcid = lcidFromLocale(locale);
		return getAllMessages(lcid);
	}
	
	public Map<String, String> getAllMessages(String lcid) throws WPBIOException
	{
		if (cacheMessages != null)
		{
			return cacheMessages.get(lcid);
		}
		return new HashMap<String, String>();
	}
	
	public Long getFingerPrint(Locale locale)
	{
		return cacheFingerPrint;
	}
	
	public void Refresh() throws WPBIOException {
		synchronized (lock) {
			Map<String, Map<String, String>> tempCache = new HashMap<String, Map<String, String>>();
			List<WPBMessage> records = dataStorage.getAllRecords(WPBMessage.class);
			for(WPBMessage item: records)
			{
				String lcid = item.getLcid();
				Map<String, String> lcidRecords = tempCache.get(lcid);
				if (null == lcidRecords)
				{
					lcidRecords = new HashMap<String, String>();
					tempCache.put(lcid, lcidRecords);
				}
				lcidRecords.put(item.getName(), item.getValue());
			}
			cacheMessages =  tempCache;
			Random r = new Random();
			cacheFingerPrint = r.nextLong();
		}
		
	}
	
	public Set<String> getSupportedLocales()
	{
		return cacheMessages.keySet();
	}
}

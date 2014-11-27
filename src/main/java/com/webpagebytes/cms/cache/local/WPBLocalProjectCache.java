package com.webpagebytes.cms.cache.local;

import java.util.Calendar;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import com.webpagebytes.cms.cache.WPBProjectCache;
import com.webpagebytes.cms.cmsdata.WPBArticle;
import com.webpagebytes.cms.cmsdata.WPBProject;
import com.webpagebytes.cms.datautility.WPBAdminDataStorage;
import com.webpagebytes.cms.datautility.WPBAdminDataStorageFactory;
import com.webpagebytes.cms.exception.WPBIOException;
import com.webpagebytes.cms.utility.Pair;

public class WPBLocalProjectCache implements WPBProjectCache {
	private WPBProject project;
	Pair<String, String> defaultLocale;
	Set<String> supportedLanguages;
	private WPBAdminDataStorage dataStorage;
	private static final Object lock = new Object();

	public WPBLocalProjectCache()
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
	public String getDefaultLanguage() throws WPBIOException
	{
		return project.getDefaultLanguage();
	}
	
	public Pair<String, String> getDefaultLocale() throws WPBIOException
	{
		return defaultLocale;
	}
	public Set<String> getSupportedLocales() throws WPBIOException
	{
		return supportedLanguages;
	}
	public WPBProject getProject() throws WPBIOException
	{
		return project;
	}
	
	private WPBProject createDefaultProject() throws WPBIOException
	{
		WPBProject project = new WPBProject();
		project.setPrivkey(WPBProject.PROJECT_KEY);
		project.setDefaultLanguage("en");
		project.setSupportedLanguages("en");
		project.setLastModified(Calendar.getInstance(TimeZone.getTimeZone("GMT")).getTime());	
		dataStorage.addWithKey(project);		
		return project;
	}
	
	public void Refresh() throws WPBIOException {
		synchronized (lock) {
			project = dataStorage.get(WPBProject.PROJECT_KEY, WPBProject.class);
			if (null == project)
			{
				project = createDefaultProject();
			}
			String defaultLanguage = project.getDefaultLanguage();
			String[] langs_ = defaultLanguage.split("_");
			if (langs_.length == 1)
			{
				defaultLocale = new Pair<String, String>(langs_[0], "");
			} else
			if (langs_.length == 2)
			{
				defaultLocale = new Pair<String, String>(langs_[0], langs_[1]);
			} else
				throw new WPBIOException("Invalid default language");
			
			supportedLanguages = project.getSupportedLanguagesSet();
		}
	}
}

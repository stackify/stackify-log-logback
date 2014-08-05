/*
 * StackifyErrorAppenderTest.java
 * Copyright 2013 Stackify
 */
package com.stackify.log.logback;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import ch.qos.logback.classic.spi.ILoggingEvent;

import com.stackify.api.common.ApiConfiguration;
import com.stackify.api.common.log.LogAppender;

/**
 * StackifyErrorAppenderTest
 * @author Eric Martin
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({StackifyErrorAppender.class})
public class StackifyErrorAppenderTest {

	/**
	 * testGetSetApiUrl
	 */
	@Test
	public void testGetSetApiUrl() {
		String apiUrl = "apiUrl";
		StackifyErrorAppender appender = new StackifyErrorAppender();
		Assert.assertEquals("https://api.stackify.com", appender.getApiUrl());
		appender.setApiUrl(apiUrl);
		Assert.assertEquals(apiUrl, appender.getApiUrl());
	}
	
	/**
	 * testGetSetApiKey
	 */
	@Test
	public void testGetSetApiKey() {
		String apiKey = "apiKey";
		StackifyErrorAppender appender = new StackifyErrorAppender();
		Assert.assertNull(appender.getApiKey());
		appender.setApiKey(apiKey);
		Assert.assertEquals(apiKey, appender.getApiKey());
	}
	
	/**
	 * testGetSetApplication
	 */
	@Test
	public void testGetSetApplication() {
		String application = "application";
		StackifyErrorAppender appender = new StackifyErrorAppender();
		Assert.assertNull(appender.getApplication());
		appender.setApplication(application);
		Assert.assertEquals(application, appender.getApplication());
	}
		
	/**
	 * testGetSetEnvironment
	 */
	@Test
	public void testGetSetEnvironment() {
		String environment = "environment";
		StackifyErrorAppender appender = new StackifyErrorAppender();
		Assert.assertNull(appender.getEnvironment());
		appender.setEnvironment(environment);
		Assert.assertEquals(environment, appender.getEnvironment());
	}
		
	/**
	 * testStartAppendStop
	 * @throws Exception 
	 */
	@Test
	public void testStartAppendStop() throws Exception {
		String application = "application";
		String environment = "environment";

		StackifyErrorAppender appender = new StackifyErrorAppender();
		appender.setApiKey("key");
		appender.setApplication(application);
		appender.setEnvironment(environment);
		
		LogAppender<ILoggingEvent> logAppender = Mockito.mock(LogAppender.class);
		
		PowerMockito.whenNew(LogAppender.class).withAnyArguments().thenReturn(logAppender);

		appender.start();
		
		Mockito.verify(logAppender).activate(Mockito.any(ApiConfiguration.class));
		
		ILoggingEvent event = Mockito.mock(ILoggingEvent.class);
		appender.doAppend(event);
		
		Mockito.verify(logAppender).appendError(event);

		appender.stop();
		
		Mockito.verify(logAppender).close();
	}
}

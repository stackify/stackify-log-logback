/*
 * Copyright 2014 Stackify
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
 * StackifyLogAppender JUnit Test
 * @author Eric Martin
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({StackifyLogAppender.class})
public class StackifyLogAppenderTest {

	/**
	 * testGetSetApiUrl
	 */
	@Test
	public void testGetSetApiUrl() {
		String apiUrl = "apiUrl";
		StackifyLogAppender appender = new StackifyLogAppender();
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
		StackifyLogAppender appender = new StackifyLogAppender();
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
		StackifyLogAppender appender = new StackifyLogAppender();
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
		StackifyLogAppender appender = new StackifyLogAppender();
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

		StackifyLogAppender appender = new StackifyLogAppender();
		appender.setApiKey("key");
		appender.setApplication(application);
		appender.setEnvironment(environment);
		
		LogAppender<ILoggingEvent> logAppender = Mockito.mock(LogAppender.class);
		
		PowerMockito.whenNew(LogAppender.class).withAnyArguments().thenReturn(logAppender);

		appender.start();
		
		Mockito.verify(logAppender).activate(Mockito.any(ApiConfiguration.class));
		
		ILoggingEvent event = Mockito.mock(ILoggingEvent.class);
		appender.doAppend(event);
		
		Mockito.verify(logAppender).append(event);

		appender.stop();
		
		Mockito.verify(logAppender).close();
	}
	
	/**
	 * testStartException
	 * @throws Exception 
	 */
	@Test
	public void testStartException() throws Exception {
		StackifyLogAppender appender = new StackifyLogAppender();
		appender.setApiKey("key");
		appender.setApplication("application");
		appender.setEnvironment("environment");
		
		StackifyLogAppender appenderSpy = PowerMockito.spy(appender);
		
		LogAppender<ILoggingEvent> logAppender = Mockito.mock(LogAppender.class);
		Mockito.doThrow(new RuntimeException()).when(logAppender).activate(Mockito.any(ApiConfiguration.class));
		
		PowerMockito.whenNew(LogAppender.class).withAnyArguments().thenReturn(logAppender);

		appenderSpy.start();
		
		Mockito.verify(appenderSpy).addError(Mockito.anyString(), Mockito.any(Exception.class));
	}
	
	/**
	 * testAppendException
	 * @throws Exception 
	 */
	@Test
	public void testAppendException() throws Exception {
		StackifyLogAppender appender = new StackifyLogAppender();
		appender.setApiKey("key");
		appender.setApplication("application");
		appender.setEnvironment("environment");
		
		StackifyLogAppender appenderSpy = PowerMockito.spy(appender);
		
		LogAppender<ILoggingEvent> logAppender = Mockito.mock(LogAppender.class);
		Mockito.doThrow(new RuntimeException()).when(logAppender).append(Mockito.any(ILoggingEvent.class));
		
		PowerMockito.whenNew(LogAppender.class).withAnyArguments().thenReturn(logAppender);

		appenderSpy.append(Mockito.mock(ILoggingEvent.class));
		
		Mockito.verify(appenderSpy).addError(Mockito.anyString(), Mockito.any(Exception.class));
	}
	
	/**
	 * testStopException
	 * @throws Exception 
	 */
	@Test
	public void testStopException() throws Exception {
		StackifyLogAppender appender = new StackifyLogAppender();
		appender.setApiKey("key");
		appender.setApplication("application");
		appender.setEnvironment("environment");
		
		StackifyLogAppender appenderSpy = PowerMockito.spy(appender);
		
		LogAppender<ILoggingEvent> logAppender = Mockito.mock(LogAppender.class);
		Mockito.doThrow(new RuntimeException()).when(logAppender).close();
		
		PowerMockito.whenNew(LogAppender.class).withAnyArguments().thenReturn(logAppender);

		appenderSpy.stop();
		
		Mockito.verify(appenderSpy).addError(Mockito.anyString(), Mockito.any(Exception.class));
	}
}

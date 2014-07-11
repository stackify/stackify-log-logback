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
import org.mockito.Mockito;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.ThrowableProxy;

import com.google.common.base.Optional;
import com.stackify.api.EnvironmentDetail;
import com.stackify.api.LogMsg;
import com.stackify.api.StackifyError;

/**
 * ILoggingEventAdapter JUnit Test
 * @author Eric Martin
 */
public class ILoggingEventAdapterTest {

	/**
	 * testGetThrowable
	 */
	@Test
	public void testGetThrowable() {
		ILoggingEvent event = Mockito.mock(ILoggingEvent.class);
		
		ILoggingEventAdapter adapter = new ILoggingEventAdapter(Mockito.mock(EnvironmentDetail.class));
		Optional<Throwable> throwable = adapter.getThrowable(event);
		
		Assert.assertFalse(throwable.isPresent());
	}
	
	/**
	 * testGetThrowableWithoutException
	 */
	@Test
	public void testGetThrowableWithoutException() {
		ThrowableProxy proxy = Mockito.mock(ThrowableProxy.class);
		Mockito.when(proxy.getThrowable()).thenReturn(new NullPointerException());
		
		ILoggingEvent event = Mockito.mock(ILoggingEvent.class);
		Mockito.when(event.getThrowableProxy()).thenReturn(proxy);
		
		ILoggingEventAdapter adapter = new ILoggingEventAdapter(Mockito.mock(EnvironmentDetail.class));
		Optional<Throwable> throwable = adapter.getThrowable(event);
		
		Assert.assertTrue(throwable.isPresent());
	}
	
	/**
	 * testGetLogMsg
	 */
	@Test
	public void testGetLogMsg() {
		String msg = "msg";
		StackifyError ex = Mockito.mock(StackifyError.class);
		String th = "th";
		String level = "debug";
		String srcMethod = "srcMethod";
		Integer srcLine = Integer.valueOf(14);
		
		StackTraceElement ste = new StackTraceElement("", srcMethod, "", srcLine);
		
		ILoggingEvent event = Mockito.mock(ILoggingEvent.class);
		Mockito.when(event.getFormattedMessage()).thenReturn(msg);
		Mockito.when(event.getThreadName()).thenReturn(th);
		Mockito.when(event.getLevel()).thenReturn(Level.DEBUG);
		Mockito.when(event.getCallerData()).thenReturn(new StackTraceElement[]{ste});
		
		ILoggingEventAdapter adapter = new ILoggingEventAdapter(Mockito.mock(EnvironmentDetail.class));
		LogMsg logMsg = adapter.getLogMsg(event, Optional.of(ex));
		
		Assert.assertNotNull(logMsg);
		Assert.assertEquals(msg, logMsg.getMsg());
		Assert.assertNull(logMsg.getData());
		Assert.assertEquals(ex, logMsg.getEx());		
		Assert.assertEquals(th, logMsg.getTh());		
		Assert.assertEquals(level, logMsg.getLevel());			
		Assert.assertEquals(srcMethod, logMsg.getSrcMethod());		
		Assert.assertEquals(srcLine, logMsg.getSrcLine());		
	}
	
	/**
	 * testGetStackifyError
	 */
	@Test
	public void testGetStackifyError() {
		ILoggingEvent event = Mockito.mock(ILoggingEvent.class);
		Mockito.when(event.getFormattedMessage()).thenReturn("Exception message");
		
		Throwable exception = Mockito.mock(Throwable.class);
		
		ILoggingEventAdapter adapter = new ILoggingEventAdapter(Mockito.mock(EnvironmentDetail.class));
		StackifyError error = adapter.getStackifyError(event, exception);
		
		Assert.assertNotNull(error);
	}
}

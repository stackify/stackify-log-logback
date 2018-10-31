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

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;

import com.stackify.api.common.ApiClients;
import com.stackify.api.common.ApiConfiguration;
import com.stackify.api.common.ApiConfigurations;
import com.stackify.api.common.log.LogAppender;
import com.stackify.api.common.mask.Masker;
import lombok.Getter;
import lombok.Setter;

/**
 * Logback logger appender for sending logs to Stackify.
 *
 * <p>
 * Example appender configuration:
 * <pre>
 * {@code
 * <appender name="STACKIFY" class="com.stackify.log.logback.StackifyLogAppender">
 *	   <apiKey>YOUR_API_KEY</apiKey>
 *	   <application>YOUR_APPLICATION_NAME</application>
 *	   <environment>YOUR_ENVIRONMENT</environment>
 * </appender>
 * }
 * </pre>
 *
 * <p>
 * Be sure to shutdown Logback to flush this appender of any errors and shutdown the background thread:
 * <pre>
 * LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
 * loggerContext.stop();
 * </pre>
 *
 * @author Eric Martin
 */
public class StackifyLogAppender extends AppenderBase<ILoggingEvent> {

	/**
	 * API URL (Appender configuration parameter)
	 */
	private String apiUrl = "https://api.stackify.com";

	/**
	 * API Key (Appender configuration parameter)
	 */
	private String apiKey = null;

	/**
	 * Application name (Appender configuration parameter)
	 */
	private String application = null;

	/**
	 * Environment (Appender configuration parameter)
	 */
	private String environment = null;

	@Setter
	@Getter
	private String skipJson = "false";

	@Setter
	@Getter
	private String maskEnabled;

	@Setter
	@Getter
	private String maskCreditCard;

	@Setter
	@Getter
	private String maskSSN;

	@Setter
	@Getter
	private String maskIP;

	@Setter
	@Getter
	private String maskCustom;

	/**
	 * Allow logging from com.stackify.* (Appender configuration parameter)
	 */
	@Setter
	@Getter
	private String allowComDotStackify = null;

	/**
	 * Generic log appender
	 */
	private LogAppender<ILoggingEvent> logAppender;

	/**
	 * @return the apiUrl
	 */
	public String getApiUrl() {
		return apiUrl;
	}

	/**
	 * @param apiUrl the apiUrl to set
	 */
	public void setApiUrl(String apiUrl) {
		this.apiUrl = apiUrl;
	}

	/**
	 * @return the apiKey
	 */
	public String getApiKey() {
		return apiKey;
	}

	/**
	 * @param apiKey the apiKey to set
	 */
	public void setApiKey(String apiKey) {
		this.apiKey = apiKey;
	}

	/**
	 * @return the application
	 */
	public String getApplication() {
		return application;
	}

	/**
	 * @param application the application to set
	 */
	public void setApplication(String application) {
		this.application = application;
	}

	/**
	 * @return the environment
	 */
	public String getEnvironment() {
		return environment;
	}

	/**
	 * @param environment the environment to set
	 */
	public void setEnvironment(String environment) {
		this.environment = environment;
	}

	/**
	 * @see ch.qos.logback.core.AppenderBase#start()
	 */
	@Override
	public void start() {
		super.start();

		// build the api config

		ApiConfiguration apiConfig = ApiConfigurations.fromPropertiesWithOverrides(apiUrl, apiKey, application, environment, allowComDotStackify);

		// get the client project name with version

		String clientName = ApiClients.getApiClient(StackifyLogAppender.class, "/stackify-log-logback.properties", "stackify-log-logback");

		// build the log appender

		try {

			// setup masker

			Masker masker = new Masker();
			if (Boolean.parseBoolean(maskEnabled)) {

				// set default masks
				masker.addMask(Masker.MASK_CREDITCARD);
				masker.addMask(Masker.MASK_SSN);

				if (maskCreditCard != null && !Boolean.parseBoolean(maskCreditCard)) {
					masker.removeMask(Masker.MASK_CREDITCARD);
				}

				if (maskSSN != null && !Boolean.parseBoolean(maskSSN)) {
					masker.removeMask(Masker.MASK_SSN);
				}

				if (Boolean.parseBoolean(maskIP)) {
					masker.addMask(Masker.MASK_IP);
				}

				if (maskCustom != null) {
					masker.addMask(maskCustom);
				}

			} else {
				masker.clearMasks();
			}

			this.logAppender = new LogAppender<ILoggingEvent>(
					clientName,
					new ILoggingEventAdapter(apiConfig.getEnvDetail()),
					masker,
					Boolean.parseBoolean(skipJson));
			this.logAppender.activate(apiConfig);
		} catch (Exception e) {
			addError("Exception starting the Stackify_LogBackgroundService", e);
		}
	}

	/**
	 * @see ch.qos.logback.core.AppenderBase#append(java.lang.Object)
	 */
	@Override
	protected void append(final ILoggingEvent event) {
		try {
			this.logAppender.append(event);
		} catch (Exception e) {
			addError("Exception appending event to Stackify Log Appender", e);
		}
	}

	/**
	 * @see ch.qos.logback.core.AppenderBase#stop()
	 */
	@Override
	public void stop() {
		try {
			this.logAppender.close();
		} catch (Exception e) {
			addError("Exception closing Stackify Log Appender", e);
		}

		super.stop();
	}
}

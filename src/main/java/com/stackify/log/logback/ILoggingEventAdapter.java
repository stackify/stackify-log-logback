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

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.IThrowableProxy;
import ch.qos.logback.classic.spi.ThrowableProxy;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stackify.api.EnvironmentDetail;
import com.stackify.api.LogMsg;
import com.stackify.api.StackifyError;
import com.stackify.api.WebRequestDetail;
import com.stackify.api.common.lang.Throwables;
import com.stackify.api.common.log.APMLogData;
import com.stackify.api.common.log.EventAdapter;
import com.stackify.api.common.log.ServletLogContext;
import com.stackify.api.common.log.StackTraceUtil;
import com.stackify.api.common.util.Maps;
import com.stackify.api.common.util.Preconditions;

import java.util.Date;
import java.util.Map;

/**
 * ILoggingEventAdapter
 *
 * @author Eric Martin
 */
public class ILoggingEventAdapter implements EventAdapter<ILoggingEvent> {

    /**
     * Environment detail
     */
    private final EnvironmentDetail envDetail;

    /**
     * JSON converter
     */
    private final ObjectMapper json = new ObjectMapper();

    public ILoggingEventAdapter(final EnvironmentDetail environmentDetail) {
        Preconditions.checkNotNull(environmentDetail);
        this.envDetail = environmentDetail;
    }

    @Override
    public Throwable getThrowable(final ILoggingEvent event) {

        IThrowableProxy iThrowableProxy = event.getThrowableProxy();

        if (iThrowableProxy != null) {
            if (iThrowableProxy instanceof ThrowableProxy) {
                ThrowableProxy throwableProxy = (ThrowableProxy) iThrowableProxy;
                return throwableProxy.getThrowable();
            }
        }

        return null;
    }

    @Override
    public StackifyError getStackifyError(final ILoggingEvent event, final Throwable exception) {

        StackifyError.Builder builder = StackifyError.newBuilder();
        builder.environmentDetail(envDetail);
        builder.occurredEpochMillis(new Date(event.getTimeStamp()));

        if (exception != null) {
            builder.error(Throwables.toErrorItem(event.getFormattedMessage(), exception));
        } else {
            String className = null;
            String methodName = null;
            int lineNumber = 0;

            StackTraceElement[] callerData = event.getCallerData();

            if ((callerData != null) && (0 < callerData.length)) {
                StackTraceElement locInfo = callerData[0];

                if (locInfo != null) {
                    className = locInfo.getClassName();
                    methodName = locInfo.getMethodName();
                    lineNumber = locInfo.getLineNumber();
                }
            }

            builder.error(Throwables.toErrorItem(event.getFormattedMessage(), className, methodName, lineNumber));
        }

        String user = APMLogData.isLinked() ? APMLogData.getUser() : ServletLogContext.getUser();

        if (user != null) {
            builder.userName(user);
        }

        WebRequestDetail webRequest = APMLogData.isLinked() ? APMLogData.getWebRequest() : ServletLogContext.getWebRequest();

        if (webRequest != null) {
            builder.webRequestDetail(webRequest);
        }

        builder.serverVariables(Maps.fromProperties(System.getProperties()));

        return builder.build();
    }

    @Override
    public LogMsg getLogMsg(final ILoggingEvent event, final StackifyError error) {

        LogMsg.Builder builder = LogMsg.newBuilder();

        builder.msg(event.getFormattedMessage());

        Map<String, String> props = event.getMDCPropertyMap();

        if (props != null) {
            if (!props.isEmpty()) {
                try {
                    builder.data(json.writeValueAsString(props));
                } catch (Exception e) {
                    // do nothing
                }
            }
        }

        builder.ex(error);
        builder.th(event.getThreadName());
        builder.epochMs(event.getTimeStamp());
        builder.level(event.getLevel().toString().toLowerCase());

        String transactionId = APMLogData.isLinked() ? APMLogData.getTransactionId() : ServletLogContext.getTransactionId();

        if (transactionId != null) {
            builder.transId(transactionId);
        }

        StackTraceElement stackTraceElement = StackTraceUtil.getStackTraceElement(event.getCallerData());

        if (stackTraceElement != null) {
            builder.srcMethod(stackTraceElement.getClassName() + "." + stackTraceElement.getMethodName());

            try {
                builder.srcLine(stackTraceElement.getLineNumber());
            } catch (Throwable e) {
            }
        }

        return builder.build();
    }

    @Override
    public boolean isErrorLevel(final ILoggingEvent event) {
        return event.getLevel().isGreaterOrEqual(Level.ERROR);
    }

    @Override
    public String getClassName(final ILoggingEvent event) {
        return StackTraceUtil.getClassName(event.getCallerData());
    }

}

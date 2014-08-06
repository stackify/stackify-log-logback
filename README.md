# stackify-log-logback

[![Build Status](https://travis-ci.org/stackify/stackify-log-logback.png)](https://travis-ci.org/stackify/stackify-log-logback)
[![Coverage Status](https://coveralls.io/repos/stackify/stackify-log-logback/badge.png?branch=master)](https://coveralls.io/r/stackify/stackify-log-logback?branch=master)

Logback logger appender for sending log messages and exceptions to Stackify.

Error Logging and Monitoring Overview:

http://docs.stackify.com/s/3095/m/7787/l/189767-error-logging-and-monitoring-overview

Logging Overview:

http://docs.stackify.com/s/3095/m/7787/l/226390-log-aggregation-beta

Sign Up for a Trial:

http://www.stackify.com/sign-up/

## Usage

Example appender configuration:
```xml
<appender name="STACKIFY" class="com.stackify.log.logback.StackifyLogAppender">
    <apiKey>YOUR_API_KEY</apiKey>
    <application>YOUR_APPLICATION_NAME</application>
    <environment>YOUR_ENVIRONMENT</environment>
</appender>
```

Be sure to shutdown Logback to flush this appender of any errors and shutdown the background thread:
```java
LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
loggerContext.stop();
```

## Installation

Add it as a maven dependency:
```xml
<dependency>
    <groupId>com.stackify</groupId>
    <artifactId>stackify-log-logback</artifactId>
    <version>1.0.1</version>
</dependency>
```

## License

Copyright 2014 Stackify, LLC.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

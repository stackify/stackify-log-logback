# stackify-log-logback

[![Maven Central](https://img.shields.io/maven-central/v/com.stackify/stackify-log-logback.svg)](http://mvnrepository.com/artifact/com.stackify/stackify-log-logback)
[![Build Status](https://travis-ci.org/stackify/stackify-log-logback.png)](https://travis-ci.org/stackify/stackify-log-logback)
[![Coverage Status](https://coveralls.io/repos/stackify/stackify-log-logback/badge.png?branch=master)](https://coveralls.io/r/stackify/stackify-log-logback?branch=master)

Logback logger appender for sending log messages and exceptions to Stackify.

Errors and Logs Overview:

http://docs.stackify.com/m/7787/l/189767

Sign Up for a Trial:

http://www.stackify.com/sign-up/

## Installation

Add it as a maven dependency:
```xml
<dependency>
    <groupId>com.stackify</groupId>
    <artifactId>stackify-log-logback</artifactId>
    <version>INSERT_LATEST_MAVEN_CENTRAL_VERSION</version>
    <scope>runtime</scope>
</dependency>
```

## Usage

Example appender configuration:
```xml
<appender name="STACKIFY" class="com.stackify.log.logback.StackifyLogAppender">
    <apiKey>YOUR_API_KEY</apiKey>
    <application>YOUR_APPLICATION_NAME</application>
    <environment>YOUR_ENVIRONMENT</environment>
</appender>
```

Note: *If you are logging from a device that has the stackify-agent installed, the environment setting is optional. We will use the environment associated to your device in Stackify.*

Be sure to shutdown Logback to flush this appender of any errors and shutdown the background thread:
```java
ch.qos.logback.classic.LoggerContext loggerContext = (ch.qos.logback.classic.LoggerContext) org.slf4j.LoggerFactory.getILoggerFactory();
loggerContext.stop();
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

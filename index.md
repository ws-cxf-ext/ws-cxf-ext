# Overview

WS CXF EXT is a CXF and Spring Security plugin which aims to:
* Authentificating RESTful or SOAP webservices by a HTTP set of headers (HMAC-SHA1);
* Logging webservices calling with additionnal informations like the user of a Spring Security context;
* Marshalling/unmarshalling of a set of exception (functional and technical) and Calendars.

# Installation with maven

## Maven repository

```xml
<repository>
    <id>ws-cxf-ext-mvn-repo</id>
    <url>https://raw.github.com/ws-cxf-ext/ws-cxf-ext/mvn-repo/</url>
    <snapshots>
        <enabled>true</enabled>
        <updatePolicy>always</updatePolicy>
    </snapshots>
</repository>
```

## Maven dependency

```xml
<dependency>
    <groupId>ws-cxf-ext</groupId>
    <artifactId>ws-cxf-ext</artifactId>
    <version>${ws-cxf-ext.version}</version>
</dependency>
```

# Versions

You could see the available version [here](https://github.com/ws-cxf-ext/ws-cxf-ext/releases).

# License

Apache License, Version 2.0 (the "License").

You may obtain a copy of the License at [http://www.apache.org/licenses/LICENSE-2.0](http://www.apache.org/licenses/LICENSE-2.0).

# See also

* [Wiki home](https://github.com/ws-cxf-ext/ws-cxf-ext/wiki)
* [Getting started](https://github.com/ws-cxf-ext/ws-cxf-ext/wiki/Getting-started)
* [Contributions](https://github.com/ws-cxf-ext/ws-cxf-ext/wiki/Contributions)
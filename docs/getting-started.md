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

# How to use

## Spring server configuration

### Server application properties

Note : this variables aren't mandatory (by default, the environment is "dev" and the authentification is enabled).
The names of variables are significant.

```
ws.disable.auth=false
ws.env.auth=production
```

### Declaring clients

Note : the names of the CustomBasicAuth's beans are significant.

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
    xmlns:jaxrs="http://cxf.apache.org/jaxrs" xmlns:http-conf="http://cxf.apache.org/transports/http/configuration"
    xmlns:cxf="http://cxf.apache.org/core" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xmlns:jaxws="http://cxf.apache.org/jaxws" xmlns:context="http://www.springframework.org/schema/context"
    xmlns:util="http://www.springframework.org/schema/util"
    xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd
        http://cxf.apache.org/jaxrs http://cxf.apache.org/schemas/jaxrs.xsd
        http://cxf.apache.org/transports/http/configuration http://cxf.apache.org/schemas/configuration/http-conf.xsd
        http://www.springframework.org/schema/context http://www.springframework.org/schema/context/spring-context.xsd
        http://www.springframework.org/schema/util http://www.springframework.org/schema/util/spring-util.xsd
        http://cxf.apache.org/core http://cxf.apache.org/schemas/core.xsd">

    <util:list id="listReadOnly" value-type="java.lang.String">
        <value>client2-readonly-secret</value>
        <value>client1-readandwrite-secret</value>
        <value>client3-admin</value>
    </util:list>

    <util:list id="listReadWrite" value-type="java.lang.String">
        <value>client1-readandwrite-secret</value>
        <value>client3-admin</value>
    </util:list>

    <util:list id="listAdmin" value-type="java.lang.String">
        <value>client3-admin</value>
    </util:list>
    
    <bean id="exceptionServuceWichIsAlsoOpenToClient4" class="org.ws.cxf.ext.auth.ExceptionAuth">
        <property name="pattern" value="/serviceWhichIsOpenToClient4" />
        <property name="appids">
            <list>
                <value>client1-readandwrite-secret</value>
            	<value>client2-readonly-secret</value>
                <value>client3-admin</value>
                <value>client4-onlyoneservice-secret</value>
            </list>
        </property>
    </bean>
    
    <bean id="exceptionServiceWichIsNotAuthentified" class="org.ws.cxf.ext.auth.ExceptionAuth">
        <property name="pattern" value="/serviceWichIsNotAuthentified" />
        <property name="disable" value="true" />
    </bean>

    <bean id="authGet" class="org.ws.cxf.ext.auth.CustomBasicAuth">
        <property name="method" value="GET" />
        <property name="appids" ref="listReadOnly" />

        <property name="exceptions">
            <list>
                <ref bean="exceptionServuceWichIsAlsoOpenToClient3" />
                <ref bean="exceptionServiceWichIsNotAuthentified" />
            </list>
        </property>
    </bean>

    <bean id="authPost" class="org.ws.cxf.ext.auth.CustomBasicAuth">
        <property name="method" value="POST" />
        <property name="appids" ref="listReadWrite" />
    </bean>

    <bean id="authPut" class="org.ws.cxf.ext.auth.CustomBasicAuth">
        <property name="method" value="PUT" />
        <property name="appids" ref="listReadWrite" />
    </bean>

    <bean id="authDelete" class="org.ws.cxf.ext.auth.CustomBasicAuth">
        <property name="method" value="DELETE" />
        <property name="appids" ref="listReadWrite" />
    </bean>
    
    <bean id="authAdm" class="org.ws.cxf.ext.auth.CustomBasicAuth">
        <property name="appids" ref="listAdmin" />
    </bean>

    <bean id="currentAppId" class="org.ws.cxf.ext.appid.CurrentAppId"></bean>
</beans>
```

### Importing server interceptors

```xml
<import resource="ws-cxf-ext-server-context.xml" />
```

### Attach server interceptors


Note : the names of the interceptors beans are significant.

```xml
<jaxrs:server id="restContainer" address="/">
    <jaxrs:serviceBeans>
        <ref bean="serviceWhichIsOpenToClient3" />
        <ref bean="serviceWichIsNotAuthentified" />
        <ref bean="otherService" />
     </jaxrs:serviceBeans>

     <jaxrs:extensionMappings>
         <entry key="json" value="application/json" />
     </jaxrs:extensionMappings>

    <jaxrs:providers>
        <bean class="com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider" />
        <bean class="com.fasterxml.jackson.jaxrs.xml.JacksonJaxbXMLProvider" />
        <bean class="org.ws.cxf.ext.utils.others.CalendarProvider" />
        <bean class="org.ws.cxf.ext.utils.others.ExceptionHandler" />
    </jaxrs:providers>
    <jaxrs:features>
    </jaxrs:features>
    <jaxrs:inInterceptors>
        <ref bean="authSrvIn" />
        <ref bean="logSrvIn" />
    </jaxrs:inInterceptors>
    <jaxrs:outInterceptors>
        <ref bean="authSrvOut" />
        <ref bean="logSrvOut" />
    </jaxrs:outInterceptors>
</jaxrs:server>
```

## Spring client configuration

### Client application properties

Note : the environment variable isn't mandatory (by default, the environment is "dev" and the authentification is enabled).
The names of variables are significant.

```
ws.appid.auth=client1-readandwrite-secret
ws.env.auth=production
```

### Importing client interceptors

```xml
<import resource="ws-cxf-ext-client-context.xml" />
```

### Attach client interceptors

Note : the names of the interceptors and handlers beans are significant.

```xml
<jaxrs:client id="demoMemberServiceWebClient" address="http://yourservice.com/api" serviceClass="your.service.ServiceClas">
    <jaxrs:headers>
        <entry key="Accept" value="application/json" />
    </jaxrs:headers>
    <jaxrs:providers>
        <ref bean="jaxrsJsonProvider" />
        <ref bean="jaxrsCalendarProvider" />
        <ref bean="responseExceptionHandler" />
    </jaxrs:providers>
    <jaxrs:outInterceptors>
        <ref bean="authCltOut" />
        <ref bean="logCltOut" />
    </jaxrs:outInterceptors>
    <jaxrs:inInterceptors>
        <ref bean="authCltIn" />
        <ref bean="logCltIn" />
    </jaxrs:inInterceptors>
</jaxrs:client>
```

## Use OpenFeign as client

If you want to use OpenFeign instead of CXF as Restful client library, you can use the `org.ws.cxf.ext.feign.FeignClientAuthenticationInterceptor`.

Example using a Spring bean:

```java
@Value("${ws.appid.auth}")
private String appid; // client1-readandwrite-secret for example

@Value("${ws.env.auth}")
private String env; // production for example

@Value("${api.url}")
private String url; // url is the server url as String like http://localhost:8082/api for example

// ...

MyJaxRsInterface api = new Feign.Builder().interceptors(Arrays.asList(new FeignClientAuthenticationInterceptor(url, appid, env)))
// ... encoder, decoder, chosen http client, request options with timeouts...
.target(MyJaxRsInterface.class, url);
```

## Knowing if your client is an admin application

An admin application is referenced by the `authAdm` instance of `CustomBasicAuth`.

An admin application is usefull to know if your API can return sensitive data for example.

You'll just have to inject the [`ICurrentAppId`](../src/main/java/org/ws/cxf/ext/appid/ICurrentAppId.java) bean and use the `isAdmin` method.

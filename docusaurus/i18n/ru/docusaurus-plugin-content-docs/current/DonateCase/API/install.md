---
id: install
title: Установка
sidebar_position: 2
---
# Установка
Maven
```xml
<repository>
    <id>JodexIndustries</id>
    <name>JodexIndustries Repo</name>
    <url>https://repo.jodexindustries.xyz/releases</url>
</repository>
```
```xml
<dependency>
  <groupId>com.jodexindustries.donatecase</groupId>
  <artifactId>DonateCaseAPI</artifactId>
  <version>2.2.6.2</version>
  <scope>provided</scope>
</dependency>
```
Gradle
```gradle
maven {
    name "JodexIndustries"
    url "https://repo.jodexindustries.xyz/releases"
}
```
```gradle
compileOnly("com.jodexindustries.donatecase:DonateCaseAPI:2.2.6.2")
```
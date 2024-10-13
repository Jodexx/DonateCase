---
id: install
title: Встановлення
sidebar_position: 2
---
# Встановлення
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
  <artifactId>spigot</artifactId>
  <version>2.2.6.3</version>
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
compileOnly("com.jodexindustries.donatecase:spigot:2.2.6.3")
```
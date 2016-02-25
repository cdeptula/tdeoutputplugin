Tableau Data Extract Output Plugin
===============

A big thanks to my employer [Inquidia Consulting](http://www.inquidia.com) for allowing me to open source and make this plugin available.

The Tableau Data Extract plugin for Pentaho Data Integration allows you to use Pentaho's ETL capabilities to blend data from multiple sources into a single Tableau Data Extract (tde) file.  You can then publish the TDE file to a Tableau server or use the TDE in Tableau Desktop.

A Tableau Data Extract file is a compressed file specially optimized for Tableau performance.  If you are familiar with Tableau, a TDE is what Tableau creates when you tell Tableau to perform extracts from a data source instead of connecting live.  This plugin skips that step and goes directly to the TDE file format.

System Requirements
-------------------

- Pentaho Data Integration 6.0 and above
- Tableau 9.2 and above
- Version 1.x and below are compatible with Pentaho Data Integration 5.4 and below and Tableau 9.1 and below.

Installation
------------


Due to licensing the Tableau Data Extract API is not distributed through the Pentaho Marketplace and must be separately downloaded and installed.

1. Install the Tableau Data Exract Output plugin from the Pentaho marketplace.
2. Download the Tableau SDK for Java from http://onlinehelp.tableau.com/current/api/sdk/en-us/SDK/tableau_sdk_installing.htm#downloading
3. Follow the instructions for your environment to install the Tableau SDK: http://onlinehelp.tableau.com/current/api/sdk/en-us/help.htm#SDK/tableau_sdk_using_java_eclipse.htm%3FTocPath%3D_____5
4. Copy the jar files from sdk-install-dir/Java to ${PDI_HOME}/plugins/steps/TDEOutputPlugin/lib directory.
6. Restart PDI

**Manual Install**

Due to licensing the Tableua Data Extract API is not distributed through the zip and must be separately downloaded and installed.

1. Unzip TDEOutputPlugin.zip to ${PDI_HOME}/plugins/steps/TDEOutputPlugin
2. Follow steps 2 and above from the "Using the Pentaho Marketplace" instructions

**Using with Pentaho 5.x and below**

The TDE Output Plugin version 2.0 and above is not tested with Pentaho 5.x and below.  However, it may still work if you follow these instructions.

1. Download the Apache VFS2 Jar from https://commons.apache.org/proper/commons-vfs/download_vfs.cgi
2. Copy this jar to the ${PDI_HOME}/plugins/steps/TDEOutputPlugin/lib folder

Building from Source
--------------------
This project uses ant; however, you must manually configure your classpath before building.

1. Download the "C/C++/Java" data extract API from Tableau.  Choose the 32 bit or 64 bit version based on your Java JDK.
2. Unzip the downloaded zip.
3. Edit the build.properties file.
  1. For Java 7: Set tdeclasspath to the Java directory from the downloaded zip.
  3. For PDI 6.0 and up: Set pentahoclasspath to ${PDI_HOME}/lib
  5. Set pentahoswtclasspath to ${PDI_HOME}/libswt
4. Build the plugin using "ant dist"
5. The plugin will be in the dist directory.


Tableau Data Extract Output Plugin
===============

A big thanks to my employer OpenBI (http://www.openbi.com) for allowing me to open source and make this plugin available.

The Tableau Data Extract plugin for Pentaho Data Integration allows you to use Pentaho's ETL capabilities to generate a Tableau Data Extract (tde) file.  You can then publish the TDE file to a Tableau server or use the TDE in Tableau Desktop.

A Tableau Data Extract file is a compressed file specially optimized for Tableau performance.  If you are familiar with Tableau, a TDE is what Tableau creates when you tell Tableau to perform extracts from a data source instead of connecting live.  This plugin skips that step and goes directly to the TDE file format.

System Requirements
-------------------

- Windows
- Pentaho Data Integration 4.3 and above
- Tableau 8.0, 8.1

The plugin only runs in Windows environments due to Tableau API limitations.

Installation
------------

**Using the Pentaho Marketplace**

Due to licensing concerns the Tableau Data Extract API is not distributed through the Pentaho Marketplace and must be separately downloaded and installed.

1. Install the Tableau Data Exract Output plugin from the Pentaho marketplace.
2. Download the "C/C++/Java" data extract API from Tableau.  Choose the 32 bit or 64 bit version based on your Java JDK. - http://www.tableausoftware.com/data-extract-api
3. Unzip the downloaded zip.
4. For PDI 5.0 and above - Copy the contents of the Java directory to ${PDI_HOME}/plugins/steps/TDEOutputPlugin directory.
5. For PDI 4.8 and below - Copy the contents of the Java6 directory to ${PDI_HOME}/plugins/steps/TDEOutputPlugin directory.
6. Add the bin folder to your system path.  
  1. Right click on "My Computer" and go to "Properties"  
  2. Select "Advanced system settings"
  3. Click the "Environment Variables" button.
  4. Under "System variables" select the "Path" variable and clikc "Edit"
  5. At the end of the "Variable value" add a ; and the path to the bin directory of the downloaded zip.
  6. Click OK
7. Restart PDI

**Manual Install**

Due to licensing concerns the Tableua Data Extract API is not distributed through the zip and must be separately downloaded and installed.

1. Unzip TDEOutputPlugin.zip to ${PDI_HOME}/plugins/steps/TDEOutputPlugin
2. Follow steps 2 and above from the "Using the Pentaho Marketplace" instructions
 
Building from Source
--------------------
This project uses ant; however, you must manually configure your classpath before building.

1. Download the "C/C++/Java" data extract API from Tableau.  Choose the 32 bit or 64 bit version based on your Java JDK. - http://www.tableausoftware.com/data-extract-api
2. Unzip the downloaded zip.
3. Edit the build.properties file.
  1. For Java 7: Set tdeclasspath to the Java directory from the downloaded zip.
  2. For Java 6: Set tdeclasspath to the Java6 directory from the downloaded zip.
  3. For PDI 5.0 and up: Set pentahoclasspath to ${PDI_HOME}/lib
  4. For PDI 4.5 and below: Set pentahoclasspath to ${PDI_HOME}/libext
  5. Set pentahoswtclasspath to ${PDI_HOME}/libswt
4. Build the plugin using "ant dist"
5. The plugin will be in the dist directory.

Tableau Data Extract Output Plugin
===============

A big thanks to my employer [Inquidia Consulting](http://www.inquidia.com) for allowing me to open source and make this plugin available.

The Tableau Data Extract plugin for Pentaho Data Integration allows you to use Pentaho's ETL capabilities to blend data from multiple sources into a single Tableau Data Extract (tde) file.  You can then publish the TDE file to a Tableau server or use the TDE in Tableau Desktop.

A Tableau Data Extract file is a compressed file specially optimized for Tableau performance.  If you are familiar with Tableau, a TDE is what Tableau creates when you tell Tableau to perform extracts from a data source instead of connecting live.  This plugin skips that step and goes directly to the TDE file format.

System Requirements
-------------------

- Pentaho Data Integration 6.0 and above (Plugin version 1.2.0 and above)
- Pentaho Data Integration 4.3 to 5.4 (Plugin version 1.1.0 and below)
- Tableau 8.0, 8.1

Installation
------------


Due to licensing the Tableau Data Extract API is not distributed through the Pentaho Marketplace and must be separately downloaded and installed.

1. Install the Tableau Data Exract Output plugin from the Pentaho marketplace.
2. Download the "C/C++/Java" data extract API from Tableau.  Choose the 32 bit or 64 bit version based on your Java JDK. - http://www.tableausoftware.com/data-extract-api
3. Unzip the downloaded zip.
4. For PDI 5.0 and above - Copy the contents of the lib64/dataextract/Java directory to ${PDI_HOME}/plugins/steps/TDEOutputPlugin directory.
5. For PDI 4.8 and below - Copy the contents of the lib64/dataextract/Java6 directory to ${PDI_HOME}/plugins/steps/TDEOutputPlugin directory.
6. For Windows: Add the bin folder from the downloaded API to your system path.  
7. For Linux: 
  - Set the LD_LIBRARY_PATH environment variable to ${EXTRACT_API}/lib64/dataextract (export LD_LIBRARY_PATH=...)
  - Add the ${EXTRACT_API}/bin directory to your PATH variable.  (export PATH=${PATH}:...)
8. Restart PDI

**Manual Install**

Due to licensing the Tableua Data Extract API is not distributed through the zip and must be separately downloaded and installed.

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


[![Bitdeli Badge](https://d2weczhvl823v0.cloudfront.net/cdeptula/tdeoutputplugin/trend.png)](https://bitdeli.com/free "Bitdeli Badge")


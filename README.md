Tableau Data Extract Output Plugin
===============

The Tableau Data Extract plugin for Pentaho Data Integration allows you to use Pentaho's ETL capabilities to blend data from multiple sources into a single Tableau Data Extract (tde) file.  You can then publish the TDE file to a Tableau server or use the TDE in Tableau Desktop.

A Tableau Data Extract file is a compressed file specially optimized for Tableau performance.  If you are familiar with Tableau, a TDE is what Tableau creates when you tell Tableau to perform extracts from a data source instead of connecting live.  This plugin skips that step and goes directly to the TDE file format.

System Requirements
-------------------

- Pentaho Data Integration 8.1 and above
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

Due to licensing the Tableau Data Extract API is not distributed through the zip and must be separately downloaded and installed.

1. Unzip TDEOutputPlugin.zip to ${PDI_HOME}/plugins/steps/TDEOutputPlugin
2. Follow steps 2 and above from the "Using the Pentaho Marketplace" instructions

Building from Source
--------------
This project is built using the Maven build system.  To build this plugin:

1. Download the Tableau SDK for Java from http://onlinehelp.tableau.com/current/api/sdk/en-us/SDK/tableau_sdk_installing.htm#downloading
2. Install the Tableau jars into the local Maven repository by using the commands:
 
    ``` maven
    mvn deploy:deploy-file -DgroupId=com.tableausoftware -DartifactId=jna -Dversion=10.4 -DrepositoryId=project-maven-repo -DupdateReleaseInfo=true -Dfile=${tableau_sdk_home}/Java/jna.jar -Durl=file:${plugin_home}/project-maven-repo
    mvn deploy:deploy-file -DgroupId=com.tableausoftware -DartifactId=tableaucommon -Dversion=10.4 -DrepositoryId=project-maven-repo -DupdateReleaseInfo=true -Dfile=${tableau_sdk_home}/Java/tableaucommon.jar -Durl=file:${plugin_home}/project-maven-repo
    mvn deploy:deploy-file -DgroupId=com.tableausoftware -DartifactId=tableauextract -Dversion=10.4 -DrepositoryId=project-maven-repo -DupdateReleaseInfo=true -Dfile=${tableau_sdk_home}/Java/tableauextract.jar -Durl=file:${plugin_home}/project-maven-repo
    mvn deploy:deploy-file -DgroupId=com.tableausoftware -DartifactId=tableauserver -Dversion=10.4 -DrepositoryId=project-maven-repo -DupdateReleaseInfo=true -Dfile=${tableau_sdk_home}/Java/tableauserver.jar -Durl=file:${plugin_home}/project-maven-repo
    ``` 
    
    >NOTE: Replace ${tableau_sdk_home} with the location of the downloaded sdk and ${plugin_home} with the root directory of this plugin's source.
    
3. Build the plugin with Maven

    ```maven
    mvn clean package
    ```
    
4. The plugin built plugin will now be in the target directory.
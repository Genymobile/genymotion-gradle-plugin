Genymotion Gradle Plugin
========================

`plugin` folder contains the Gradle plugin

`sample` foolder contains a sample code that uses the plugin


Testing the plugin
------------------

 - Compile the plugin

    cd plugin
    gradle uploadArchives

The result will be push into a new folder repo, at the root of this folder.

 - Then, execute the sample

    cd ../sample
    gradle genymotion

This should launch a VD, display installed VDs or any other action described on GenymotionTask.



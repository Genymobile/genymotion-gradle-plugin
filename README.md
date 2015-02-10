Gradle Plugin for Genymotion
========================

Gradle Plugin Documentation
------------------
To learn how to use the Gradle plugin for Genymotion, you can read the official documentation here.

Link coming soon...

Downloading the plugin
------------------
To download the plugin, run:

```sh
git clone https://github.com/Genymobile/genymotion-gradle-plugin.git
```

Project description
------------------
The `plugin` folder contains the Gradle plugin.

The `samples` folder contains the example project using the Gradle plugin.


Compiling the plugin
------------------

To compile the plugin, go to the root project folder and run the following command:

```sh
./gradlew :plugin:uploadArchives
```

The build result is a Maven repository. It will be pushed into a new folder: `repo`

You can now use the plugin as a simple repository. To add it to your project, you must add these lines to your build.gradle file:

```groovy
buildscript {
    repositories {
        maven { url uri('<path/to/local/repo>') }
    }
    dependencies {
        classpath 'com.genymotion:gradlePlugin:0.5'
    }
}
```

For more information on how to use the plugin, please refer to the official documentation

Coming soon...


Give a try
------------------
Execute the sample "simple" using this command:

```sh
cd samples
../gradlew :simple:genymotionLaunch
```
This starts a virtual device on your computer.



Contributing
------------------
The Gradle Plugin for Genymotion is an open source project under GPL v3 license.

Pull requests are strongly encouraged.

Before pulling, you need to run the functional tests on the project. You can run it with the command:

```sh
./gradlew :plugin:test
```

Before running the tests you need gmtool, the command line tool for Genymotion.

You also need to fill a few files like follows:

1. Copy the `plugin/res/test/default.properties.TEMPLATE` file and name it `default.properties`
2. Fill the new file with your information (genymotion username, password, license) and the path to your local Genymotion installation (genymotionPath).
3. Also, in the file `plugin/res/test/android-app/local.properties` precise the `sdk.dir` field with your Android SDK path 
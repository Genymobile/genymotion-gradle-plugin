Genymotion Gradle Plugin
========================

Gradle Plugin Documentation
------------------
If you want to learn how to use the Genymotion Gradle plugin, you can read the official documentation here.

Link coming soon...

Downloading the plugin
------------------
To download the plugin run:

```sh
git clone https://github.com/Genymobile/genymotion-gradle-plugin.git
```

Project description
------------------
`plugin` folder contains the Gradle plugin

`samples` folder contains the example project using the plugin


Compiling the plugin
------------------

To compile the plugin, got to the root project's folder and run the following command:

```sh
./gradlew :plugin:uploadArchives
```

The build result is a maven repository. It will be push into a new folder: `repo`

You can now use the plugin as a simple repository. To add it to your project file you need to add these lines to your build.gradle:

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

If you want more information about the plugin usage you will find it on the official documentation

Coming soon...


Give a try
------------------
Execute the sample "simple" with this command:

```sh
cd samples
../gradlew :simple:genymotionLaunch
```
This should launch a virtual device on your computer.



Contributing
------------------
The Genymotion Gradle Plugin is an open source project under GPL v3 license.

Pull request are widely encouraged if needed.

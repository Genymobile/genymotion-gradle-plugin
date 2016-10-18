#Gradle Plugin for Genymotion

##Gradle Plugin Documentation

Here is a short description on how to use the Gradle plugin for Genymotion into your `build.gradle`.

###Including the plugin

To use our plugin, you need first to reference our plugin’s repository. Here is what your `build.gradle` should look like:

```gradle
buildscript {
    repositories {
        jcenter()
    }
    dependencies {
        classpath 'com.genymotion:plugin:1.0'
    }
}
```

Then, apply the plugin. Insert this line into your build.gradle:

```gradle
apply plugin: "genymotion"
```

###Using the plugin
When this is done you can access to the Genymotion’s Gradle features. You can open a `genymotion` section and start defining your devices like this:

```gradle
genymotion {
    devices {
        nexus5 {
            template "Google Nexus 5 - 4.4.4 - API 19 - 1080x1920"
        }
    }
}
```

This simple example will create and launch a Nexus 5 running KitKat right before the connectedAndroidTest task.

You can also use the plugin to run your tests in Genymotion Cloud. To add a cloud device, use the following syntax:

```gradle
genymotion {
    cloudDevices {
        nexus5 {
            template "Google Nexus 5 - 4.4.4 - API 19 - 1080x1920"
        }
    }
}
```

This will create a Nexus 5 device on Genymotion Cloud.

But this plugin can do a lot more, read [the full documentation](https://www.genymotion.com/#!/developers/gradle-plugin) for an exhaustive explanation.

##Downloading the plugin's source code

To download the plugin, run:

```sh
git clone --recursive https://github.com/Genymobile/genymotion-gradle-plugin.git
```

##Project description

The `plugin` folder contains the Gradle plugin.

The `samples` folder contains the example project using the Gradle plugin.
There are two samples:
* 'simple' that showcases how to use the gradle plugin on its own.
* 'binocle' that showcases how to use the gradle plugin in the context of an Android app.


##Compiling the plugin


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
        classpath 'com.genymotion:plugin:+'
    }
}
```

##Give it a try

Execute the sample "simple" using this command:

```sh
cd samples
../gradlew :simple:genymotionLaunch
```
This starts a virtual device on your computer.


##Contributing

The Gradle Plugin for Genymotion is an open source project under GPL v3 license.

Pull requests are strongly encouraged.

You can learn more about the plugin architecture in the [architecture document](docs/architecture.md).

### Unit testing the project
Before pulling, you need to run the unit tests on the project. You can run it with the command:

```sh
./gradlew :plugin:test
```

As some tests are using the Android Gradle plugin, you need to have the Android SDK installed and the `ANDROID_HOME` environment variable set to be able to run all the tests.


### Optional integration tests

This project also contains integration tests.

Before running these tests you need gmtool, the command line tool for Genymotion. It is embedded in the Genymotion app bundle.

The integration tests rely on a `default.properties` file where all needed properties can be read.
This can be addressed in two ways. Either you create such file (ideal when working locally) or you inject all the needed properties
via command line argument (ideal for CI):

Method 1:

1. Copy the `plugin/res/test/default.properties.TEMPLATE` file and name it `default.properties`
2. Fill the new file with your information (genymotion username, password, license) and the path to your local Genymotion installation (genymotionPath).
3. Also, in the file `plugin/res/test/android-app/local.properties` precise the `sdk.dir` field with your Android SDK path 

Method 2:

Add the following command line arguments when running the tests:
```sh
./gradlew <taskName> -Pusername=<username> -Ppassword=<password> -Plicense=<license> -PgenymotionPath=<path-to-genymotion>
```

Then launch the integration tests with this command:
```sh
./gradlew :plugin:integrationTest
```

Or launch all the tests (unit & integration) with this command:
```sh
./gradlew :plugin:check
```

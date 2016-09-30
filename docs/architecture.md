# Genymotion Gradle plugin architecture

## Intro

This document describes the architecture of the Genymotion Gradle plugin. You
don't need to read this to use the Gradle plugin, but it is useful if you want
to contribute to it.

## Tasks

For each relevant Android task, the plugin injects two tasks: a "launch" task
and a "finish" task. The launch task starts the device and performs some of the
defined actions. The finish task performs the remaining actions and stops the
device.

## Main classes

GenymotionGradlePlugin is the main entry point.

GenymotionPluginExtension represents the definition of what the user sets in the
`genymotion` block: the configuration, the list of local devices and the list of
cloud devices.

### Domain Specific Language (DSL)

GenymotionConfig represents the configuration inside the `genymotion` block.

LocalVDLaunchDsl and CloudVDLaunchDsl represent the definition of a device in
the `devices` and `localDevices` block, respectively. They both inherit from
VDLaunchDsl, which contains common properties.

### Actions

GenymotionPluginExtension creates Gradle tasks to launch and finish devices.
These tasks are implemented in GenymotionLaunchTask and GenymotionFinishTask.

The task classes uses DeviceController to perform the device actions.
DeviceController has two inherited classes: LocalDeviceController and
CloudDeviceController. Most of the actions are started by DeviceController
itself, but some location-specific behaviors are implemented in the inherited
classes. For example starting and stopping a device is location-specific, since
users have more control on the way to start and stop local devices than cloud
devices.

Actions are calls to Genymotion `gmtool` binary. These calls are wrapped in the
GMTool class.

#!/usr/bin/env python3

from os import path
import platform
import re
from subprocess import check_call, call

PROJECT_DIR = path.dirname(path.dirname(path.abspath(__file__)))

"""This is a map of Gradle versions (keys) linked to Android Gradle plugin versions (list of values)"""
ANDROID_TO_GRADLE = {
    "2.2": ["1.3.1", "1.3.0", "1.2.3", "1.2.2", "1.2.1", "1.2.0", "1.1.3", "1.1.2", "1.1.1", "1.1.0", "1.0.0"],
    "2.10": ["2.1.2", "2.1.0", "2.0.0", "1.5.0"],
    "2.14.1": ["+"],
}

if platform.system() == "Windows":
    GRADLEW = "gradlew.bat"
else:
    GRADLEW = 'gradlew'

GRADLE_PATH = path.join(PROJECT_DIR, GRADLEW)


def main():
    # We want to finish with 2.2 as it does not handle the --gradle-version option on the wrapper command which is
    # mandatory to switch the gradle version of the project
    gradle_versions = sorted(ANDROID_TO_GRADLE.keys(), key=explicit_version, reverse=True)
    try:
        for gradle_version in gradle_versions:
            # Set the current project to the good Gradle version
            cmd = [GRADLE_PATH, 'wrapper', '--gradle-version', gradle_version]
            print(cmd)
            check_call(cmd)

            for android_version in ANDROID_TO_GRADLE[gradle_version]:
                # Launch the Genymotion Gradle plugin tests using the current Android Gradle plugin version
                cmd = [GRADLE_PATH, ':plugin:test', '--stacktrace', '-PincludeCat=com.genymotion.Android',
                       '-PandroidPluginVersion=' + android_version]
                print(cmd)
                check_call(cmd)
    finally:
        # We reset the gradle wrapper
        cmd = ['git', 'checkout', PROJECT_DIR + path.sep + 'gradle']
        print(cmd)
        call(cmd)


def explicit_version(version):
    x = 0
    y = 0
    z = 0

    m = re.search("(\d+)\.(\d+)\.(\d+)", version)
    if m:
        x = int(m.group(1))
        y = int(m.group(2))
        z = int(m.group(3))
    else:
        m = re.search("(\d+)\.(\d+)", version)
        x = int(m.group(1))
        y = int(m.group(2))

    return "%03d.%03d.%03d"%(x,y,z)

if __name__ == "__main__":
    main()

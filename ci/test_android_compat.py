#!/usr/bin/env python3

from os import path
import platform
from subprocess import check_call, call

PROJECT_DIR = path.dirname(path.dirname(path.abspath(__file__)))

"""This is a map of Gradle versions (keys) linked to Android Gradle plugin versions (list of values)"""
ANDROID_TO_GRADLE = {
    "2.2": ["1.3.1", "1.3.0", "1.2.3", "1.2.2", "1.2.1", "1.2.0", "1.1.3", "1.1.2", "1.1.1", "1.1.0", "1.0.0"],
    "2.10": ["+", "1.5.0"],
}

if platform.system() == "Windows":
    GRADLEW = "gradlew.bat"
else:
    GRADLEW = 'gradlew'

def main():
    gradle_versions = sorted(ANDROID_TO_GRADLE.keys()) # we want to finish with 2.2
    try:
        for gradle_version in gradle_versions:
            # Set the current project to the good Gradle version
            cmd = [PROJECT_DIR + path.sep + GRADLEW, 'wrapper', '--gradle-version', gradle_version]
            print(cmd)
            check_call(cmd)

            for android_version in ANDROID_TO_GRADLE[gradle_version]:
                # Launch the Genymotion Gradle plugin tests using the current Android Gradle plugin version
                cmd = [PROJECT_DIR + path.sep + GRADLEW, ':plugin:test', '--stacktrace', '-PincludeCat=com.genymotion.Android',
                       '-PandroidPluginVersion=' + android_version]
                print(cmd)
                check_call(cmd)
    finally:
        # We reset the gradle wrapper
        cmd = ['git', 'checkout', PROJECT_DIR + path.sep + 'gradle']
        print(cmd)
        call(cmd)


if __name__ == "__main__":
    main()

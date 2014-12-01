package test.groovy.com.genymotion

import main.groovy.com.genymotion.*
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.testfixtures.ProjectBuilder
import org.junit.After
import org.junit.Before
import org.junit.Ignore
import org.junit.Test

import static org.junit.Assert.*


class GenymotionPluginExtensionTest {

    Project project

    @Before
    public void setUp() {
    }

    @Test
    public void canConfigFromFile(){

        project = TestTools.init()
        GenymotionConfig config = GMTool.getConfig(true)

        project.genymotion.config.fromFile = "res/test/config.properties"

        //we set the config file
        project.tasks.genymotionLaunch.exec()

        assertEquals("statistics not loaded from file",           false,          project.genymotion.config.statistics)
        assertEquals("username not loaded from file",             "testName",     project.genymotion.config.username)
        assertEquals("password not loaded from file",             "testPWD",      project.genymotion.config.password)
        assertEquals("store_credentials not loaded from file",    true,          project.genymotion.config.store_credentials)
        assertEquals("license not loaded from file",              "testLicense",  project.genymotion.config.license)
        assertEquals("proxy not loaded from file",                true,           project.genymotion.config.proxy)
        assertEquals("proxy_address not loaded from file",        "testAddress",  project.genymotion.config.proxy_address)
        assertEquals("proxy not loaded from file",                true,           project.genymotion.config.proxy)
        assertEquals("proxy_port not loaded from file",           12345,          project.genymotion.config.proxy_port)
        assertEquals("proxy_auth not loaded from file",           true,           project.genymotion.config.proxy_auth)
        assertEquals("proxy_username not loaded from file",       "testUsername", project.genymotion.config.proxy_username)
        assertEquals("proxy_password not loaded from file",       "testPWD",      project.genymotion.config.proxy_password)
        assertEquals("virtual_device_path not loaded from file",  "testPath",     project.genymotion.config.virtual_device_path)
        assertEquals("sdk_path not loaded from file",             "testPath",     project.genymotion.config.sdk_path)
        assertEquals("use_custom_sdk not loaded from file",       true,           project.genymotion.config.use_custom_sdk)
        assertEquals("screen_capture_path not loaded from file",  "testPath",     project.genymotion.config.screen_capture_path)
        assertEquals("taskLaunch not loaded from file",           "testTask",     project.genymotion.config.taskLaunch)
        assertEquals("automaticLaunch not loaded from file",      true,           project.genymotion.config.automaticLaunch)
        assertEquals("processTimeout not loaded from file",       500000,         project.genymotion.config.processTimeout)
        assertEquals("verbose not loaded from file",              true,           project.genymotion.config.verbose)
        assertEquals("persist not loaded from file",              true,           project.genymotion.config.persist)
        assertEquals("abortOnError not loaded from file",         false,          project.genymotion.config.abortOnError)

        //we set the last config back
        GMTool.setConfig(config, true)

        //ENTER HERE the path to a properties file containing good credential (username, password & license)
        String path = "/home/eyal/genymotion/gradle-plugin/junit/config.properties"

        File f = new File(path)
        assertTrue("Config file does not exists to restore good a configuration. Set the path on the source code to continue correctly the tests", f.exists())

        project.genymotion.config.fromFile = path
        project.genymotion.config.persist = true

        //we set the config file
        project.genymotion.applyConfigFromFile()
        GMTool.setConfig(config, true)
    }

    @Test
    public void canInjectToCustomTask(){

        project = TestTools.init()

        String taskName = "dummy"
        project.task(taskName) << {}

        project.genymotion.config.taskLaunch = taskName
        //we inject the genymotion task on the task hierarchy
        project.genymotion.injectTasks()

        def task = project.tasks.getByName(taskName) //throw exception if task not found
        def finishTask = project.tasks.getByName(GenymotionGradlePlugin.TASK_FINISH) //throw exception if task not found

        assertTrue("Launch task not injected", task.dependsOn.contains(GenymotionGradlePlugin.TASK_LAUNCH))
        assertTrue("Finish task not injected", task.finalizedBy.getDependencies().contains(finishTask))
    }

    @Test
    public void canInjectToCustomTasks(){

        project = TestTools.init()

        def tasks = []
        3.times {
            String taskName = "dummy$it"
            project.task(taskName) << {}
            tasks.add(taskName)
        }

        project.genymotion.config.taskLaunch = tasks

        //we inject the genymotion task on the task hierarchy
        project.genymotion.injectTasks()

        tasks.each {
            def task = project.tasks.getByName(it) //throw exception if task not found
            def finishTask = project.tasks.getByName(GenymotionGradlePlugin.TASK_FINISH) //throw exception if task not found

            assertTrue("Launch task not injected", task.dependsOn.contains(GenymotionGradlePlugin.TASK_LAUNCH))
            assertTrue("Finish task not injected", task.finalizedBy.getDependencies().contains(finishTask))
        }

    }

    @Test
    public void canInjectToDefaultAndroidTask(){

        project = getAndroidProject()
        project.genymotion.config.genymotionPath = TestTools.GENYMOTION_PATH
        project.genymotion.config.verbose = true
        project.evaluate() //internal method but: "... it is actually an internal method and is therefore potentially subject to change in future releases. There will be a supported mechanism for doing this kind of thing in the near future." http://gradle.1045684.n5.nabble.com/why-doesn-t-gradle-project-afterEvaluate-execute-in-my-unit-test-td4512335.html

        String taskName = AndroidPluginTools.DEFAULT_ANDROID_TASK

        def task = project.tasks.getByName(taskName) //throw exception if task not found
        def finishTask = project.tasks.getByName(GenymotionGradlePlugin.TASK_FINISH) //throw exception if task not found

        assertTrue("Launch task not injected", task.dependsOn.contains(GenymotionGradlePlugin.TASK_LAUNCH))
        assertTrue("Finish task not injected", task.finalizedBy.getDependencies().contains(finishTask))
    }

    @Test
    public void canInjectToVariants(){

        project = getAndroidProject()
        project.android.productFlavors{
            flavor1
            flavor2
        }
        project.evaluate() //internal method but: "... it is actually an internal method and is therefore potentially subject to change in future releases. There will be a supported mechanism for doing this kind of thing in the near future." http://gradle.1045684.n5.nabble.com/why-doesn-t-gradle-project-afterEvaluate-execute-in-my-unit-test-td4512335.html

        String taskName = AndroidPluginTools.DEFAULT_ANDROID_TASK

        project.android.productFlavors.all { flavor ->
            String flavorTaskName = AndroidPluginTools.getFlavorTaskName(flavor.name)
            def task = project.tasks.getByName(flavorTaskName) //throw exception if task not found

            Task launchTask = project.tasks.findByName(AndroidPluginTools.getFlavorLaunchTask(flavor.name))
            Task endTask = project.tasks.findByName(AndroidPluginTools.getFlavorEndTask(flavor.name))

            assertTrue("Launch task not injected in flavor $flavor.name", task.dependsOn.contains(launchTask))
            assertTrue("Finish task not injected in flavor $flavor.name", task.finalizedBy.getDependencies().contains(endTask))
        }
    }

    private static Project getAndroidProject() {
        Project project = ProjectBuilder.builder().withProjectDir(new File("res/test/android-app")).build();

        project.apply plugin: 'com.android.application'
        project.apply plugin: 'genymotion'

        project.android {
            compileSdkVersion 21
        }

        project.afterEvaluate {
            println "TASKS AFTER "+project.tasks
        }
//        project.beforeEvaluate {
//            println "TASKS BEFORE w"+project.tasks
//        }

        return project
    }

    @After
    public void finishTest(){
    }
}
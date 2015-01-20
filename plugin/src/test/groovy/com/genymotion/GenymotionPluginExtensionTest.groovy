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
    public void canConfigFromLocalProperties(){

        project = getAndroidProject()
        project.evaluate() //internal method but: "... it is actually an internal method and is therefore potentially subject to change in future releases. There will be a supported mechanism for doing this kind of thing in the near future." http://gradle.1045684.n5.nabble.com/why-doesn-t-gradle-project-afterEvaluate-execute-in-my-unit-test-td4512335.html
        project.genymotion.processConfiguration()

        assertEquals("genymotion.username is not catched from local.properties",    "user",     project.genymotion.config.username)
        assertEquals("genymotion.password is not catched from local.properties",    "password", project.genymotion.config.password)
        assertEquals("genymotion.statistics is not catched from local.properties",  true,       project.genymotion.config.statistics)
        assertEquals("genymotion.proxyPort is not catched from local.properties",   100,        project.genymotion.config.proxyPort)
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

    @Test
    public void canGetGenymotionDevices(){

        project = TestTools.init()

        project.genymotion.devices {
            "default" {}
            "both" {
                productFlavors "flavor1", "flavor2"
            }
            "product1" {
                productFlavors "flavor1"
            }
            "product2" {
                productFlavors "flavor2"
            }
        }

        //IMPORTANT: Tab needs to be in alphabetical order
        assertEquals(["both", "default", "product1", "product2"], project.genymotion.getDevices()*.name)
        assertEquals(["both", "default", "product1"], project.genymotion.getDevices("flavor1")*.name)
        assertEquals(["both", "default", "product2"], project.genymotion.getDevices("flavor2")*.name)
        assertEquals(["default"], project.genymotion.getDevices("toto")*.name)
    }


    private static Project getAndroidProject() {
        Project project = ProjectBuilder.builder().withProjectDir(new File("res/test/android-app")).build();

        project.apply plugin: 'com.android.application'
        project.apply plugin: 'genymotion'

        project.android {
            compileSdkVersion 21
        }
        project.genymotion.config.genymotionPath = TestTools.getDefaultConfig().genymotionPath

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
        TestTools.cleanAfterTests()
    }
}

package com.genymotion

import org.junit.experimental.categories.Categories
import org.junit.experimental.categories.Categories.IncludeCategory
import org.junit.runner.RunWith
import org.junit.runners.Suite

public interface Android {}

public interface GMTool {}

@RunWith(Categories)
@IncludeCategory(Android)
@Suite.SuiteClasses(GenymotionPluginExtensionTest)
public class AndroidTestSuite {}

package com.kastik.tests

import org.junit.platform.suite.api.IncludeEngines
import org.junit.platform.suite.api.SelectPackages
import org.junit.platform.suite.api.Suite

@Suite
@IncludeEngines("junit-jupiter") // runs kotlin.test on JUnit 5
@SelectPackages(
    "com.kastik.tests.domain",
    "com.kastik.tests.data"
)
class AllTestsSuite
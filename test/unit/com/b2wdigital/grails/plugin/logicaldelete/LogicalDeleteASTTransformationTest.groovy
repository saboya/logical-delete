package com.b2wdigital.grails.plugin.logicaldelete

import grails.test.GrailsUnitTestCase
import org.codehaus.groovy.control.CompilePhase
import org.codehaus.groovy.tools.ast.TransformTestHelper

class LogicalDeleteASTTransformationTest extends GrailsUnitTestCase {

    void test() {
        def file = new File("test/unit/com/b2wdigital/grails/plugin/logicaldelete/LogicalDeleteTest.groovy")
        assert file.exists()
        def invoker = new TransformTestHelper(new LogicalDeleteASTTRansformation(), CompilePhase.CANONICALIZATION)
        def clazz = invoker.parse(file)
        def test = clazz.newInstance()
        test.deleted = true
        assert test.deleted
    }

}

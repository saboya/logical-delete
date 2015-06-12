package com.b2wdigital.grails.plugin.gormlogicaldelete

import grails.test.GrailsUnitTestCase
import org.codehaus.groovy.control.CompilePhase
import org.codehaus.groovy.tools.ast.TransformTestHelper

class GormLogicalDeleteASTTransformationTest extends GrailsUnitTestCase {

    void test() {
        def file = new File("grails-app/domain/com/b2wdigital/grails/plugin/gormlogicaldelete/TestDomain.groovy")
        assert file.exists()
        def invoker = new TransformTestHelper(new GormLogicalDeleteASTTRansformation(), CompilePhase.CANONICALIZATION)
        Class<TestDomain> clazz = invoker.parse(file)
        def test = clazz.newInstance()
        test.deleted = true
        assert test.deleted
    }

}

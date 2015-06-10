package com.b2wdigital.grails.plugin.logicaldelete

import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import spock.lang.Specification

/**
 * See the API for {@link grails.test.mixin.services.ServiceUnitTestMixin} for usage instructions
 */
@TestFor(TestService)
@Mock(TestDomain)
class TestServiceSpec extends Specification {

    def setup() {
    }

    def cleanup() {
    }

    void testSaveTestDomain() {
        assert TestDomain.list().size(), 0
        new TestService().saveProduct("QWER-0")
    }

    void testDeleteTestDomain() {

    }
}

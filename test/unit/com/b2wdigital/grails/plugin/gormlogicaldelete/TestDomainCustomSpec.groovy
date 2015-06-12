package com.b2wdigital.grails.plugin.gormlogicaldelete

import grails.test.mixin.TestFor
import spock.lang.Specification

/**
 * See the API for {@link grails.test.mixin.support.GrailsUnitTestMixin} for usage instructions
 */
@TestFor(TestDomainCustom)
class TestDomainCustomSpec extends Specification {

    def setup() {
        DomainClassEnhancer.enhance(grailsApplication.domainClasses)
        grailsApplication.mainContext.addApplicationListener(PreQueryListener.instance)

        3.times {
            TestDomainCustom.findOrSaveByName("name" + it)
        }
    }

    void "custom property domain"() {
        given: "I have non-deleted domains"

        when: "I delete one domain"
        TestDomainCustom.first().delete()

        then: "I should have X non-deleted domains, and X+1 domains overall"
        TestDomainCustom.findAll().size() + 1 == withDeletedTestDomainCustomCount()
    }

    private int withDeletedTestDomainCustomCount() {
        def count
        TestDomainCustom.withDeleted {
            count = TestDomainCustom.findAll().size()
        }
        return count
    }
}

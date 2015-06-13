package com.b2wdigital.grails.plugin.gormlogicaldelete

import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import spock.lang.Specification

@TestFor(TestDomain)
class TestDomainSpec extends Specification {

    def setup() {
        DomainClassEnhancer.enhance(grailsApplication.domainClasses)
        grailsApplication.mainContext.addApplicationListener(PreQueryListener.instance)

        3.times {
            TestDomain.findOrSaveByName("name" + it)
        }
    }

    void "saving default annotation domain and checking default deleted property state"() {
        given: "I have 3 non-deleted domains"

        when: "Save on a new domain is called"
            TestDomain.findOrSaveByName("test name")

        then: "I should have 4 non-deleted domains"
             TestDomain.list().size() == 4
            !TestDomain.findByName("test name").deleted == !false
    }

    void "logically deleting default annotation domain"() {
        given: "I have X non-deleted domains"

        when: "I delete one domain"
            TestDomain.first().delete()

        then: "I should have X-1 non-deleted domains, and X domains overall"
            TestDomain.findAll().size()+1 == withDeletedTestDomainCount()
    }

    void "logically deleting default annotation domain with extra parameter"() {
        given: "I have X non-deleted domains"

        when: "I delete one domain"
        TestDomain.first().delete(flush: true)

        then: "I should have X-1 non-deleted domains, and X domains overall"
        TestDomain.findAll().size()+1 == withDeletedTestDomainCount()
    }

    void "phisically deleting default annotation domain"() {
        given: "I have X non-deleted domains"

        when: "I delete one domain"
            TestDomain.first().delete(logicalDelete: false)

        then: "I should have X-1 non-deleted domains, and X-1 domains overall"
            TestDomain.findAll().size() == withDeletedTestDomainCount()
    }

    void "phisically deleting default annotation domain passing extra parameters"() {
        given: "I have X non-deleted domains"

        when: "I delete one domain"
            TestDomain.first().delete(logicalDelete: false, flush: true)

        then: "I should have X-1 non-deleted domains, and X-1 domains overall"
            TestDomain.findAll().size() == withDeletedTestDomainCount()
    }

    private int withDeletedTestDomainCount() {
        def count
        TestDomain.withDeleted {
             count = TestDomain.findAll().size()
        }
        return count
    }
}

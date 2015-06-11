package com.b2wdigital.grails.plugin.logicaldelete

import com.nanlabs.grails.plugin.logicaldelete.LogicalDeleteDomainClassEnhancer
import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import spock.lang.Ignore
import spock.lang.Specification

/**
 * See the API for {@link grails.test.mixin.services.ServiceUnitTestMixin} for usage instructions
 */
@TestFor(TestService)
@Mock(TestDomain)
class TestServiceSpec extends Specification {

    def setup() {
        LogicalDeleteDomainClassEnhancer.enhance(grailsApplication.domainClasses)
    }

    void "saving annotated domain"() {
        given: "I have 3 non-deleted domains"
            createTestDomains(3)

        when: "Save on a new domain is called"
            service.saveTestDomain("test name")

        then: "I should have 4 non-deleted domains"
            assert TestDomain.list().size(), 4
            assert !TestDomain.findByName("test name").deleted, !false
    }

    void "deleting annotated domain"() {
        given: "I have non-deleted domains"
            createTestDomains(3)

        when: "I delete one domain"
            service.deleteTestDomain(TestDomain.first())

        then: "I should have X non-deleted domains, and X+1 domains overall"
            assert TestDomain.findAll().size()+1, withDeletedCount()
    }

    private int withDeletedCount() {
        def count
        TestDomain.withDeleted {
             count = TestDomain.findAll().size()
        }
        return count
    }

    private void createTestDomains(int qty) {
        qty.times {
            new TestDomain(name: "name " + it).save()
        }
    }
}

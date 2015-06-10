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
@Ignore
class TestServiceSpec extends Specification {

    def setup() {
        LogicalDeleteDomainClassEnhancer.enhance(grailsApplication.domainClasses)
    }

    void "saving annotated domain"() {
        given: "nothing"

        when: "Save is called"
            service.saveTestDomain("test name")

        then:
            assert TestDomain.list().size(), 1
            assert TestDomain.find().deleted, false
    }

    void "deleting annotated domain"() {
        given:
            List<TestDomain> testDomains = createTestDomains()

        when:
            service.deleteTestDomain(testDomains.first())

        then:
            assert TestDomain.findAll().size(), 2
            TestDomain.withDeleted {
                assert TestDomain.findAll().size(), 3
            }
    }

    private def createTestDomains() {
        3.times {
            new TestDomain(name: "name " + it).save()
        }
    }
}

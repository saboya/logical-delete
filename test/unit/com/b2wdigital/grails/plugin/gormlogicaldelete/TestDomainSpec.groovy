package com.b2wdigital.grails.plugin.gormlogicaldelete

import org.springframework.beans.factory.config.MapFactoryBean
import grails.test.mixin.TestFor
import spock.lang.Specification

@TestFor(TestDomain)
class TestDomainSpec extends Specification {

    def setup() {
        defineBeans {
            logicalDeleteDomains(MapFactoryBean) { bean ->
                sourceMap = grailsApplication.domainClasses*.clazz.findAll{ it.isAnnotationPresent(GormLogicalDelete) }.collectEntries { clazz ->
                    [(clazz): [
                            property    : clazz.getAnnotation(GormLogicalDelete).property(),
                            deletedState: clazz.getAnnotation(GormLogicalDelete).deletedState()
                    ]]
                }
            }
            logicalDeletePreQueryListener(PreQueryListener) { bean ->
                logicalDeleteDomains = ref('logicalDeleteDomains') // Needed for Grails < 2.3.5
            }
        }
        DomainClassEnhancer.enhance(mainContext.getBean('logicalDeleteDomains').keySet())
        mainContext.addApplicationListener(mainContext.getBean("logicalDeletePreQueryListener"))

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

package com.b2wdigital.grails.plugin.gormlogicaldelete

import org.springframework.beans.factory.config.MapFactoryBean
import grails.test.mixin.TestFor
import spock.lang.Specification

@TestFor(TestDomainCustom)
class TestDomainCustomSpec extends Specification {

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
            TestDomainCustom.findOrSaveByName("name" + it)
        }
    }

    void "saving custom annotation domain and checking default deleted property state"() {
        given: "I have 3 non-deleted domains"

        when: "Save on a new domain is called"
            TestDomainCustom.findOrSaveByName("test name")

        then: "I should have 4 non-deleted domains"
            TestDomainCustom.list().size() == 4
            TestDomainCustom.findByName("test name").differentPropertyTest == true
    }

    void "logically deleting custom annotation domain"() {
        given: "I have non-deleted domains"

        when: "I delete one domain"
            TestDomainCustom.first().delete()

        then: "I should have X non-deleted domains, and X+1 domains overall"
            TestDomainCustom.findAll().size() + 1 == withDeletedTestDomainCustomCount()
    }

    void "logically deleting custom annotation domain with extra parameter"() {
        given: "I have non-deleted domains"

        when: "I delete one domain"
        TestDomainCustom.first().delete(flush: true)

        then: "I should have X non-deleted domains, and X+1 domains overall"
        TestDomainCustom.findAll().size() + 1 == withDeletedTestDomainCustomCount()
    }

    void "phisically deleting custom annotation domain"() {
        given: "I have X non-deleted domains"

        when: "I delete one domain"
            TestDomainCustom.first().delete(logicalDelete: false)

        then: "I should have X-1 non-deleted domains, and X-1 domains overall"
            TestDomainCustom.findAll().size() == withDeletedTestDomainCustomCount()
    }

    void "phisically deleting custom annotation domain with extra parameter"() {
        given: "I have X non-deleted domains"

        when: "I delete one domain"
        TestDomainCustom.first().delete(logicalDelete: false, flush:true)

        then: "I should have X-1 non-deleted domains, and X-1 domains overall"
        TestDomainCustom.findAll().size() == withDeletedTestDomainCustomCount()
    }

    private int withDeletedTestDomainCustomCount() {
        def count
        TestDomainCustom.withDeleted {
            count = TestDomainCustom.findAll().size()
        }
        return count
    }
}

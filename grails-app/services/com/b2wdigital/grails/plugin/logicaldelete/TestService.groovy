package com.b2wdigital.grails.plugin.logicaldelete

import grails.transaction.Transactional

@Transactional
class TestService {

    def saveTestDomain(String name) {
        def testDomain = new TestDomain()
        testDomain.name = name
        testDomain.save()
    }

    def deleteTestDomain(TestDomain instance) {
        instance.delete()
    }

    def saveTestDomainCustom(String name) {
        def instance = new TestDomainCustom()
        instance.name = name
        instance.save()
    }

    def deleteTestDomainCustom(TestDomainCustom instance) {
        instance.delete()
    }
}

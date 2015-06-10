package com.b2wdigital.grails.plugin.logicaldelete

import grails.transaction.Transactional

@Transactional
class TestService {

    def saveTestDomain(String name) {
        def testDomain = new TestDomain()
        testDomain.name = name
        testDomain.save()
    }

    def deleteTestDomain(TestDomain testDomain) {
        testDomain.delete()
    }
}

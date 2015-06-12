package com.b2wdigital.grails.plugin.logicaldelete

@LogicalDelete(property = "differentPropertyTest",deletedState = false)
class TestDomainCustom {
    String name

    static constraints = {
    }
}

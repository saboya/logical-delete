package com.b2wdigital.grails.plugin.logicaldelete

@LogicalDelete(property = "differentPropertyTest",deletedStateValue = false)
class TestDomainCustom {
    String name

    static constraints = {
    }
}

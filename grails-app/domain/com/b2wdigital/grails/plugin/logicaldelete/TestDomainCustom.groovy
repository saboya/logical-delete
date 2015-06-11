package com.b2wdigital.grails.plugin.logicaldelete

@LogicalDelete(property = "differentPropertyTest")
class TestDomainCustom {
    String name

    static constraints = {
    }
}

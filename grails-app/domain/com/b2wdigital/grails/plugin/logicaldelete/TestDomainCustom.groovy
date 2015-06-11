package com.b2wdigital.grails.plugin.logicaldelete

import com.nanlabs.grails.plugin.logicaldelete.LogicalDelete

@LogicalDelete(property = "differentPropertyTest")
class TestDomainCustom {
    String name

    static constraints = {
    }
}

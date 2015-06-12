package com.b2wdigital.grails.plugin.gormlogicaldelete

@GormLogicalDelete(property = "differentPropertyTest",deletedState = false)
class TestDomainCustom {
    String name

    static constraints = {
    }
}

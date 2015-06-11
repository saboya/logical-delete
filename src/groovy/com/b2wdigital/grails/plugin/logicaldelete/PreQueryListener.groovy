package com.b2wdigital.grails.plugin.logicaldelete

import org.grails.datastore.mapping.query.event.PreQueryEvent
import org.springframework.context.ApplicationListener

class PreQueryListener implements ApplicationListener<PreQueryEvent> {

    @Override
    void onApplicationEvent(PreQueryEvent event) {
        def domainClass = event.query.entity.javaClass
        if(LogicalDeleteDomainClass.isAssignableFrom(domainClass)) {
            if(!event.query.session.getSessionProperty(LogicalDeleteDomainClassEnhancer.PHYSICAL_SESSION)) {
                event.query.eq(domainClass.deletedStateProperty,!domainClass.deletedStateValue)
            }
        }
    }
}
package com.b2wdigital.grails.plugin.logicaldelete

import org.grails.datastore.mapping.query.event.PreQueryEvent
import org.springframework.context.ApplicationListener

class ListenerService implements ApplicationListener<PreQueryEvent> {
    static transactional = false

    @Override
    void onApplicationEvent(PreQueryEvent event) {
        if(LogicalDeleteDomainClass.isAssignableFrom(event.query.entity.javaClass)) {
            if(!event.query.session.getSessionProperty(LogicalDeleteDomainClassEnhancer.PHYSICAL_PARAM)) {
                event.query.eq(event.query.entity.javaClass.deletedStateProperty,false)
            }
        }
    }
}
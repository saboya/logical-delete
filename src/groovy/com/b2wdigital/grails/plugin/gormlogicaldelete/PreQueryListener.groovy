package com.b2wdigital.grails.plugin.gormlogicaldelete

import org.grails.datastore.mapping.query.event.PreQueryEvent
import org.springframework.context.ApplicationListener
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component
import javax.annotation.Resource

@Component("logicalDeletePreQueryListener")
@Scope("singleton")
class PreQueryListener implements ApplicationListener<PreQueryEvent> {
    @Resource
    def logicalDeleteDomains

    @Override
    void onApplicationEvent(PreQueryEvent event) {
        def domainClass = event.query.entity.javaClass
        if(logicalDeleteDomains[domainClass]) {
            if(!event.query.session.getSessionProperty(DomainClassEnhancer.PHYSICAL_SESSION)) {
                event.query.eq(
                        logicalDeleteDomains[domainClass].property,
                        !logicalDeleteDomains[domainClass].deletedState
                )
            }
        }
    }
}
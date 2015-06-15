import com.b2wdigital.grails.plugin.gormlogicaldelete.DomainClassEnhancer
import com.b2wdigital.grails.plugin.gormlogicaldelete.PreQueryListener
import com.b2wdigital.grails.plugin.gormlogicaldelete.GormLogicalDelete
import org.springframework.beans.factory.config.MapFactoryBean

class GormLogicalDeleteGrailsPlugin {
    def version = "1.0"
    def grailsVersion = "2.3.0 > *"
    def title = "Gorm Logical Delete Plugin"
    def author = "Rodrigo Saboya"
    def authorEmail = "rodrigo.saboya@b2wdigital.com.br"
    def description = '''\
Allows you to do a logical deletion of domain classes

This is a fork based on Nanlabs's Logical Delete plugin:
https://grails.org/plugin/logical-delete

The original plugin relied on Hibernate filters and HawkEventing. This one
aims to implement logical deletion using GORM only. It also adds customizable
property name and deleted state value.
'''
    def documentation = "https://github.com/saboya/gorm-logical-delete"

    def license = "APACHE"
    def organization = [name: "B2W Digital", url: "http://www.b2wdigital.com/"]
    def developers = [
            [name: "Ezequiel Parada", email: "ezequiel@nan-labs.com"]
    ]
    def issueManagement = [system: 'GITHUB', url: 'https://github.com/saboya/gorm-logical-delete/issues']
    def scm = [url: 'https://github.com/saboya/gorm-logical-delete']

    def pluginExcludes = [
            "grails-app/domain/**"
    ]

    def loadAfter = ['domainClass']


    def doWithSpring = {
        beans {
            logicalDeleteDomains(MapFactoryBean) { bean ->
                sourceMap = application.domainClasses*.clazz.findAll{ it.isAnnotationPresent(GormLogicalDelete) }.collectEntries { clazz ->
                    [(clazz): [
                            property    : clazz.getAnnotation(GormLogicalDelete).property(),
                            deletedState: clazz.getAnnotation(GormLogicalDelete).deletedState()
                    ]]
                }
            }
            logicalDeletePreQueryListener(PreQueryListener) { bean ->
                logicalDeleteDomains = ref('logicalDeleteDomains') // Needed for Grails < 2.3.5
            }
        }
    }

    def doWithDynamicMethods = { ctx ->
        DomainClassEnhancer.enhance(ctx.getBean('logicalDeleteDomains').keySet())
    }
}

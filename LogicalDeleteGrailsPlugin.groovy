import com.b2wdigital.grails.plugin.logicaldelete.LogicalDeleteDomainClassEnhancer
import com.b2wdigital.grails.plugin.logicaldelete.PreQueryListener

class LogicalDeleteGrailsPlugin {
    def version = "0.1"
    def grailsVersion = "2.4.4 > *"
    def title = "Logical Delete Plugin"
    def author = "Rodrigo Saboya"
    def authorEmail = "rodrigo.saboya@b2wdigital.com.br"
    def description = '''\
Allows you to do a logical deletion of domain classes

This is a fork based on Ezequiel Parada's original Logical Delete plugin:
https://grails.org/plugin/logical-delete

The original plugin relied on Hibernate filters and HawkEventing. This one
aims to implement logical deletion using GORM only.
'''
    def documentation = "https://github.com/saboya/logical-delete"

    def license = "APACHE"
    def organization = [name: "B2W Digital", url: "http://www.b2wdigital.com/"]
    def developers = [
            [name: "Ezequiel Parada", email: "ezequiel@nan-labs.com"]
    ]
    def issueManagement = [system: 'GITHUB', url: 'https://github.com/saboya/logical-delete/issues']
    def scm = [url: 'https://github.com/saboya/logical-delete']

    def pluginExcludes = [
            "grails-app/domain/**",
            "grails-app/services/TestService"
    ]

    def loadAfter = ['controllers', 'domainClass']

    def doWithDynamicMethods = { ctx ->
        ctx.addApplicationListener(new PreQueryListener())
        LogicalDeleteDomainClassEnhancer.enhance(application.domainClasses)
    }
}

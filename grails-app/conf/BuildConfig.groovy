grails.project.work.dir = 'target'
grails.project.class.dir = "target/classes"
grails.project.test.class.dir = "target/test-classes"
grails.project.test.reports.dir = "target/test-reports"

grails.project.dependency.resolver = "maven" // or ivy
grails.project.dependency.resolution = {

    inherits 'global'
    log 'warn'

    repositories {
        grailsCentral()
        mavenLocal()
        mavenCentral()
    }

    plugins {
        build ':coveralls:0.1.3', ':release:3.1.1', ':rest-client-builder:2.1.1', {
          export = false
        }
        test(':code-coverage:2.0.3-3') {
            export = false
        }
    }
}
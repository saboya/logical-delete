package com.b2wdigital.grails.plugin.gormlogicaldelete;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.codehaus.groovy.transform.GroovyASTTransformationClass;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@GroovyASTTransformationClass({"com.b2wdigital.grails.plugin.gormlogicaldelete.GormLogicalDeleteASTTRansformation"})
public @interface GormLogicalDelete {
    String property() default "deleted";
    boolean deletedState() default true;
}

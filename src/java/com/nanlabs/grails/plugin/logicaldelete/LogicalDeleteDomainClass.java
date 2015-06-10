package com.nanlabs.grails.plugin.logicaldelete;

public interface LogicalDeleteDomainClass {

    boolean getDeleted();

    void setDeleted(boolean deleted);
}

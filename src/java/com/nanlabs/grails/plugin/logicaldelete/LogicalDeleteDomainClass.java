package com.nanlabs.grails.plugin.logicaldelete;

public interface LogicalDeleteDomainClass {

    boolean getDeletedState();

    void setLogicalDeleteState(boolean newValue);
}

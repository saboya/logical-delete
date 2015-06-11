package com.nanlabs.grails.plugin.logicaldelete;

public interface LogicalDeleteDomainClass {

    boolean getDeletedState();

    void setDeletedState(boolean newValue);
}

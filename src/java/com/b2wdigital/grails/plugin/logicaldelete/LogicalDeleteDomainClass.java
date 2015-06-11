package com.b2wdigital.grails.plugin.logicaldelete;

public interface LogicalDeleteDomainClass {

    boolean getDeletedState();

    void setLogicalDeleteState(boolean newValue);
}

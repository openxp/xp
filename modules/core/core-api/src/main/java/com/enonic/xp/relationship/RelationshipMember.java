package com.enonic.xp.relationship;

public interface RelationshipMember<T>
{
    T getId();

    boolean isTypeId();

    boolean isTypePath();

}

package com.enonic.xp.repo.impl.relationship;

public interface RelationshipMember<T>
{
    RelationshipId getId();

    T getSource();

    T getTarget();
}

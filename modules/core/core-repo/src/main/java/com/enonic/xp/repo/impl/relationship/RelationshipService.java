package com.enonic.xp.repo.impl.relationship;

import com.enonic.xp.node.NodeId;

public interface RelationshipService
{

    public IdRelationship create( final NodeId source, final NodeId target );

    public IdRelationship delete( final RelationshipId id );

    public Relationships find( final RelationshipQuery query );

    public IdRelationship get( final RelationshipId id );

    public Relationships getTargets( final NodeId source );

    public Relationships getSources( final NodeId target );

}

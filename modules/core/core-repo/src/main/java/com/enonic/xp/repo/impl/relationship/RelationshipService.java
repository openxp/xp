package com.enonic.xp.repo.impl.relationship;

import com.enonic.xp.node.NodeId;
import com.enonic.xp.relationship.Relationship;
import com.enonic.xp.relationship.RelationshipId;
import com.enonic.xp.relationship.Relationships;
import com.enonic.xp.repo.impl.InternalContext;
import com.enonic.xp.util.Reference;

public interface RelationshipService
{
    Relationship store( final NodeId source, final Reference reference, final InternalContext context );

    Relationship delete( final RelationshipId id );

    Relationships find( final RelationshipQuery query );

    Relationship get( final RelationshipId id );

    Relationships getTargets( final NodeId source, final InternalContext context );

    Relationships getSources( final NodeId target, final InternalContext context );

}

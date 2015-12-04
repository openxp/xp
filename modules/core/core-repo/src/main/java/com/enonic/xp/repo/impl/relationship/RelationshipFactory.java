package com.enonic.xp.repo.impl.relationship;

import com.enonic.xp.node.NodeId;
import com.enonic.xp.relationship.Relationship;
import com.enonic.xp.relationship.RelationshipId;
import com.enonic.xp.util.Reference;

public class RelationshipFactory
{
    static Relationship create( final NodeId source, final Reference reference )
    {
        return Relationship.create().
            source( RelationshipMemberFactory.create( source ) ).
            target( RelationshipMemberFactory.create( reference ) ).
            id( new RelationshipId() ).
            name( reference.getName() ).
            type( reference.getType() ).
            build();
    }
}

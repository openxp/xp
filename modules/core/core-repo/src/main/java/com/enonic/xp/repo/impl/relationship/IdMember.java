package com.enonic.xp.repo.impl.relationship;

import com.enonic.xp.node.NodeId;
import com.enonic.xp.relationship.RelationshipMember;

public class IdMember
    implements RelationshipMember<NodeId>
{
    private final NodeId nodeId;

    public IdMember( final NodeId nodeId )
    {
        this.nodeId = nodeId;
    }

    public static IdMember from( final String value )
    {
        return new IdMember( NodeId.from( value ) );
    }

    @Override
    public boolean isTypeId()
    {
        return true;
    }

    @Override
    public boolean isTypePath()
    {
        return false;
    }

    @Override
    public NodeId getId()
    {
        return this.nodeId;
    }
}

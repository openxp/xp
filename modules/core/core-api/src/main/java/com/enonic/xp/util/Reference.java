package com.enonic.xp.util;

import com.google.common.annotations.Beta;

import com.enonic.xp.node.NodeId;
import com.enonic.xp.relationship.RelationshipName;
import com.enonic.xp.relationship.RelationshipType;

@Beta
public class Reference
{
    private final NodeId nodeId;

    private final RelationshipName name = RelationshipName.from( "unknown" );

    private final RelationshipType type = RelationshipType.MANAGED;

    public Reference( final NodeId nodeId )
    {
        this.nodeId = nodeId;
    }

    public static Reference from( final String value )
    {
        return new Reference( NodeId.from( value ) );
    }

    public RelationshipName getName()
    {
        return name;
    }

    public RelationshipType getType()
    {
        return type;
    }

    public NodeId getNodeId()
    {
        return nodeId;
    }

    @Override
    public String toString()
    {
        return nodeId.toString();
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( o == null || getClass() != o.getClass() )
        {
            return false;
        }

        final Reference reference = (Reference) o;

        if ( nodeId != null ? !nodeId.equals( reference.nodeId ) : reference.nodeId != null )
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        return nodeId != null ? nodeId.hashCode() : 0;
    }
}

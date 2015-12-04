package com.enonic.xp.repo.impl.relationship;

import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.relationship.RelationshipMember;
import com.enonic.xp.util.Reference;

public class RelationshipMemberFactory
{
    static RelationshipMember create( final String id )
    {
        if ( id.startsWith( "/" ) )
        {
            return new PathMember( NodePath.create( id ).build() );
        }

        return new IdMember( NodeId.from( id ) );
    }

    static RelationshipMember create( final NodeId nodeId )
    {
        return new IdMember( nodeId );
    }

    static RelationshipMember create( final Reference reference )
    {

        if ( reference.getNodeId() != null )
        {
            return new IdMember( reference.getNodeId() );
        }

        throw new UnsupportedOperationException( "Not implemented path-type references yet" );

    }


}

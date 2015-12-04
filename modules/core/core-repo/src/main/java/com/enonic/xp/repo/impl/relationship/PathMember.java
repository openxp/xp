package com.enonic.xp.repo.impl.relationship;

import com.enonic.xp.node.NodePath;
import com.enonic.xp.relationship.RelationshipMember;

public class PathMember
    implements RelationshipMember<NodePath>
{
    private final NodePath path;

    public PathMember( final NodePath path )
    {
        this.path = path;
    }

    @Override
    public boolean isTypeId()
    {
        return false;
    }

    @Override
    public boolean isTypePath()
    {
        return true;
    }

    @Override
    public NodePath getId()
    {
        return this.path;
    }


}

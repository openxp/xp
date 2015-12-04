package com.enonic.xp.relationship;

import java.util.UUID;

public class RelationshipId
{
    private String value;

    private RelationshipId( final String value )
    {
        this.value = value;
    }

    public RelationshipId()
    {
        this.value = UUID.randomUUID().toString();
    }

    public static RelationshipId from( final String value )
    {
        return new RelationshipId( value );
    }

    @Override
    public String toString()
    {
        return value;
    }
}

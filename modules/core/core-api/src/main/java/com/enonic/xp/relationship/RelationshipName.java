package com.enonic.xp.relationship;

public class RelationshipName
{
    private final String value;

    private RelationshipName( final String value )
    {
        this.value = value;
    }

    public static RelationshipName from( final String name )
    {
        return new RelationshipName( name );
    }

    @Override
    public String toString()
    {
        return value;
    }
}

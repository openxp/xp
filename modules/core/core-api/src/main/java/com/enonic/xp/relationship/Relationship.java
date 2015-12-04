package com.enonic.xp.relationship;

public class Relationship
{
    private final RelationshipId id;

    private final RelationshipMember source;

    private final RelationshipMember target;

    private final RelationshipType type;

    private final RelationshipName name;

    private Relationship( Builder builder )
    {
        this.id = builder.id;
        this.source = builder.source;
        this.target = builder.target;
        this.type = builder.type;
        this.name = builder.name;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public RelationshipId getId()
    {
        return id;
    }

    public RelationshipMember getSource()
    {
        return source;
    }

    public RelationshipMember getTarget()
    {
        return target;
    }

    public RelationshipType getType()
    {
        return type;
    }

    public RelationshipName getName()
    {
        return name;
    }

    public static final class Builder
    {
        private RelationshipId id;

        private RelationshipMember source;

        private RelationshipMember target;

        private RelationshipType type = RelationshipType.MANAGED;

        private RelationshipName name;

        private Builder()
        {
        }

        public Builder id( final RelationshipId id )
        {
            this.id = id;
            return this;
        }

        public Builder source( final RelationshipMember source )
        {
            this.source = source;
            return this;
        }

        public Builder target( final RelationshipMember target )
        {
            this.target = target;
            return this;
        }

        public Builder type( final RelationshipType type )
        {
            this.type = type;
            return this;
        }

        public Builder name( final RelationshipName name )
        {
            this.name = name;
            return this;
        }

        public Relationship build()
        {
            return new Relationship( this );
        }
    }
}

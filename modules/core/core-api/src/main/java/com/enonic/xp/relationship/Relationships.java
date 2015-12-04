package com.enonic.xp.relationship;

import java.util.Iterator;
import java.util.List;
import java.util.Spliterator;
import java.util.function.Consumer;

import com.google.common.collect.Lists;

public class Relationships
    implements Iterable<Relationship>
{
    private final List<Relationship> relationships;

    private Relationships( final Builder builder )
    {
        this.relationships = builder.relationships;
    }

    @Override
    public Iterator<Relationship> iterator()
    {
        return relationships.iterator();
    }

    @Override
    public void forEach( final Consumer<? super Relationship> action )
    {

    }

    public List<Relationship> getRelationships()
    {
        return relationships;
    }

    public static Builder create()
    {
        return new Builder();
    }

    @Override
    public Spliterator<Relationship> spliterator()
    {
        return null;
    }

    public static class Builder
    {

        private final List<Relationship> relationships = Lists.newArrayList();

        public Builder add( final Relationship relationship )
        {
            this.relationships.add( relationship );
            return this;
        }

        public Relationships build()
        {
            return new Relationships( this );
        }

    }
}

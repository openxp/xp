package com.enonic.xp.repo.impl.relationship;

import java.util.Iterator;
import java.util.Spliterator;
import java.util.function.Consumer;

public class Relationships
    implements Iterable<IdRelationship>
{

    @Override
    public Iterator<IdRelationship> iterator()
    {
        return null;
    }

    @Override
    public void forEach( final Consumer<? super IdRelationship> action )
    {

    }

    @Override
    public Spliterator<IdRelationship> spliterator()
    {
        return null;
    }
}

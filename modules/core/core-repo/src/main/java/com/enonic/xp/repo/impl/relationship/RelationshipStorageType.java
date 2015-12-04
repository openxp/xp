package com.enonic.xp.repo.impl.relationship;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.repo.impl.StorageType;

public class RelationshipStorageType
    implements StorageType
{

    private final String name;

    private RelationshipStorageType( final String name )
    {
        this.name = name;
    }

    public static StorageType from( final Branch branch )
    {
        return new RelationshipStorageType( branch.getName() );
    }

    @Override
    public String getName()
    {
        return this.name;
    }


}

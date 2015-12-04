package com.enonic.xp.repo.impl.relationship;

import com.enonic.xp.relationship.Relationship;
import com.enonic.xp.relationship.RelationshipId;
import com.enonic.xp.repo.impl.InternalContext;
import com.enonic.xp.repo.impl.StorageSettings;
import com.enonic.xp.repo.impl.storage.StorageData;
import com.enonic.xp.repo.impl.storage.StoreRequest;

public class RelationshipStoreRequestFactory
{
    static StoreRequest create( final Relationship relationship, final InternalContext context )
    {
        return StoreRequest.create().
            id( new RelationshipId().toString() ).
            data( StorageData.create().
                add( RelationshipIndexPath.SOURCE.getPath(), relationship.getSource().getId() ).
                add( RelationshipIndexPath.TARGET.getPath(), relationship.getTarget().getId() ).
                build() ).
            settings( StorageSettings.create().
                storageName( RelationshipStorageName.from( context.getRepositoryId() ) ).
                storageType( RelationshipStorageType.from( context.getBranch() ) ).
                build() ).
            build();
    }
}

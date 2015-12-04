package com.enonic.xp.repo.impl.relationship;

import java.util.Map;

import org.osgi.service.component.annotations.Component;

import com.google.common.collect.Maps;

import com.enonic.xp.node.NodeId;
import com.enonic.xp.relationship.Relationship;
import com.enonic.xp.relationship.RelationshipId;
import com.enonic.xp.relationship.Relationships;
import com.enonic.xp.repo.impl.InternalContext;
import com.enonic.xp.repo.impl.ReturnFields;
import com.enonic.xp.repo.impl.StorageSettings;
import com.enonic.xp.repo.impl.search.result.SearchHit;
import com.enonic.xp.repo.impl.search.result.SearchHits;
import com.enonic.xp.repo.impl.search.result.SearchResult;
import com.enonic.xp.repo.impl.storage.GetByValuesRequest;
import com.enonic.xp.repo.impl.storage.StorageDao;
import com.enonic.xp.repo.impl.storage.StoreRequest;
import com.enonic.xp.util.Reference;

@Component
public class RelationshipServiceImpl
    implements RelationshipService
{
    private StorageDao storageDao;

    @Override
    public Relationship store( final NodeId source, final Reference reference, final InternalContext context )
    {
        final Relationship relationship = RelationshipFactory.create( source, reference );

        final StoreRequest storeRequest = RelationshipStoreRequestFactory.create( relationship, context );

        storageDao.store( storeRequest );

        return relationship;
    }

    @Override
    public Relationship delete( final RelationshipId id )
    {
        return null;
    }

    @Override
    public Relationships find( final RelationshipQuery query )
    {
        return null;
    }

    @Override
    public Relationship get( final RelationshipId id )
    {
        return null;
    }

    @Override
    public Relationships getTargets( final NodeId source, final InternalContext context )
    {
        Map<String, Object> valueMap = Maps.newHashMap();
        valueMap.put( RelationshipIndexPath.SOURCE.getPath(), source );

        final GetByValuesRequest request = createGetByValueRequest( context, valueMap );

        final SearchResult searchResult = this.storageDao.getByValues( request );

        return createRelationships( searchResult );
    }

    private Relationships createRelationships( final SearchResult searchResult )
    {
        final SearchHits hits = searchResult.getResults();

        final Relationships.Builder relationships = Relationships.create();

        for ( final SearchHit hit : hits )
        {
            relationships.add( RelationshipSearchHitFactory.create( hit ) );
        }

        return relationships.build();
    }

    private GetByValuesRequest createGetByValueRequest( final InternalContext context, final Map<String, Object> values )
    {
        final GetByValuesRequest.Builder builder = GetByValuesRequest.create().
            storageSettings( StorageSettings.create().
                storageName( RelationshipStorageName.from( context.getRepositoryId() ) ).
                storageType( RelationshipStorageType.from( context.getBranch() ) ).
                build() ).
            expectSingleValue( false ).
            returnFields( ReturnFields.from( RelationshipIndexPath.ID, RelationshipIndexPath.SOURCE, RelationshipIndexPath.TARGET ) );

        for ( final String key : values.keySet() )
        {
            builder.addValue( key, values.get( key ) );
        }

        return builder.build();
    }

    @Override
    public Relationships getSources( final NodeId target, final InternalContext context )
    {
        Map<String, Object> valueMap = Maps.newHashMap();
        valueMap.put( RelationshipIndexPath.TARGET.getPath(), target );

        final GetByValuesRequest request = createGetByValueRequest( context, valueMap );

        final SearchResult searchResult = this.storageDao.getByValues( request );

        return createRelationships( searchResult );
    }

    @org.osgi.service.component.annotations.Reference
    public void setStorageDao( final StorageDao storageDao )
    {
        this.storageDao = storageDao;
    }
}

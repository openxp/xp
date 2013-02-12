package com.enonic.wem.core.search;

import java.util.Collection;
import java.util.List;

import org.elasticsearch.ElasticSearchException;
import org.elasticsearch.indices.IndexAlreadyExistsException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.google.common.collect.Lists;

import com.enonic.wem.api.account.Account;
import com.enonic.wem.api.account.UserAccount;
import com.enonic.wem.core.search.elastic.ElasticsearchIndexServiceImpl;
import com.enonic.wem.core.search.elastic.IndexMapping;
import com.enonic.wem.core.search.elastic.IndexMappingProvider;

public class IndexServiceImplTest

{
    private IndexService indexService;

    @Before
    public void init()
    {
        indexService = new IndexService();
        indexService.setDoReindexOnEmptyIndex( false );
    }

    @Test
    public void testInitializeIndex_index_not_exists()
        throws Exception
    {
        final ElasticsearchIndexServiceImpl elasticsearchIndexService = Mockito.mock( ElasticsearchIndexServiceImpl.class );
        Mockito.when( elasticsearchIndexService.getIndexStatus( IndexConstants.WEM_INDEX.value(), true ) ).thenReturn( IndexStatus.YELLOW );
        Mockito.when( elasticsearchIndexService.indexExists( IndexConstants.WEM_INDEX.value() ) ).thenReturn( false );

        final IndexMappingProvider indexMappingProvider = setUpIndexMappingMock();

        indexService.setIndexMappingProvider( indexMappingProvider );

        indexService.setElasticsearchIndexService( elasticsearchIndexService );

        indexService.initialize();

        Mockito.verify( elasticsearchIndexService, Mockito.times( 1 ) ).createIndex( IndexConstants.WEM_INDEX.value() );
    }

    @Test
    public void testInitializeIndex_index_exists()
        throws Exception
    {

        final ElasticsearchIndexServiceImpl elasticsearchIndexService = Mockito.mock( ElasticsearchIndexServiceImpl.class );
        Mockito.when( elasticsearchIndexService.getIndexStatus( IndexConstants.WEM_INDEX.value(), true ) ).thenReturn( IndexStatus.YELLOW );
        Mockito.when( elasticsearchIndexService.indexExists( IndexConstants.WEM_INDEX.value() ) ).thenReturn( true );

        final IndexMappingProvider indexMappingProvider = setUpIndexMappingMock();

        indexService.setIndexMappingProvider( indexMappingProvider );

        indexService.setElasticsearchIndexService( elasticsearchIndexService );

        indexService.initialize();

        Mockito.verify( elasticsearchIndexService, Mockito.never() ).createIndex( IndexConstants.WEM_INDEX.value() );
    }

    @Test
    public void testInitializeIndex_index_already_exists_exception()
        throws Exception
    {
        final ElasticsearchIndexServiceImpl elasticsearchIndexService = Mockito.mock( ElasticsearchIndexServiceImpl.class );
        Mockito.when( elasticsearchIndexService.getIndexStatus( IndexConstants.WEM_INDEX.value(), true ) ).thenReturn( IndexStatus.YELLOW );
        Mockito.when( elasticsearchIndexService.indexExists( IndexConstants.WEM_INDEX.value() ) ).thenReturn( false );

        Mockito.doThrow( new IndexAlreadyExistsException( null ) ).when( elasticsearchIndexService ).createIndex(
            IndexConstants.WEM_INDEX.value() );

        final IndexMappingProvider indexMappingProvider = setUpIndexMappingMock();

        indexService.setIndexMappingProvider( indexMappingProvider );

        indexService.setElasticsearchIndexService( elasticsearchIndexService );

        indexService.initialize();

        // Since index already exists exception, do not continue to add mapping
        Mockito.verify( elasticsearchIndexService, Mockito.never() ).putMapping( Mockito.isA( IndexMapping.class ) );
    }

    @Test(expected = ElasticSearchException.class)
    public void testInitializeIndex_create_index_fails_with_exception()
        throws Exception
    {
        final ElasticsearchIndexServiceImpl elasticsearchIndexService = Mockito.mock( ElasticsearchIndexServiceImpl.class );
        Mockito.when( elasticsearchIndexService.getIndexStatus( IndexConstants.WEM_INDEX.value(), true ) ).thenReturn( IndexStatus.YELLOW );
        Mockito.when( elasticsearchIndexService.indexExists( IndexConstants.WEM_INDEX.value() ) ).thenReturn( false );

        Mockito.doThrow( new ElasticSearchException( "expected" ) ).when( elasticsearchIndexService ).createIndex(
            IndexConstants.WEM_INDEX.value() );

        final IndexMappingProvider indexMappingProvider = setUpIndexMappingMock();

        indexService.setIndexMappingProvider( indexMappingProvider );

        indexService.setElasticsearchIndexService( elasticsearchIndexService );

        indexService.initialize();

        // Since index already exists exception, do not continue to add mapping
        Mockito.verify( elasticsearchIndexService, Mockito.never() ).putMapping( Mockito.isA( IndexMapping.class ) );
    }

    @Test
    public void testInitializeIndex_reindex_on_create()
        throws Exception
    {
        final ElasticsearchIndexServiceImpl elasticsearchIndexService = Mockito.mock( ElasticsearchIndexServiceImpl.class );
        Mockito.when( elasticsearchIndexService.getIndexStatus( IndexConstants.WEM_INDEX.value(), true ) ).thenReturn( IndexStatus.YELLOW );
        Mockito.when( elasticsearchIndexService.indexExists( IndexConstants.WEM_INDEX.value() ) ).thenReturn( false );
        indexService.setDoReindexOnEmptyIndex( true );

        final ReindexService reindexService = Mockito.mock( ReindexService.class );
        indexService.setReindexService( reindexService );

        final IndexMappingProvider indexMappingProvider = setUpIndexMappingMock();

        indexService.setIndexMappingProvider( indexMappingProvider );

        indexService.setElasticsearchIndexService( elasticsearchIndexService );

        indexService.initialize();

        Mockito.verify( elasticsearchIndexService, Mockito.times( 1 ) ).createIndex( IndexConstants.WEM_INDEX.value() );
        Mockito.verify( reindexService, Mockito.times( 1 ) ).reindexAccounts();
    }

    @Test
    public void testIndexAccount()
    {
        Account account = UserAccount.create( "enonic:rmy" );

        final ElasticsearchIndexServiceImpl elasticsearchIndexService = Mockito.mock( ElasticsearchIndexServiceImpl.class );
        indexService.setElasticsearchIndexService( elasticsearchIndexService );

        indexService.index( account );

        Mockito.verify( elasticsearchIndexService, Mockito.times( 1 ) ).index( Mockito.isA( Collection.class ) );
    }


    private IndexMappingProvider setUpIndexMappingMock()
    {
        final IndexMappingProvider indexMappingProvider = Mockito.mock( IndexMappingProvider.class );
        List<IndexMapping> indexMappings = Lists.newArrayList(
            new IndexMapping( IndexConstants.WEM_INDEX.value(), IndexType.ACCOUNT.getIndexTypeName(), "Testings 1234" ) );
        Mockito.when( indexMappingProvider.getMappingsForIndex( IndexConstants.WEM_INDEX.value() ) ).thenReturn( indexMappings );
        return indexMappingProvider;
    }

}

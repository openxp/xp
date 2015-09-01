package com.enonic.wem.repo.internal.index;

import org.junit.Before;
import org.junit.Test;

import com.enonic.wem.repo.internal.entity.AbstractNodeTest;
import com.enonic.wem.repo.internal.entity.FindNodesByQueryCommand;
import com.enonic.wem.repo.internal.entity.PushNodesCommand;
import com.enonic.xp.index.PurgeIndexParams;
import com.enonic.xp.index.ReindexParams;
import com.enonic.xp.index.ReindexResult;
import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.FindNodesByQueryResult;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeQuery;
import com.enonic.xp.query.parser.QueryParser;

import static org.junit.Assert.*;

public class IndexServiceImplTest
    extends AbstractNodeTest
{
    private IndexServiceImpl indexService;

    private Node rootNode;

    @Override
    @Before
    public void setUp()
        throws Exception
    {
        super.setUp();
        this.indexService = new IndexServiceImpl();
        this.indexService.setBranchService( this.branchService );
        this.indexService.setIndexServiceInternal( this.indexServiceInternal );
        this.indexService.setNodeDao( this.nodeDao );

        this.rootNode = this.createDefaultRootNode();
    }

    @Test
    public void initialize()
        throws Exception
    {
        final Node node = createNode( CreateNodeParams.create().
            name( "myNode" ).
            parent( NodePath.ROOT ).
            build() );

        refresh();

        final ReindexResult result = this.indexService.reindex( ReindexParams.create().
            addBranch( CTX_DEFAULT.getBranch() ).
            repositoryId( CTX_DEFAULT.getRepositoryId() ).
            initialize( true ).
            build() );

        assertEquals( 2, result.getReindexNodes().getSize() );

        assertNotNull( queryForNode( node.id() ) );
        assertNotNull( queryForNode( rootNode.id() ) );
    }

    @Test
    public void not_initialize()
        throws Exception
    {
        final Node node = createNode( CreateNodeParams.create().
            name( "myNode" ).
            parent( NodePath.ROOT ).
            build() );

        refresh();

        final ReindexResult result = this.indexService.reindex( ReindexParams.create().
            addBranch( CTX_DEFAULT.getBranch() ).
            repositoryId( CTX_DEFAULT.getRepositoryId() ).
            initialize( false ).
            build() );

        assertEquals( 2, result.getReindexNodes().getSize() );

        assertNotNull( queryForNode( node.id() ) );
        assertNotNull( queryForNode( rootNode.id() ) );
    }


    @Test
    public void purge_then_reindex()
        throws Exception
    {
        final Node node = createNode( CreateNodeParams.create().
            name( "myNode" ).
            parent( NodePath.ROOT ).
            build() );

        refresh();

        this.indexService.purgeSearchIndex( new PurgeIndexParams( CTX_DEFAULT.getRepositoryId() ) );

        assertNull( queryForNode( node.id() ) );

        final ReindexResult result = this.indexService.reindex( ReindexParams.create().
            addBranch( CTX_DEFAULT.getBranch() ).
            repositoryId( CTX_DEFAULT.getRepositoryId() ).
            initialize( false ).
            build() );

        assertEquals( 2, result.getReindexNodes().getSize() );

        assertNotNull( queryForNode( node.id() ) );
        assertNotNull( queryForNode( rootNode.id() ) );
    }

    @Test
    public void reindex_other_branch()
        throws Exception
    {
        final Node node = createNode( CreateNodeParams.create().
            name( "myNode" ).
            parent( NodePath.ROOT ).
            build() );

        PushNodesCommand.create().
            ids( NodeIds.from( node.id() ) ).
            target( CTX_OTHER.getBranch() ).
            indexServiceInternal( this.indexServiceInternal ).
            nodeDao( this.nodeDao ).
            versionService( this.versionService ).
            branchService( this.branchService ).
            queryService( this.queryService ).
            build().
            execute();

        refresh();

        assertNotNull( queryForNode( node.id() ) );
        assertNotNull( CTX_OTHER.callWith( () -> queryForNode( node.id() ) ) );

        this.indexService.purgeSearchIndex( new PurgeIndexParams( CTX_DEFAULT.getRepositoryId() ) );

        refresh();

        assertNull( queryForNode( node.id() ) );

        this.indexService.reindex( ReindexParams.create().
            addBranch( CTX_DEFAULT.getBranch() ).
            repositoryId( CTX_DEFAULT.getRepositoryId() ).
            initialize( false ).
            build() );

        assertNotNull( queryForNode( node.id() ) );
        assertNull( CTX_OTHER.callWith( () -> queryForNode( node.id() ) ) );

        this.indexService.reindex( ReindexParams.create().
            addBranch( CTX_OTHER.getBranch() ).
            repositoryId( CTX_DEFAULT.getRepositoryId() ).
            initialize( true ).
            build() );

        assertNull( queryForNode( node.id() ) );
        assertNotNull( CTX_OTHER.callWith( () -> queryForNode( node.id() ) ) );
    }

    private Node queryForNode( final NodeId nodeId )
    {
        final FindNodesByQueryResult result = FindNodesByQueryCommand.create().
            nodeDao( this.nodeDao ).
            branchService( this.branchService ).
            indexServiceInternal( this.indexServiceInternal ).
            queryService( this.queryService ).
            versionService( this.versionService ).
            query( NodeQuery.create().query( QueryParser.parse( "_id = '" + nodeId.toString() + "'" ) ).build() ).
            build().
            execute();

        return result.getNodes().getNodeById( nodeId );
    }

}
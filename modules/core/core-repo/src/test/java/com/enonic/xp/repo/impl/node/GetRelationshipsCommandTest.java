package com.enonic.xp.repo.impl.node;

import org.junit.Before;
import org.junit.Test;

import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.relationship.Relationships;
import com.enonic.xp.util.Reference;

import static org.junit.Assert.*;

public class GetRelationshipsCommandTest
    extends AbstractNodeTest
{
    @Before
    public void setUp()
        throws Exception
    {
        super.setUp();
        this.createDefaultRootNode();
    }

    @Test
    public void single_id()
        throws Exception
    {
        final PropertyTree node1_data = new PropertyTree();
        node1_data.addReference( "myRef", Reference.from( "node2" ) );

        final Node node1_1 = createNode( CreateNodeParams.create().
            setNodeId( NodeId.from( "node1_1" ) ).
            parent( NodePath.ROOT ).
            name( "node1_1" ).
            data( node1_data ).
            build() );

        createNode( CreateNodeParams.create().
            setNodeId( NodeId.from( "node2" ) ).
            parent( NodePath.ROOT ).
            name( "node2" ).
            build() );

        final Relationships relationships = GetRelationshipsCommand.create().
            nodeId( node1_1.id() ).
            indexServiceInternal( this.indexServiceInternal ).
            storageService( this.storageService ).
            searchService( this.searchService ).
            build().
            execute();

        assertFalse( relationships.getRelationships().isEmpty() );
        assertEquals( 1, relationships.getRelationships().size() );
    }

    @Test
    public void multiple_id()
        throws Exception
    {
        final PropertyTree node1_data = new PropertyTree();
        node1_data.addReference( "myRef", Reference.from( "node2" ) );
        node1_data.addReference( "myRef", Reference.from( "node3" ) );

        final Node node1_1 = createNode( CreateNodeParams.create().
            setNodeId( NodeId.from( "node1_1" ) ).
            parent( NodePath.ROOT ).
            name( "node1_1" ).
            data( node1_data ).
            build() );

        createNode( CreateNodeParams.create().
            setNodeId( NodeId.from( "node2" ) ).
            parent( NodePath.ROOT ).
            name( "node2" ).
            build() );

        createNode( CreateNodeParams.create().
            setNodeId( NodeId.from( "node3" ) ).
            parent( NodePath.ROOT ).
            name( "node3" ).
            build() );

        final Relationships relationships = GetRelationshipsCommand.create().
            nodeId( node1_1.id() ).
            indexServiceInternal( this.indexServiceInternal ).
            storageService( this.storageService ).
            searchService( this.searchService ).
            build().
            execute();

        assertFalse( relationships.getRelationships().isEmpty() );
        assertEquals( 2, relationships.getRelationships().size() );
    }
}
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

public class GetReferencesCommandTest
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

        createNode( CreateNodeParams.create().
            setNodeId( NodeId.from( "node1_1" ) ).
            parent( NodePath.ROOT ).
            name( "node1_1" ).
            data( node1_data ).
            build() );

        final Node node2 = createNode( CreateNodeParams.create().
            setNodeId( NodeId.from( "node2" ) ).
            parent( NodePath.ROOT ).
            name( "node2" ).
            build() );

        final Relationships references = GetReferencesCommand.create().
            nodeId( node2.id() ).
            indexServiceInternal( this.indexServiceInternal ).
            storageService( this.storageService ).
            searchService( this.searchService ).
            build().
            execute();

        assertFalse( references.getRelationships().isEmpty() );
        assertEquals( 1, references.getRelationships().size() );
    }

    @Test
    public void multiple_id()
        throws Exception
    {
        final PropertyTree referenceToNode3Data = new PropertyTree();
        referenceToNode3Data.addReference( "myRef", Reference.from( "node3" ) );

        createNode( CreateNodeParams.create().
            setNodeId( NodeId.from( "node1" ) ).
            parent( NodePath.ROOT ).
            name( "node1" ).
            data( referenceToNode3Data ).
            build() );

        createNode( CreateNodeParams.create().
            setNodeId( NodeId.from( "node2" ) ).
            parent( NodePath.ROOT ).
            name( "node2" ).
            data( referenceToNode3Data ).
            build() );

        final Node node3 = createNode( CreateNodeParams.create().
            setNodeId( NodeId.from( "node3" ) ).
            parent( NodePath.ROOT ).
            name( "node3" ).
            build() );

        refresh();

        final Relationships references = GetReferencesCommand.create().
            nodeId( node3.id() ).
            indexServiceInternal( this.indexServiceInternal ).
            storageService( this.storageService ).
            searchService( this.searchService ).
            build().
            execute();

        assertFalse( references.getRelationships().isEmpty() );
        assertEquals( 2, references.getRelationships().size() );
    }
}
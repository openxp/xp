package com.enonic.wem.export.internal;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

import com.enonic.wem.api.export.ImportNodeException;
import com.enonic.wem.api.export.NodeImportResult;
import com.enonic.wem.api.node.CreateNodeParams;
import com.enonic.wem.api.node.Node;
import com.enonic.wem.api.node.NodePath;
import com.enonic.wem.api.node.NodeService;
import com.enonic.wem.export.internal.reader.FileExportReader;
import com.enonic.wem.export.internal.writer.NodeExportPathResolver;
import com.enonic.wem.export.internal.xml.serializer.XmlNodeSerializer;

import static org.junit.Assert.*;

public class NodeImportCommandTest
{
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    private NodeService importNodeService;

    @Before
    public void setUp()
        throws Exception
    {
        this.importNodeService = new NodeServiceMock();
    }

    @Test
    public void import_node()
        throws Exception
    {
        final Path nodeFileDir = Files.createDirectories( Paths.get( temporaryFolder.getRoot().getPath() + "/myExport/mynode/_" ) );
        assert nodeFileDir != null;

        final byte[] nodeXmlFile = readFromFile( "node_unordered.xml" ).getBytes();

        Files.write( Paths.get( nodeFileDir.toString(), NodeExportPathResolver.NODE_XML_EXPORT_NAME ), nodeXmlFile );

        final NodeServiceMock importNodeService = new NodeServiceMock();
        final NodeImportResult nodeImportResult = NodeImportCommand.create().
            nodeService( importNodeService ).
            exportReader( new FileExportReader() ).
            xmlNodeSerializer( new XmlNodeSerializer() ).
            importRoot( NodePath.ROOT ).
            exportHome( this.temporaryFolder.getRoot().toPath() ).
            exportName( "myExport" ).
            build().
            execute();

        assertEquals( 1, nodeImportResult.importedNodes.getSize() );
    }

    @Test
    public void import_nodes()
        throws Exception
    {
        createNodeXmlFile( "/myExport/mynode", false );
        createNodeXmlFile( "/myExport/mynode/mychild", false );
        createNodeXmlFile( "/myExport/mynode/mychild/mychildchild", false );
        createNodeXmlFile( "/myExport/mynode/mychild/mychildchild/mychildchildchild", false );

        final NodeImportResult nodeImportResult = NodeImportCommand.create().
            nodeService( this.importNodeService ).
            exportReader( new FileExportReader() ).
            xmlNodeSerializer( new XmlNodeSerializer() ).
            importRoot( NodePath.ROOT ).
            exportHome( this.temporaryFolder.getRoot().toPath() ).
            exportName( "myExport" ).
            build().
            execute();

        assertEquals( 4, nodeImportResult.importedNodes.getSize() );

        final Node mynode = assertNodeExists( NodePath.ROOT, "mynode" );
        final Node mychild = assertNodeExists( mynode.path(), "mychild" );
        final Node mychildchild = assertNodeExists( mychild.path(), "mychildchild" );
        assertNodeExists( mychildchild.path(), "mychildchildchild" );
    }

    @Test
    public void import_nodes_into_child()
        throws Exception
    {
        createNodeXmlFile( "/myExport/mynode", false );
        createNodeXmlFile( "/myExport/mynode/mychild", false );
        createNodeXmlFile( "/myExport/mynode/mychild/mychildchild", false );
        createNodeXmlFile( "/myExport/mynode/mychild/mychildchild/mychildchildchild", false );

        final NodePath importRoot = NodePath.newNodePath( NodePath.ROOT, "my-import-here" ).build();

        this.importNodeService.create( CreateNodeParams.create().
            parent( importRoot.getParentPath() ).
            name( importRoot.getLastElement().toString() ).
            build() );

        final NodeImportResult nodeImportResult = NodeImportCommand.create().
            nodeService( this.importNodeService ).
            exportReader( new FileExportReader() ).
            xmlNodeSerializer( new XmlNodeSerializer() ).
            importRoot( importRoot ).
            exportHome( this.temporaryFolder.getRoot().toPath() ).
            exportName( "myExport" ).
            build().
            execute();

        assertEquals( 4, nodeImportResult.importedNodes.getSize() );

        final Node mynode = assertNodeExists( importRoot, "mynode" );
        final Node mychild = assertNodeExists( mynode.path(), "mychild" );
        final Node mychildchild = assertNodeExists( mychild.path(), "mychildchild" );
        assertNodeExists( mychildchild.path(), "mychildchildchild" );
    }


    @Test(expected = ImportNodeException.class)
    public void import_node_non_existing_parent()
        throws Exception
    {
        final NodePath importRoot = NodePath.newNodePath( NodePath.ROOT, "non-existing-node" ).build();

        NodeImportCommand.create().
            nodeService( this.importNodeService ).
            exportReader( new FileExportReader() ).
            xmlNodeSerializer( new XmlNodeSerializer() ).
            importRoot( importRoot ).
            exportHome( this.temporaryFolder.getRoot().toPath() ).
            exportName( "myExport" ).
            build().
            execute();
    }

    @Test(expected = ImportNodeException.class)
    public void expect_order_file_if_manual()
        throws Exception
    {
        createNodeXmlFile( "/myExport/mynode/_", true );

        NodeImportCommand.create().
            nodeService( this.importNodeService ).
            exportReader( new FileExportReader() ).
            xmlNodeSerializer( new XmlNodeSerializer() ).
            importRoot( NodePath.ROOT ).
            exportHome( this.temporaryFolder.getRoot().toPath() ).
            exportName( "myExport" ).
            build().
            execute();
    }

    @Test
    public void import_nodes_ordered()
        throws Exception
    {
        createNodeXmlFile( "/myExport/mynode", true );
        createNodeXmlFile( "/myExport/mynode/mychild1", false );
        createNodeXmlFile( "/myExport/mynode/mychild1/mychildchild", true );
        createNodeXmlFile( "/myExport/mynode/mychild1/mychildchild/mychildchildchild1", false );
        createNodeXmlFile( "/myExport/mynode/mychild1/mychildchild/mychildchildchild2", false );
        createNodeXmlFile( "/myExport/mynode/mychild1/mychildchild/mychildchildchild3", false );
        createNodeXmlFile( "/myExport/mynode/mychild2", false );

        createOrderFile( "/myExport/mynode", "mychild2", "mychild1" );
        createOrderFile( "/myExport/mynode/mychild1/mychildchild", "mychildchildchild1", "mychildchildchild2", "mychildchildchild3" );

        final NodeImportResult nodeImportResult = NodeImportCommand.create().
            nodeService( this.importNodeService ).
            exportReader( new FileExportReader() ).
            xmlNodeSerializer( new XmlNodeSerializer() ).
            importRoot( NodePath.ROOT ).
            exportHome( this.temporaryFolder.getRoot().toPath() ).
            exportName( "myExport" ).
            build().
            execute();

        assertEquals( 7, nodeImportResult.importedNodes.getSize() );

        final Node mynode = assertNodeExists( NodePath.ROOT, "mynode" );
        final Node mychild = assertNodeExists( mynode.path(), "mychild1" );
        final Node mychildchild = assertNodeExists( mychild.path(), "mychildchild" );
        assertNodeExists( mynode.path(), "mychild2" );
        assertNodeExists( mychildchild.path(), "mychildchildchild1" );
        assertNodeExists( mychildchild.path(), "mychildchildchild2" );
        assertNodeExists( mychildchild.path(), "mychildchildchild3" );
    }

    @Test
    public void ordered_not_in_list_not_imported()
        throws Exception
    {
        createNodeXmlFile( "/myExport/mynode", true );
        createNodeXmlFile( "/myExport/mynode/mychild1", false );
        createNodeXmlFile( "/myExport/mynode/mychild2", false );
        createNodeXmlFile( "/myExport/mynode/mychild3", false );

        createOrderFile( "/myExport/mynode", "mychild2", "mychild1" );

        final NodeImportResult nodeImportResult = NodeImportCommand.create().
            nodeService( this.importNodeService ).
            exportReader( new FileExportReader() ).
            xmlNodeSerializer( new XmlNodeSerializer() ).
            importRoot( NodePath.ROOT ).
            exportHome( this.temporaryFolder.getRoot().toPath() ).
            exportName( "myExport" ).
            build().
            execute();

        assertEquals( 3, nodeImportResult.importedNodes.getSize() );

        final Node mynode = assertNodeExists( NodePath.ROOT, "mynode" );
        assertNodeExists( mynode.path(), "mychild1" );
        assertNodeExists( mynode.path(), "mychild2" );

        final Node mychild3 = this.importNodeService.getByPath( NodePath.newNodePath( mynode.path(), "mychild3" ).build() );
        assertNull( mychild3 );
    }

    private void createOrderFile( final String path, final String... childNodeNames )
        throws Exception
    {
        final String lineSeparator = System.getProperty( "line.separator" );

        final Path nodeFileDir =
            Files.createDirectories( Paths.get( temporaryFolder.getRoot().getPath(), path, NodeExportPathResolver.SYSTEM_FOLDER_NAME ) );

        StringBuilder builder = new StringBuilder();

        for ( final String childNodeName : childNodeNames )
        {
            builder.append( childNodeName );
            builder.append( lineSeparator );
        }

        Files.write( Paths.get( nodeFileDir.toString(), NodeExportPathResolver.ORDER_EXPORT_NAME ), builder.toString().getBytes() );
    }

    private void createNodeXmlFile( final String path, boolean ordered )
        throws Exception
    {
        final Path nodeFileDir =
            Files.createDirectories( Paths.get( temporaryFolder.getRoot().getPath(), path, NodeExportPathResolver.SYSTEM_FOLDER_NAME ) );

        Files.write( Paths.get( nodeFileDir.toString(), NodeExportPathResolver.NODE_XML_EXPORT_NAME ),
                     readFromFile( ordered ? "node_manual_ordered.xml" : "node_unordered.xml" ).getBytes() );
    }


    private Node assertNodeExists( final NodePath parentPath, final String name )
    {
        final Node node = importNodeService.getByPath( NodePath.newNodePath( parentPath, name ).build() );
        assertNotNull( node );
        return node;
    }

    final String readFromFile( final String fileName )
        throws Exception
    {
        final URL url = getClass().getResource( fileName );
        if ( url == null )
        {
            throw new IllegalArgumentException( "Resource file [" + fileName + "] not found" );
        }

        return Resources.toString( url, Charsets.UTF_8 );

    }

}
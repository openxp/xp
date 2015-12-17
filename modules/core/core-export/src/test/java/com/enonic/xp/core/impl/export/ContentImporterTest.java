package com.enonic.xp.core.impl.export;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mockito;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.content.CreateContentParams;
import com.enonic.xp.core.impl.export.writer.NodeExportPathResolver;
import com.enonic.xp.export.ContentImportResult;
import com.enonic.xp.vfs.VirtualFiles;

import static org.junit.Assert.*;

public class ContentImporterTest
{
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    private ContentService contentService;

    @Before
    public void setUp()
        throws Exception
    {
        this.contentService = Mockito.mock( ContentService.class );
    }

    @Test
    public void importNodes()
        throws Exception
    {
        createNodeXmlFile( Paths.get( "myExport", "mynode" ) );
        createNodeXmlFile( Paths.get( "myExport", "mynode", "mychild" ) );
        createNodeXmlFile( Paths.get( "myExport", "mynode", "mychild", "mychildchild" ) );
        createContentFolder( Paths.get( "myExport", "mynode", "mychild", "folder_1" ) );
        createNodeXmlFile( Paths.get( "myExport", "mynode", "mychild", "mychildchild", "mychildchildchild" ) );
        createContentFolder( Paths.get( "myExport", "mynode", "mychild", "mychildchild", "mychildchildchild", "folder_2" ) );
        createContentFolder( Paths.get( "myExport", "mynode", "mychild", "mychildchild", "mychildchildchild", "folder_2", "folder_2_a" ) );

        final ContentImportResult result = ContentImporter.create().
            contentService( this.contentService ).
            targetPath( ContentPath.ROOT ).
            sourceDirectory( VirtualFiles.from( Paths.get( this.temporaryFolder.getRoot().toPath().toString(), "myExport" ) ) ).
            build().
            execute();

        assertEquals( 0, result.getImportErrors().size() );
        assertEquals( 3, result.getAddedContent().getSize() );

        Mockito.verify( contentService, Mockito.times( 3 ) ).create( Mockito.isA( CreateContentParams.class ) );
    }

    private void createNodeXmlFile( final Path exportPath )
        throws Exception
    {
        final Path nodeFileDir = Files.createDirectories(
            Paths.get( temporaryFolder.getRoot().getPath(), exportPath.toString(), NodeExportPathResolver.SYSTEM_FOLDER_NAME ) );

        assertNotNull( nodeFileDir );
        Files.write( Paths.get( nodeFileDir.toString(), NodeExportPathResolver.NODE_XML_EXPORT_NAME ),
                     readFromFile( "node_unordered.xml" ).getBytes() );
    }

    private void createContentFolder( final Path exportPath )
        throws Exception
    {
        final Path folderDir = Files.createDirectories( Paths.get( temporaryFolder.getRoot().getPath(), exportPath.toString() ) );
        assertNotNull( folderDir );
    }

    private String readFromFile( final String fileName )
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
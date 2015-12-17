package com.enonic.xp.core.impl.export;

import java.net.URL;

import com.google.common.io.ByteSource;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentNotFoundException;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.content.CreateContentParams;
import com.enonic.xp.content.CreateMediaParams;
import com.enonic.xp.content.UpdateMediaParams;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.export.ContentImportResult;
import com.enonic.xp.export.ImportNodeException;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.vfs.VirtualFile;
import com.enonic.xp.vfs.VirtualFilePath;

import static com.enonic.xp.core.impl.export.ExportConstants.SYSTEM_FOLDER_NAME;

public final class ContentImporter
{
    private final ContentPath importRoot;

    private final ContentService contentService;

    private final VirtualFile exportRoot;

    private final ContentImportResult.Builder result = ContentImportResult.create();

    private ContentImporter( final Builder builder )
    {
        this.contentService = builder.contentService;
        this.exportRoot = builder.exportRoot;
        this.importRoot = builder.importRoot;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public ContentImportResult execute()
    {
        verifyImportRoot();

        importFromDirectory( this.exportRoot );

        return this.result.build();
    }

    private void verifyImportRoot()
    {
        try
        {
            contentService.getByPath( this.importRoot );
        }
        catch ( ContentNotFoundException e )
        {
            throw new ImportNodeException( "Import root '" + this.importRoot + "' not found" );
        }
    }

    private String getLastBitFromUrl( final URL url )
    {
        return url.getPath().replaceFirst( ".*/([^/?]+).*", "$1" );
    }

    private void importFromDirectory( final VirtualFile parentFolder )
    {
        parentFolder.getChildren().stream().
            filter( ( folder ) -> !getLastBitFromUrl( folder.getUrl() ).equals( SYSTEM_FOLDER_NAME ) ).
            forEach( ( file ) -> {
                if ( file.isFolder() )
                {
                    this.processFolder( file );
                }
                else
                {
                    this.importMedia( file );
                }
            } );
    }

    private void importMedia( final VirtualFile mediaFile )
    {
        try
        {
            final ContentPath parent = resolveContentImportPath( mediaFile.getPath() ).getParentPath();

            final String name = mediaFile.getName();
            final ByteSource mediaSource = mediaFile.getByteSource();
            if ( !contentService.contentExists( ContentPath.from( parent, name ) ) )
            {
                createMedia( parent, name, mediaSource );
                this.result.added( ContentPath.from( parent, name ) );
            }
            else
            {
                updateMedia( parent, name, mediaSource );
                this.result.updated( ContentPath.from( parent, name ) );
            }
        }
        catch ( Exception e )
        {
            result.addError( "Could not import media file in [" + mediaFile.getPath().getPath() + "]: " + e.getMessage(), e );
        }
    }

    private void processFolder( final VirtualFile contentFolder )
    {
        try
        {
            if ( !hasSystemFolder( contentFolder ) )
            {
                createFolderContent( contentFolder );
            }
            importFromDirectory( contentFolder );
        }
        catch ( Exception e )
        {
            result.addError( "Could not import content in folder [" + contentFolder.getPath().getPath() + "]: " + e.getMessage(), e );
        }
    }

    private void createFolderContent( final VirtualFile contentFolder )
    {
        final ContentPath parent = resolveContentImportPath( contentFolder.getPath() ).getParentPath();
        final String name = contentFolder.getName();

        if ( contentService.contentExists( ContentPath.from( parent, name ) ) )
        {
            return;
        }

        final CreateContentParams createContentParams = CreateContentParams.create().
            name( name ).
            displayName( name ).
            parent( parent ).
            inheritPermissions( true ).
            type( ContentTypeName.folder() ).
            contentData( new PropertyTree() ).
            build();
        contentService.create( createContentParams );

        this.result.added( ContentPath.from( parent, name ) );
    }

    private void createMedia( final ContentPath parent, final String name, final ByteSource mediaSource )
    {
        final CreateMediaParams createMediaParams = new CreateMediaParams().
            name( name ).
            parent( parent ).
            byteSource( mediaSource );
        contentService.create( createMediaParams );
    }

    private void updateMedia( final ContentPath parent, final String name, final ByteSource mediaSource )
    {
        final Content existing = contentService.getByPath( ContentPath.from( parent, name ) );

        final UpdateMediaParams updateMediaParams = new UpdateMediaParams().
            name( name ).
            content( existing.getId() ).
            byteSource( mediaSource );
        contentService.update( updateMediaParams );
    }

    private boolean hasSystemFolder( final VirtualFile contentFolder )
    {
        return contentFolder.getChildren().stream().
            anyMatch( ( file -> file.getName().equals( SYSTEM_FOLDER_NAME ) ) );
    }

    private ContentPath resolveContentImportPath( final VirtualFilePath filePath )
    {
        final VirtualFilePath relativePath = filePath.subtractPath( this.exportRoot.getPath() );
        final ContentPath relativeContentPath = ContentPath.create().elements( relativePath.getElements() ).build();
        return ContentPath.from( this.importRoot, relativeContentPath );
    }

    public static final class Builder
    {
        private ContentPath importRoot;

        private ContentService contentService;

        private VirtualFile exportRoot;

        private Builder()
        {
        }

        public Builder targetPath( ContentPath contentPath )
        {
            this.importRoot = contentPath;
            return this;
        }

        public Builder sourceDirectory( VirtualFile exportRoot )
        {
            this.exportRoot = exportRoot;
            return this;
        }

        public Builder contentService( ContentService contentService )
        {
            this.contentService = contentService;
            return this;
        }

        public ContentImporter build()
        {
            return new ContentImporter( this );
        }
    }

}

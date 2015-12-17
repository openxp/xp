package com.enonic.xp.core.impl.export;

import org.apache.commons.lang.StringUtils;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.export.ContentImportParams;
import com.enonic.xp.export.ContentImportResult;
import com.enonic.xp.export.ContentImportService;
import com.enonic.xp.export.NodeImportResult;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeService;
import com.enonic.xp.repository.RepositoryId;

import static com.enonic.xp.content.ContentConstants.CONTENT_ROOT_NAME;

@Component(immediate = true)
public final class ContentImportServiceImpl
    implements ContentImportService
{
    private ContentService contentService;

    private NodeService nodeService;

    @Override
    public ContentImportResult importContent( final ContentImportParams params )
    {
        return getContext( params.getTargetBranch() ).callWith( () -> doImportContent( params ) );
    }

    private ContentImportResult doImportContent( final ContentImportParams params )
    {
        final NodeImportResult nodeResult = importNodes( params );

        ContentImportResult contentResult = ContentImporter.create().
            contentService( this.contentService ).
            sourceDirectory( params.getSource() ).
            targetPath( params.getTargetPath() ).
            build().
            execute();
        contentResult = addNodeResults( contentResult, nodeResult );

        return contentResult;
    }

    private NodeImportResult importNodes( final ContentImportParams params )
    {
        final NodePath targetNodePath = toNodePath( params.getTargetPath() );

        return NodeImporter.create().
            nodeService( this.nodeService ).
            sourceDirectory( params.getSource() ).
            targetNodePath( targetNodePath ).
            dryRun( false ).
            importNodeIds( true ).
            importPermissions( true ).
            ignoreMissingNodeSource().
            build().
            execute();
    }

    private Context getContext( final Branch branch )
    {
        final Context current = ContextAccessor.current();
        final RepositoryId contentRepo = ContentConstants.CONTENT_REPO.getId();
        return ContextBuilder.from( current ).
            repositoryId( contentRepo ).
            branch( branch ).
            build();
    }

    private ContentImportResult addNodeResults( final ContentImportResult contentResult, final NodeImportResult nodeResult )
    {
        final ContentImportResult.Builder result = ContentImportResult.copyOf( contentResult );
        for ( NodePath nodePath : nodeResult.getAddedNodes() )
        {
            result.added( toContentPath( nodePath ) );
        }
        for ( NodePath nodePath : nodeResult.getUpdateNodes() )
        {
            result.updated( toContentPath( nodePath ) );
        }
        for ( NodeImportResult.ImportError importError : nodeResult.getImportErrors() )
        {
            result.addError( importError.getException(), importError.getMessage() );
        }
        return result.build();
    }

    private NodePath toNodePath( final ContentPath contentPath )
    {
        return new NodePath( CONTENT_ROOT_NAME + contentPath.asAbsolute().toString() ).asAbsolute().trimTrailingDivider();
    }

    private ContentPath toContentPath( final NodePath nodePath )
    {
        final String contentPath = StringUtils.substringAfter( nodePath.asAbsolute().toString(), CONTENT_ROOT_NAME + "/" );
        return ContentPath.from( contentPath ).asAbsolute();
    }

    @Reference
    public void setContentService( final ContentService contentService )
    {
        this.contentService = contentService;
    }

    @Reference
    public void setNodeService( final NodeService nodeService )
    {
        this.nodeService = nodeService;
    }
}

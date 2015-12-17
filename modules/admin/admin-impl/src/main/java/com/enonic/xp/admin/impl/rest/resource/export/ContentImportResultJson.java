package com.enonic.xp.admin.impl.rest.resource.export;

import java.util.List;

import com.google.common.collect.Lists;

import com.enonic.xp.content.ContentPath;
import com.enonic.xp.export.ContentImportResult;

public class ContentImportResultJson
{
    private List<String> addedNodes = Lists.newArrayList();

    private List<String> updateNodes = Lists.newArrayList();

    private List<String> importErrors = Lists.newArrayList();

    public static ContentImportResultJson from( final ContentImportResult result )
    {
        final ContentImportResultJson json = new ContentImportResultJson();

        for ( final ContentPath nodePath : result.getAddedContent() )
        {
            json.addedNodes.add( nodePath.toString() );
        }

        for ( final ContentPath nodePath : result.getUpdatedContent() )
        {
            json.updateNodes.add( nodePath.toString() );
        }

        for ( final ContentImportResult.ImportError importError : result.getImportErrors() )
        {
            json.importErrors.add( importError.getMessage() + " - " + importError.getException() );
        }

        return json;
    }

    @SuppressWarnings("UnusedDeclaration")
    public List<String> getAddedNodes()
    {
        return addedNodes;
    }

    @SuppressWarnings("UnusedDeclaration")
    public List<String> getUpdateNodes()
    {
        return updateNodes;
    }

    @SuppressWarnings("UnusedDeclaration")
    public List<String> getImportErrors()
    {
        return importErrors;
    }
}

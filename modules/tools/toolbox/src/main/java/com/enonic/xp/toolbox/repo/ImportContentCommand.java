package com.enonic.xp.toolbox.repo;

import com.fasterxml.jackson.databind.node.ObjectNode;

import io.airlift.airline.Command;
import io.airlift.airline.Option;

import com.enonic.xp.toolbox.util.JsonHelper;

@Command(name = "importContent", description = "Import content from a named export.")
public final class ImportContentCommand
    extends RepoCommand
{
    public static final String IMPORT_REST_PATH = "/admin/rest/export/importContent";

    @Option(name = "-t", description = "Target path for import. Format: <branch-name>:<content-path>. e.g 'draft:/'", required = true)
    public String targetBranchPath;

    @Option(name = "-s", description = "A named export to import.", required = true)
    public String exportName;

    @Override
    protected void execute()
        throws Exception
    {
        final String result = postRequest( IMPORT_REST_PATH, createJsonRequest() );
        System.out.println( result );
    }

    private ObjectNode createJsonRequest()
    {
        final ObjectNode json = JsonHelper.newObjectNode();
        json.put( "exportName", this.exportName );
        json.put( "targetBranchPath", this.targetBranchPath );
        return json;
    }
}

package com.enonic.xp.admin.impl.rest.resource.export;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Preconditions;

public class ImportContentRequestJson
{
    private final BranchContentPath targetBranchPath;

    private final String exportName;

    @JsonCreator
    public ImportContentRequestJson( @JsonProperty("exportName") final String exportName,
                                     @JsonProperty("targetBranchPath") final String targetBranchPath )
    {

        Preconditions.checkNotNull( exportName, "exportName not specified" );
        Preconditions.checkNotNull( targetBranchPath, "targetBranchPath not specified" );

        this.targetBranchPath = BranchContentPath.from( targetBranchPath );
        this.exportName = exportName;
    }

    public BranchContentPath getTargetBranchPath()
    {
        return targetBranchPath;
    }

    public String getExportName()
    {
        return exportName;
    }

}

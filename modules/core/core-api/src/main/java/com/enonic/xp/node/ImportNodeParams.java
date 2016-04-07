package com.enonic.xp.node;

public class ImportNodeParams
{
    private final CreateBinaries createBinaries;

    private final Node node;

    private final InsertManualStrategy insertManualStrategy;

    private final boolean dryRun;

    private final boolean importPermissions;

    private ImportNodeParams( Builder builder )
    {
        createBinaries = builder.createBinaries;
        node = builder.node;
        insertManualStrategy = builder.insertManualStrategy;
        dryRun = builder.dryRun;
        importPermissions = builder.importPermissions;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public Node getNode()
    {
        return node;
    }

    public InsertManualStrategy getInsertManualStrategy()
    {
        return insertManualStrategy;
    }

    public CreateBinaries getCreateBinaries()
    {
        return createBinaries;
    }

    public boolean isDryRun()
    {
        return dryRun;
    }

    public boolean isImportPermissions()
    {
        return importPermissions;
    }

    public static final class Builder
    {
        private CreateBinaries createBinaries;

        private Node node;

        private InsertManualStrategy insertManualStrategy;

        private boolean dryRun;

        private boolean importPermissions;

        private Builder()
        {
        }

        public Builder binaryAttachments( CreateBinaries createBinaries )
        {
            this.createBinaries = createBinaries;
            return this;
        }

        public Builder importNode( Node node )
        {
            this.node = node;
            return this;
        }

        public Builder insertManualStrategy( InsertManualStrategy insertManualStrategy )
        {
            this.insertManualStrategy = insertManualStrategy;
            return this;
        }

        public Builder dryRun( boolean dryRun )
        {
            this.dryRun = dryRun;
            return this;
        }

        public Builder importPermissions( boolean importPermissions )
        {
            this.importPermissions = importPermissions;
            return this;
        }

        public ImportNodeParams build()
        {
            return new ImportNodeParams( this );
        }
    }
}

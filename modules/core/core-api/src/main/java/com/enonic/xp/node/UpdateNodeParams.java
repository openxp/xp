package com.enonic.xp.node;

import java.util.Set;

import com.google.common.annotations.Beta;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.google.common.io.ByteSource;

import com.enonic.xp.util.BinaryReference;

@Beta
public class UpdateNodeParams
{
    private final NodeId id;

    private final NodeEditor editor;

    private final CreateBinaries createBinaries;

    private final boolean dryRun;

    private UpdateNodeParams( final Builder builder )
    {
        this.id = builder.id;
        this.editor = builder.editor;
        this.createBinaries = new CreateBinaries( ImmutableSet.copyOf( builder.createBinaries ) );
        this.dryRun = builder.dryRun;
    }

    public CreateBinaries getCreateBinaries()
    {
        return createBinaries;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public NodeId getId()
    {
        return id;
    }

    public NodeEditor getEditor()
    {
        return editor;
    }

    public boolean isDryRun()
    {
        return dryRun;
    }

    public static final class Builder
    {
        private NodeId id;

        private NodeEditor editor;

        private Set<CreateBinary> createBinaries = Sets.newHashSet();

        private boolean dryRun = false;

        private Builder()
        {
        }

        public Builder id( final NodeId id )
        {
            this.id = id;
            return this;
        }

        public Builder editor( final NodeEditor editor )
        {
            this.editor = editor;
            return this;
        }

        public Builder attachBinary( final BinaryReference binaryReference, final ByteSource byteSource )
        {
            this.createBinaries.add( new CreateBinary( binaryReference, byteSource ) );
            return this;
        }

        public Builder setCreateBinaries( final CreateBinaries createBinaries )
        {
            this.createBinaries = createBinaries != null ? createBinaries.getSet() : Sets.newHashSet();
            return this;
        }

        public Builder dryRun( final boolean dryRun )
        {
            this.dryRun = dryRun;
            return this;
        }

        public UpdateNodeParams build()
        {
            this.validate();
            return new UpdateNodeParams( this );
        }

        private void validate()
        {
            Preconditions.checkNotNull( this.id, "id cannot be null" );
            Preconditions.checkNotNull( this.editor, "editor cannot be null" );
        }
    }
}

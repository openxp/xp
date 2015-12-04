package com.enonic.xp.repo.impl.node;

import com.google.common.base.Preconditions;

import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.relationship.Relationships;
import com.enonic.xp.repo.impl.InternalContext;

public class GetRelationshipsCommand
    extends AbstractNodeCommand
{
    private final NodeId source;

    private GetRelationshipsCommand( final Builder builder )
    {

        super( builder );
        this.source = builder.source;
    }

    public Relationships execute()
    {
        final Context context = ContextAccessor.current();

        return this.storageService.getRelationships( source, InternalContext.from( context ) );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
        extends AbstractNodeCommand.Builder<Builder>
    {
        private NodeId source;

        private Builder()
        {
        }

        public Builder nodeId( final NodeId source )
        {
            this.source = source;
            return this;
        }

        @Override
        void validate()
        {
            super.validate();
            Preconditions.checkNotNull( this.source );
        }

        public GetRelationshipsCommand build()
        {
            this.validate();
            return new GetRelationshipsCommand( this );
        }
    }
}
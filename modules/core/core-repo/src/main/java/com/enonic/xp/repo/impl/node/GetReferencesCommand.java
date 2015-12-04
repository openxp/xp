package com.enonic.xp.repo.impl.node;

import com.google.common.base.Preconditions;

import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.relationship.Relationships;
import com.enonic.xp.repo.impl.InternalContext;

public class GetReferencesCommand
    extends AbstractNodeCommand
{
    private final NodeId target;

    private GetReferencesCommand( final Builder builder )
    {

        super( builder );
        this.target = builder.target;
    }

    public Relationships execute()
    {
        final Context context = ContextAccessor.current();

        return this.storageService.getReferences( target, InternalContext.from( context ) );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
        extends AbstractNodeCommand.Builder<Builder>
    {
        private NodeId target;

        private Builder()
        {
        }

        public Builder nodeId( final NodeId target )
        {
            this.target = target;
            return this;
        }

        @Override
        void validate()
        {
            super.validate();
            Preconditions.checkNotNull( this.target );
        }

        public GetReferencesCommand build()
        {
            this.validate();
            return new GetReferencesCommand( this );
        }
    }
}
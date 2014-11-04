package com.enonic.wem.core.entity;

import com.google.common.base.Preconditions;

public class DuplicateNodeCommand
    extends AbstractFindNodeCommand
{
    private NodeId nodeId;

    private DuplicateNodeCommand( final Builder builder )
    {
        super( builder );
        this.nodeId = builder.id;
    }

    public Node execute()
    {
        final Node existingNode = doGetById( nodeId, false );

        final String newNodeName = resolveNewNodeName( existingNode );

        final CreateNodeParams builder = CreateNodeParams.create().
            name( newNodeName ).
            parent( existingNode.parent() ).
            data( existingNode.data() ).
            attachments( existingNode.attachments() ).
            indexConfigDocument( existingNode.getIndexConfigDocument() ).
            childOrder( existingNode.getChildOrder() ).
            build();

        final Node duplicatedNode = doCreateNode( builder );

        storeChildNodes( existingNode, duplicatedNode );

        return duplicatedNode;
    }

    private void storeChildNodes( final Node originalParent, final Node newParent )
    {

        //TODO: Fix size
        final FindNodesByParentResult findNodesByParentResult = doFindNodesByParent( FindNodesByParentParams.create().
            parentPath( originalParent.path() ).
            from( 0 ).
            size( 100 ).
            build() );

        for ( final Node node : findNodesByParentResult.getNodes() )
        {
            final Node newChildNode = this.doCreateNode( CreateNodeParams.create().
                childOrder( node.getChildOrder() ).
                attachments( node.attachments() ).
                data( node.data() ).
                name( node.name().toString() ).
                indexConfigDocument( node.getIndexConfigDocument() ).
                parent( newParent.path() ).
                build() );

            storeChildNodes( node, newChildNode );
        }
    }

    private String resolveNewNodeName( final Node existingNode )
    {
        String newNodeName = DuplicateValueResolver.name( existingNode.name() );

        boolean resolvedUnique = false;

        while ( !resolvedUnique )
        {
            final NodePath checkIfExistsPath = NodePath.newNodePath( existingNode.parent(), newNodeName ).build();
            Node foundNode = this.doGetByPath( checkIfExistsPath, false );

            if ( foundNode == null )
            {
                resolvedUnique = true;
            }
            else
            {
                newNodeName = DuplicateValueResolver.name( newNodeName );
            }
        }

        return newNodeName;
    }

    public static Builder create()
    {
        return new Builder();
    }


    public static class Builder
        extends AbstractFindNodeCommand.Builder<Builder>
    {
        private NodeId id;

        Builder()
        {
            super();
        }

        public Builder id( final NodeId nodeId )
        {
            this.id = nodeId;
            return this;
        }

        public DuplicateNodeCommand build()
        {
            validate();
            return new DuplicateNodeCommand( this );
        }

        void validate()
        {
            Preconditions.checkNotNull( id );
        }
    }

}
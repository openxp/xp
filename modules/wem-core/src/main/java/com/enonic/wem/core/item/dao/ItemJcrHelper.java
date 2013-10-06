package com.enonic.wem.core.item.dao;


import javax.jcr.ItemExistsException;
import javax.jcr.ItemNotFoundException;
import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import com.enonic.wem.api.item.Item;
import com.enonic.wem.api.item.ItemAlreadyExist;
import com.enonic.wem.api.item.ItemId;
import com.enonic.wem.api.item.ItemPath;
import com.enonic.wem.api.item.NoItemAtPathFound;
import com.enonic.wem.api.item.NoItemWithIdFound;
import com.enonic.wem.core.jcr.JcrConstants;

class ItemJcrHelper
{
    private static final String ITEMS_NODE = "items";

    static final String ITEMS_PATH = JcrConstants.ROOT_NODE + "/" + ITEMS_NODE + "/";

    private final Session session;

    ItemJcrHelper( final Session session )
    {
        this.session = session;
    }

    private Node itemsNode()
    {
        try
        {
            final Node root = session.getRootNode();
            return root.getNode( ITEMS_PATH );
        }
        catch ( RepositoryException e )
        {
            throw new RuntimeException( "Failed to get itemsNode", e );
        }
    }

    void ensurePath( final ItemPath path )
    {
        try
        {
            Node parentNode = itemsNode();
            for ( int i = 0; i < path.elementCount(); i++ )
            {
                final String pathElement = path.getElementAsString( i );
                if ( !parentNode.hasNode( pathElement ) )
                {
                    parentNode = parentNode.addNode( pathElement );
                }
                else
                {
                    parentNode = parentNode.getNode( pathElement );
                }
            }

        }
        catch ( RepositoryException e )
        {
            throw new RuntimeException( "Failed to ensurePath: " + path, e );
        }
    }

    Item persistNewItem( final Item item, final Node parentItemNode )
    {
        try
        {
            final Node newItemNode = parentItemNode.addNode( item.name(), JcrConstants.ITEM_NODETYPE );
            updateItemNode( newItemNode, item );
            return ItemJcrMapper.toItem( newItemNode ).build();
        }
        catch ( ItemExistsException e )
        {
            try
            {
                final Node existingItemNode = parentItemNode.getNode( item.name() );
                final Item existingItem = ItemJcrMapper.toItem( existingItemNode ).build();
                throw new ItemAlreadyExist( existingItem.path() );
            }
            catch ( RepositoryException e1 )
            {
                throw new RuntimeException( "Failed to createChild", e );
            }
        }
        catch ( RepositoryException e )
        {
            throw new RuntimeException( "Failed to createChild", e );
        }
    }

    Node getItemNodeByPath( final ItemPath path )
        throws NoItemAtPathFound
    {
        try
        {
            return itemsNode().getNode( path.asRelative().toString() );
        }
        catch ( PathNotFoundException e )
        {
            throw new NoItemAtPathFound( path );
        }
        catch ( RepositoryException e )
        {
            throw new RuntimeException( "Failed to getItemNodeByPath", e );
        }
    }

    Item updateItemNode( final Node itemNode, final Item item )
    {
        try
        {
            ItemJcrMapper.toJcr( item, itemNode );
            return ItemJcrMapper.toItem( itemNode ).build();
        }
        catch ( RepositoryException e )
        {
            throw new RuntimeException( "Failed to updateItemNode", e );
        }
    }

    Item updateItemNode( final Node existingItemNode, final UpdateItemArgs updateItemArgs )
    {
        try
        {
            ItemJcrMapper.updateItemNode( updateItemArgs, existingItemNode );
            return ItemJcrMapper.toItem( existingItemNode ).build();
        }
        catch ( RepositoryException e )
        {
            throw new RuntimeException( "Failed to updateItemNode", e );
        }
    }

    Node getItemNodeById( final ItemId id )
        throws NoItemWithIdFound
    {
        try
        {
            return session.getNodeByIdentifier( id.toString() );
        }
        catch ( ItemNotFoundException e )
        {
            throw new NoItemWithIdFound( id );
        }
        catch ( RepositoryException e )
        {
            throw new RuntimeException( "Failed to getItemById", e );
        }
    }

    Item getItemById( final ItemId id )
        throws NoItemWithIdFound
    {
        try
        {
            final Node itemNode = session.getNodeByIdentifier( id.toString() );
            return ItemJcrMapper.toItem( itemNode ).build();
        }
        catch ( ItemNotFoundException e )
        {
            throw new NoItemWithIdFound( id );
        }
        catch ( RepositoryException e )
        {
            throw new RuntimeException( "Failed to getItemById", e );
        }
    }

    Item getItemByPath( final ItemPath path )
        throws NoItemAtPathFound
    {
        final Node itemNode = getItemNodeByPath( path );
        return ItemJcrMapper.toItem( itemNode ).build();
    }


}

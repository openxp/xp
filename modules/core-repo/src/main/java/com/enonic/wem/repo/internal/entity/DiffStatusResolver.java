package com.enonic.wem.repo.internal.entity;

import com.enonic.wem.api.content.CompareStatus;
import com.enonic.wem.api.node.NodeVersion;

class DiffStatusResolver
{
    static CompareStatus resolve( final DiffStatusParams diffStatusParams )
    {
        final NodeVersion targetBranch = diffStatusParams.getTarget();
        final NodeVersion sourceBranch = diffStatusParams.getSource();

        if ( targetBranch == null )
        {
            return new CompareStatus( CompareStatus.Status.NEW );
        }

        if ( sourceBranch == null )
        {
            return new CompareStatus( CompareStatus.Status.DELETED );
        }

        if ( targetBranch.equals( sourceBranch ) )
        {
            return new CompareStatus( CompareStatus.Status.EQUAL );
        }

        if ( sourceBranch.getTimestamp().isAfter( targetBranch.getTimestamp() ) )
        {
            return new CompareStatus( CompareStatus.Status.NEWER );
        }

        if ( sourceBranch.getTimestamp().isBefore( targetBranch.getTimestamp() ) )
        {
            return new CompareStatus( CompareStatus.Status.OLDER );
        }

        throw new RuntimeException( "Not able to resolve compare status" );
    }

}
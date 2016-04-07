package com.enonic.xp.node;

import java.util.Set;

import com.google.common.annotations.Beta;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import com.enonic.xp.support.AbstractImmutableEntitySet;
import com.enonic.xp.util.BinaryReference;

@Beta
public class CreateBinaries
    extends AbstractImmutableEntitySet<CreateBinary>
{
    public CreateBinaries( final ImmutableSet<CreateBinary> set )
    {
        super( set );
    }

    public CreateBinary get( final BinaryReference binaryReference )
    {
        for ( final CreateBinary createBinary : this.set )
        {
            if ( createBinary.getReference().equals( binaryReference ) )
            {
                return createBinary;
            }
        }

        return null;
    }

    private CreateBinaries( final Set<CreateBinary> set )
    {
        super( ImmutableSet.copyOf( set ) );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static CreateBinaries empty()
    {
        final Set<CreateBinary> returnFields = Sets.newHashSet();
        return new CreateBinaries( returnFields );
    }

    public static final class Builder
    {
        private Set<CreateBinary> createBinaries = Sets.newHashSet();

        public Builder add( final CreateBinary createBinary )
        {
            this.createBinaries.add( createBinary );
            return this;
        }

        public CreateBinaries build()
        {
            return new CreateBinaries( ImmutableSet.copyOf( this.createBinaries ) );
        }
    }
}

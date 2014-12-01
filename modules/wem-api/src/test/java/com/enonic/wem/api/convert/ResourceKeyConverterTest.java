package com.enonic.wem.api.convert;

import org.junit.Test;

import com.enonic.wem.api.convert.ConvertException;
import com.enonic.wem.api.convert.Converters;
import com.enonic.wem.api.resource.ResourceKey;

import static org.junit.Assert.*;

public class ResourceKeyConverterTest
{
    @Test
    public void testSameType()
    {
        final ResourceKey key = ResourceKey.from( "mymodule:/some/path" );
        assertSame( key, Converters.convert( key, ResourceKey.class ) );
    }

    @Test
    public void testToString()
    {
        assertEquals( ResourceKey.from( "mymodule:/some/path" ), Converters.convert( "mymodule:/some/path", ResourceKey.class ) );
    }

    @Test(expected = ConvertException.class)
    public void testFailure()
    {
        Converters.convert( "/some/path", ResourceKey.class );
    }
}
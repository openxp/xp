package com.enonic.wem.script.internal.serializer;

import jdk.nashorn.internal.objects.Global;
import jdk.nashorn.internal.objects.NativeArray;
import jdk.nashorn.internal.runtime.ScriptObject;

import com.enonic.wem.script.serializer.MapGeneratorBase2;

public final class ScriptMapGenerator2
    extends MapGeneratorBase2
{
    @Override
    protected Object newObject()
    {
        return Global.newEmptyInstance();
    }

    @Override
    protected Object newArray()
    {
        return Global.allocate( new Object[0] );
    }

    @Override
    protected boolean isObject( final Object value )
    {
        return !isArray( value );
    }

    @Override
    protected boolean isArray( final Object value )
    {
        return ( (ScriptObject) value ).isArray();
    }

    @Override
    protected void putInObject( final Object map, final String key, final Object value )
    {
        ( (ScriptObject) map ).put( key, value, false );
    }

    @Override
    protected void addToArray( final Object array, final Object value )
    {
        NativeArray.push( array, value );
    }
}

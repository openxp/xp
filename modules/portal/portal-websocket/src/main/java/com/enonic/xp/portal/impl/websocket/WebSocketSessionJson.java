package com.enonic.xp.portal.impl.websocket;

import java.util.List;
import java.util.Map;

import javax.websocket.Session;

import com.enonic.xp.script.serializer.MapGenerator;
import com.enonic.xp.script.serializer.MapSerializable;

public final class WebSocketSessionJson
    implements MapSerializable
{
    protected WebSocketLocation location;

    protected Session session;

    @Override
    public void serialize( final MapGenerator gen )
    {
        gen.value( "id", this.session.getId() );
        gen.value( "branch", this.location.getBranch().toString() );
        gen.value( "path", this.session.getRequestURI().getPath() );
        gen.value( "name", this.location.getName() );
        serializeMultimap( "params", gen, this.session.getRequestParameterMap() );
    }

    private void serializeMultimap( final String name, final MapGenerator gen, final Map<String, List<String>> params )
    {
        gen.map( name );
        for ( final String key : params.keySet() )
        {
            final List<String> values = params.get( key );
            if ( values.size() == 1 )
            {
                gen.value( key, values.iterator().next() );
            }
            else
            {
                gen.array( key );
                values.forEach( gen::value );
                gen.end();
            }
        }
        gen.end();
    }
}

package com.enonic.xp.portal.impl.websocket;

import javax.websocket.CloseReason;
import javax.websocket.Endpoint;
import javax.websocket.EndpointConfig;
import javax.websocket.Session;

final class WebSocketEndpoint
    extends Endpoint
{
    private final WebSocketSessionMap map;

    public WebSocketEndpoint( final WebSocketSessionMap map )
    {
        this.map = map;
    }

    @Override
    public void onOpen( final Session session, final EndpointConfig config )
    {
        final WebSocketSession result = this.map.create( session );
        result.onOpen();
    }

    @Override
    public void onClose( final Session session, final CloseReason closeReason )
    {
        final WebSocketSession result = this.map.remove( session );
        if ( result != null )
        {
            result.onClose( closeReason );
        }
    }

    @Override
    public void onError( final Session session, final Throwable cause )
    {
        final WebSocketSession result = this.map.get( session );
        if ( result != null )
        {
            result.onError( cause );
        }
    }
}

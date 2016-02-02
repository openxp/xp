package com.enonic.xp.portal.impl.websocket;

import java.util.Map;

import javax.websocket.Session;

import com.google.common.collect.Maps;

import com.enonic.xp.portal.script.PortalScriptService;
import com.enonic.xp.portal.websocket.WebSocketMessage;

final class WebSocketSessionMap
{
    private final Map<Session, WebSocketSession> map;

    private PortalScriptService portalScriptService;

    public WebSocketSessionMap()
    {
        this.map = Maps.newConcurrentMap();
    }

    public WebSocketSession create( final Session session )
    {
        final WebSocketSession result = new WebSocketSession( session, this.portalScriptService );
        this.map.put( session, result );
        return result;
    }

    public WebSocketSession get( final Session session )
    {
        return this.map.get( session );
    }

    public WebSocketSession remove( final Session session )
    {
        return this.map.remove( session );
    }

    public void sendMessage( final WebSocketMessage message )
    {
        this.map.values().stream().forEach( it -> it.sendMessage( message ) );
    }

    public void setPortalScriptService( final PortalScriptService portalScriptService )
    {
        this.portalScriptService = portalScriptService;
    }
}

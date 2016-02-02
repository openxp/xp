package com.enonic.xp.portal.impl.websocket;

import javax.servlet.http.HttpServletRequest;
import javax.websocket.Endpoint;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.portal.script.PortalScriptService;
import com.enonic.xp.portal.websocket.WebSocketMessage;
import com.enonic.xp.portal.websocket.WebSocketService;
import com.enonic.xp.web.websocket.BaseWebSocketHandler;
import com.enonic.xp.web.websocket.WebSocketHandler;

@Component(immediate = true, service = {WebSocketHandler.class, WebSocketService.class})
public final class PortalWebSocket
    extends BaseWebSocketHandler
    implements WebSocketService
{
    private final WebSocketSessionMap map;

    private final WebSocketEndpoint endpoint;

    public PortalWebSocket()
    {
        this.map = new WebSocketSessionMap();
        this.endpoint = new WebSocketEndpoint( this.map );
        setSubProtocols( "text" );
    }

    @Override
    public boolean canHandle( final HttpServletRequest req )
    {
        final String uri = req.getRequestURI();
        return WebSocketLocation.parse( uri ) != null;
    }

    @Override
    public Endpoint newEndpoint()
    {
        return this.endpoint;
    }

    @Override
    public void sendMessage( final WebSocketMessage message )
    {
        this.map.sendMessage( message );
    }

    @Reference
    public void setPortalScriptService( final PortalScriptService portalScriptService )
    {
        this.map.setPortalScriptService( portalScriptService );
    }
}

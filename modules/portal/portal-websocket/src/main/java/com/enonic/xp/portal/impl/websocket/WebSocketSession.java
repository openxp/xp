package com.enonic.xp.portal.impl.websocket;

import java.util.Objects;

import javax.websocket.CloseReason;
import javax.websocket.MessageHandler;
import javax.websocket.Session;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.portal.script.PortalScriptService;
import com.enonic.xp.portal.websocket.WebSocketMessage;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.resource.ResourceNotFoundException;
import com.enonic.xp.script.ScriptExports;

final class WebSocketSession
    implements MessageHandler.Whole<String>
{
    private final Session session;

    private final WebSocketLocation location;

    private final PortalScriptService portalScriptService;

    private final ResourceKey resourceKey;

    public WebSocketSession( final Session session, final PortalScriptService portalScriptService )
    {
        this.session = session;
        this.location = WebSocketLocation.parse( this.session.getRequestURI().getPath() );
        this.portalScriptService = portalScriptService;

        if ( this.location != null )
        {
            this.resourceKey = ResourceKey.from( this.location.getApplication(), "/ws/" + this.location.getName() + ".js" );
        }
        else
        {
            this.resourceKey = null;
        }
    }

    private WebSocketSessionJson toSessionJson()
    {
        final WebSocketSessionJson json = new WebSocketSessionJson();
        json.location = this.location;
        json.session = this.session;
        return json;
    }

    public void onOpen()
    {
        this.session.addMessageHandler( this );
        executeMethod( "open", toSessionJson() );
    }

    public void onClose( final CloseReason reason )
    {
        executeMethod( "close", toSessionJson(), reason.getCloseCode().getCode() );
    }

    public void onError( final Throwable cause )
    {
        executeMethod( "error", toSessionJson(), cause.getMessage() );
    }

    private void executeMethod( final String name, final Object... args )
    {
        if ( this.resourceKey == null )
        {
            return;
        }

        try
        {
            final ScriptExports exports = this.portalScriptService.execute( this.resourceKey );
            if ( !exports.hasMethod( name ) )
            {
                return;
            }

            exports.executeMethod( name, args );
        }
        catch ( final ResourceNotFoundException e )
        {
            // Do nothing
        }
    }

    public void sendMessage( final WebSocketMessage message )
    {
        if ( canSend( message ) )
        {
            sendMessage( message.getMessage() );
        }
    }

    private boolean canSend( final WebSocketMessage message )
    {
        final String id = message.getId();
        if ( id != null )
        {
            return id.equals( this.session.getId() );
        }

        final ApplicationKey app = message.getApplication();
        if ( !Objects.equals( app, this.location.getApplication() ) )
        {
            return false;
        }

        final String name = message.getName();
        return Objects.equals( name, this.location.getName() );
    }

    private void sendMessage( final String message )
    {
        try
        {
            this.session.getBasicRemote().sendText( message );
        }
        catch ( final Exception e )
        {
            // Do nothing
        }
    }

    @Override
    public void onMessage( final String message )
    {
        executeMethod( "message", toSessionJson(), message );
    }
}

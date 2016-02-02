package com.enonic.xp.lib.portal.websocket;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.portal.websocket.WebSocketMessage;
import com.enonic.xp.portal.websocket.WebSocketService;
import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;

public final class SendMessageHandler
    implements ScriptBean
{
    private final WebSocketMessage.Builder message;

    private WebSocketService webSocketService;

    public SendMessageHandler()
    {
        this.message = new WebSocketMessage.Builder();
    }

    public void setId( final String id )
    {
        this.message.id( id );
    }

    public void setApplication( final String application )
    {
        if ( application != null )
        {
            this.message.application( ApplicationKey.from( application ) );
        }
    }

    public void setName( final String name )
    {
        this.message.name( name );
    }

    public void setMessage( final String message )
    {
        this.message.message( message );
    }

    public void send()
    {
        this.webSocketService.sendMessage( this.message.build() );
    }

    @Override
    public void initialize( final BeanContext context )
    {
        this.webSocketService = context.getService( WebSocketService.class ).get();
        this.message.application( context.getApplicationKey() );
    }
}

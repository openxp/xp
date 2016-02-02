package com.enonic.xp.portal.websocket;

import com.enonic.xp.app.ApplicationKey;

public final class WebSocketMessage
{
    private final ApplicationKey application;

    private final String name;

    private final String id;

    private final String message;

    private WebSocketMessage( final Builder builder )
    {
        this.application = builder.application;
        this.name = builder.name;
        this.id = builder.id;
        this.message = builder.message;
    }

    public ApplicationKey getApplication()
    {
        return this.application;
    }

    public String getName()
    {
        return this.name;
    }

    public String getId()
    {
        return this.id;
    }

    public String getMessage()
    {
        return this.message;
    }

    public final static class Builder
    {
        private ApplicationKey application;

        private String name;

        private String id;

        private String message;

        public Builder application( final ApplicationKey key )
        {
            this.application = key;
            return this;
        }

        public Builder name( final String name )
        {
            this.name = name;
            return this;
        }

        public Builder id( final String id )
        {
            this.id = id;
            return this;
        }

        public Builder message( final String message )
        {
            this.message = message;
            return this;
        }

        public WebSocketMessage build()
        {
            return new WebSocketMessage( this );
        }
    }
}

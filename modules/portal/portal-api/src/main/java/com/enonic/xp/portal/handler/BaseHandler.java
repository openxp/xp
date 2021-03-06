package com.enonic.xp.portal.handler;

import java.util.EnumSet;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import com.enonic.xp.portal.PortalException;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.websocket.WebSocketConfig;
import com.enonic.xp.portal.websocket.WebSocketEndpoint;
import com.enonic.xp.web.HttpMethod;
import com.enonic.xp.web.HttpStatus;

public abstract class BaseHandler
    implements PortalHandler
{
    private final int order;

    private EnumSet<HttpMethod> methodsAllowed;

    private boolean handleOptions;

    public BaseHandler( final int order )
    {
        this.order = order;
        setMethodsAllowed( EnumSet.allOf( HttpMethod.class ) );
        setHandleOptions( true );
    }

    protected final void setMethodsAllowed( final HttpMethod... methods )
    {
        setMethodsAllowed( EnumSet.copyOf( Lists.newArrayList( methods ) ) );
    }

    protected final void setHandleOptions( final boolean handleOptions )
    {
        this.handleOptions = handleOptions;
    }

    protected final void setMethodsAllowed( final EnumSet<HttpMethod> methods )
    {
        this.methodsAllowed = Sets.newEnumSet( methods, HttpMethod.class );
        this.methodsAllowed.add( HttpMethod.OPTIONS );
    }

    @Override
    public final int getOrder()
    {
        return this.order;
    }

    @Override
    public final PortalResponse handle( final PortalRequest req )
        throws Exception
    {
        final HttpMethod method = req.getMethod();
        checkMethodAllowed( method );

        if ( this.handleOptions && ( method == HttpMethod.OPTIONS ) )
        {
            return handleOptions();
        }

        final PortalHandlerWorker worker = createWorker( req );
        worker.execute();

        return worker.response.build();
    }

    private PortalHandlerWorker createWorker( final PortalRequest req )
        throws Exception
    {
        final PortalHandlerWorker worker = newWorker( req );
        worker.request = req;
        worker.response = PortalResponse.create();
        return worker;
    }

    protected abstract PortalHandlerWorker newWorker( PortalRequest req )
        throws Exception;

    protected final PortalException notFound( final String message, final Object... args )
    {
        return PortalException.notFound( String.format( message, args ) );
    }

    protected final PortalException methodNotAllowed( final String message, final Object... args )
    {
        return new PortalException( HttpStatus.METHOD_NOT_ALLOWED, String.format( message, args ) );
    }

    private void checkMethodAllowed( final HttpMethod method )
    {
        if ( this.methodsAllowed.contains( method ) )
        {
            return;
        }

        throw methodNotAllowed( "Method %s not allowed", method );
    }

    private PortalResponse handleOptions()
    {
        return PortalResponse.create().
            status( HttpStatus.OK ).
            header( "Allow", Joiner.on( "," ).join( this.methodsAllowed ) ).
            build();
    }

    @Override
    public WebSocketEndpoint newWebSocketEndpoint( final PortalRequest req, final WebSocketConfig config )
        throws Exception
    {
        final PortalHandlerWorker worker = createWorker( req );
        return worker.newWebSocketEndpoint( config );
    }
}

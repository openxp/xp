package com.enonic.wem.admin.portal;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Component;

import com.enonic.xp.portal.PortalContext;
import com.enonic.xp.portal.PortalContextAccessor;
import com.enonic.xp.portal.RenderMode;
import com.enonic.xp.web.handler.BaseWebHandler;
import com.enonic.xp.web.handler.WebHandler;
import com.enonic.xp.web.handler.WebHandlerChain;

@Component(immediate = true, service = WebHandler.class)
public final class PortalForwardHandler
    extends BaseWebHandler
{
    private final static String PREFIX = "/admin/portal/";

    private final static String EDIT_PREFIX = PREFIX + "edit/";

    private final static String PREVIEW_PREFIX = PREFIX + "preview/";

    public PortalForwardHandler()
    {
        setOrder( 10 );
    }

    @Override
    protected boolean canHandle( final HttpServletRequest req )
    {
        return req.getRequestURI().startsWith( PREFIX );
    }

    @Override
    protected void doHandle( final HttpServletRequest req, final HttpServletResponse res, final WebHandlerChain chain )
        throws Exception
    {
        if ( !req.isUserInRole( "admin-login" ) )
        {
            res.sendError( HttpServletResponse.SC_FORBIDDEN );
            return;
        }

        final String path = req.getRequestURI();
        if ( path.startsWith( EDIT_PREFIX ) )
        {
            final String newPath = path.substring( EDIT_PREFIX.length() );
            forwardToPortal( RenderMode.EDIT, newPath, req, res );
            return;
        }

        if ( path.startsWith( PREVIEW_PREFIX ) )
        {
            final String newPath = path.substring( PREVIEW_PREFIX.length() );
            forwardToPortal( RenderMode.PREVIEW, newPath, req, res );
            return;
        }

        res.sendError( HttpServletResponse.SC_NOT_FOUND );
    }

    private void forwardToPortal( final RenderMode mode, final String path, final HttpServletRequest req, final HttpServletResponse res )
        throws Exception
    {
        final PortalContext context = new PortalContext();
        context.setBaseUri( PREFIX );
        context.setMode( mode );

        PortalContextAccessor.set( req, context );
        final RequestDispatcher dispatcher = req.getRequestDispatcher( "/portal/" + mode.toString() + "/" + path );
        dispatcher.forward( req, res );
    }
}
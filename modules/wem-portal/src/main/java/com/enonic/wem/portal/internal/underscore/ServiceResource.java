package com.enonic.wem.portal.internal.underscore;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import com.enonic.wem.api.module.ModuleKey;
import com.enonic.wem.api.module.ModuleService;
import com.enonic.wem.portal.RenderingMode;
import com.enonic.wem.api.resource.ResourceKey;
import com.enonic.wem.portal.internal.base.BaseResource;
import com.enonic.wem.portal.internal.controller.JsContext;
import com.enonic.wem.portal.internal.controller.JsController;
import com.enonic.wem.portal.internal.controller.JsControllerFactory;
import com.enonic.wem.portal.internal.controller.JsHttpRequest;
import com.enonic.wem.portal.internal.controller.JsHttpResponseSerializer;
import com.enonic.wem.portal.internal.rendering.RenderResult;

@Path("/{mode}/{workspace}/{contentPath:.+}/_/service/{module}/{service}")
public final class ServiceResource
    extends BaseResource
{
    protected ModuleService moduleService;

    protected JsControllerFactory controllerFactory;

    protected RenderingMode mode;

    protected ModuleKey moduleKey;

    @Context
    protected Request request;

    @Context
    protected UriInfo uriInfo;

    @PathParam("service")
    protected String serviceName;

    @PathParam("mode")
    public void setMode( final String mode )
    {
        this.mode = parseMode( mode );
    }

    @PathParam("module")
    public void setModule( final String module )
    {
        this.moduleKey = ModuleKey.from( module );
    }

    @GET
    public Response handleGet()
    {
        return doHandle();
    }

    @POST
    public Response handlePost()
    {
        return doHandle();
    }

    private Response doHandle()
    {
        final JsContext context = new JsContext();

        final JsHttpRequest jsRequest = new JsHttpRequest();
        jsRequest.setMode( this.mode );
        jsRequest.setMethod( this.request.getMethod() );
        jsRequest.addParams( this.uriInfo.getQueryParameters() );
        context.setRequest( jsRequest );

        final ResourceKey scriptDir = ResourceKey.from( this.moduleKey, "service/" + this.serviceName );
        final JsController controller = this.controllerFactory.newController( scriptDir );
        controller.execute( context );

        final RenderResult result = new JsHttpResponseSerializer( context.getResponse() ).serialize();
        return toResponse( result );
    }
}
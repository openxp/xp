package com.enonic.wem.portal.postprocess;

import java.util.Map;

import javax.ws.rs.core.Response;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.google.gson.Gson;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.page.ComponentName;
import com.enonic.wem.api.content.page.Page;
import com.enonic.wem.api.content.page.PageComponent;
import com.enonic.wem.api.content.page.image.ImageComponent;
import com.enonic.wem.api.content.page.layout.LayoutComponent;
import com.enonic.wem.api.content.page.part.PartComponent;
import com.enonic.wem.api.content.page.PageRegions;
import com.enonic.wem.api.content.page.region.Region;
import com.enonic.wem.portal.controller.JsContext;
import com.enonic.wem.portal.controller.JsHttpResponse;
import com.enonic.wem.portal.rendering.RenderException;
import com.enonic.wem.portal.rendering.Renderer;
import com.enonic.wem.portal.rendering.RendererFactory;


public final class PostProcessorImpl
    implements PostProcessor
{

    protected static final String WEM_COMPONENT_ATTRIBUTE = "wem:component";

    private RendererFactory rendererFactory;

    public PostProcessorImpl()
    {
    }

    @Override
    public void processResponse( final JsContext context )
        throws Exception
    {
        final JsHttpResponse response = context.getResponse();
        if ( !response.isPostProcess() )
        {
            return;
        }
        if ( !( response.getBody() instanceof String ) )
        {
            return;
        }
        final String responseBody = (String) response.getBody();
        final Document htmlDoc = Jsoup.parseBodyFragment( responseBody );

        boolean updated = false;
        while ( processDocument( htmlDoc, context ) )
        {
            updated = true;
        }

        if ( updated )
        {
            // htmlDoc.outputSettings().prettyPrint( false );
            final String processedHtml = htmlDoc.html();
            response.setBody( processedHtml );
        }
    }

    private boolean processDocument( final Document htmlDoc, final JsContext context )
        throws Exception
    {
        boolean updated = false;
        final Elements elements = htmlDoc.getElementsByAttribute( WEM_COMPONENT_ATTRIBUTE );
        for ( Element element : elements )
        {
            processComponentElement( element, context );
            updated = true;
        }
        return updated;
    }

    private void processComponentElement( final Element element, final JsContext context )
        throws Exception
    {
        final String name = element.attr( WEM_COMPONENT_ATTRIBUTE );
        final ComponentName componentName = new ComponentName( name );

        final PageComponent component = findComponent( context.getContent(), componentName );
        if ( component == null )
        {
            throw new RenderException( "Component not found: [{0}]", componentName );
        }
        final Renderer renderer = rendererFactory.getRenderer( component );
        final Response componentResult = renderer.render( component, context );

        final String componentBody = serializeResponseBody( componentResult );
        final Document componentDoc = Jsoup.parseBodyFragment( componentBody );
        final Element componentRoot = componentDoc.body();
        replaceComponentElement( element, componentRoot );
    }

    private void replaceComponentElement( final Element targetElement, final Element sourceParentElement )
    {
        if ( sourceParentElement.children().isEmpty() )
        {
            targetElement.text( sourceParentElement.ownText() ).unwrap();
        }
        else
        {
            for ( Element el : sourceParentElement.children() )
            {
                targetElement.appendChild( el );
            }
            targetElement.unwrap();
        }
    }

    private PageComponent findComponent( final Content content, final ComponentName componentName )
    {
        if ( content == null || content.getPage() == null )
        {
            return null;
        }
        final Page page = content.getPage();
        if ( !page.hasRegions() )
        {
            return null;
        }
        final PageRegions pageRegions = page.getRegions();
        for ( Region region : pageRegions )
        {
            for ( PageComponent component : region.getComponents() )
            {
                if ( componentName.equals( getName( component ) ) )
                {
                    return component;
                }
            }
        }
        return null;
    }

    private String serializeResponseBody( final Response response )
    {
        final Object value = response.getEntity();
        if ( value == null )
        {
            return null;
        }
        if ( value instanceof String )
        {
            return (String) value;
        }
        if ( value instanceof byte[] )
        {
            return new String( (byte[]) value );
        }
        if ( value instanceof Map )
        {
            return new Gson().toJson( value );
        }
        return value.toString();
    }

    private ComponentName getName( final PageComponent component )
    {
        if ( component instanceof PartComponent )
        {
            return ( (PartComponent) component ).getName();
        }
        else if ( component instanceof ImageComponent )
        {
            return ( (ImageComponent) component ).getName();
        }
        else if ( component instanceof LayoutComponent )
        {
            return ( (LayoutComponent) component ).getName();
        }
        return null;
    }

    public void setRendererFactory( final RendererFactory rendererFactory )
    {
        this.rendererFactory = rendererFactory;
    }
}

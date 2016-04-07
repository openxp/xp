package com.enonic.xp.lib.http;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.StringUtils;

import com.google.common.io.ByteSource;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.HttpUrl;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.internal.http.HttpMethod;

public final class HttpRequestHandler
{
    private String url;

    private String method = "GET";

    private Map<String, Object> params;

    private Map<String, String> headers;

    private int connectionTimeout = 10_000;

    private int readTimeout = 10_000;

    private String bodyString;

    private ByteSource bodyStream;

    private List<Map<String, Object>> bodyMultipart;

    private String contentType;

    public void setUrl( final String value )
    {
        this.url = value;
    }

    public void setMethod( final String value )
    {
        if ( value != null )
        {
            this.method = value.trim().toUpperCase();
        }
    }

    public void setParams( final Map<String, Object> params )
    {
        this.params = params;
    }

    public void setHeaders( final Map<String, String> headers )
    {
        this.headers = headers;
    }

    public void setConnectionTimeout( final Integer value )
    {
        if ( value != null )
        {
            this.connectionTimeout = value;
        }
    }

    public void setReadTimeout( final Integer value )
    {
        if ( value != null )
        {
            this.readTimeout = value;
        }
    }

    public void setBody( final String value )
    {
        this.bodyString = value;
    }

    public void setBody( final ByteSource value )
    {
        this.bodyStream = value;
    }

    public void setBody( final List<Map<String, Object>> value )
    {
        this.bodyMultipart = value;
    }

    public void setContentType( final String contentType )
    {
        this.contentType = contentType;
    }

    public ResponseMapper request()
        throws IOException
    {
        final Request request = getRequest();
        final Response response = sendRequest( request );
        return new ResponseMapper( response );
    }

    private Request getRequest()
    {
        final Request.Builder request = new Request.Builder();
        setRequestUrl( request );
        setRequestHeaders( request );
        setRequestMethod( request );
        return request.build();
    }

    private void setRequestUrl( Request.Builder request )
    {
        if ( "GET".equals( this.method ) )
        {
            HttpUrl url = HttpUrl.parse( this.url );
            if ( this.params != null )
            {
                url = addParams( url, this.params );
            }
            request.url( url );
        }
        else
        {
            request.url( this.url );
        }
    }

    private void setRequestHeaders( Request.Builder request )
    {
        if ( "GET".equals( this.method ) )
        {
            if ( this.contentType != null )
            {
                request.header( "Content-Type", this.contentType );
            }
        }

        if ( this.headers != null )
        {
            for ( Map.Entry<String, String> header : this.headers.entrySet() )
            {
                request.header( header.getKey(), header.getValue() );
            }
        }
    }

    private void setRequestMethod( Request.Builder request )
    {
        RequestBody requestBody = null;
        if ( HttpMethod.permitsRequestBody( this.method ) )
        {
            requestBody = getRequestBody();

            if ( requestBody == null && HttpMethod.requiresRequestBody( this.method ) )
            {
                final MediaType mediaType = this.contentType != null ? MediaType.parse( this.contentType ) : null;
                requestBody = RequestBody.create( mediaType, "" );
            }
        }

        request.method( this.method, requestBody );
    }

    private RequestBody getRequestBody()
    {
        if ( this.params != null && !this.params.isEmpty() )
        {
            final FormEncodingBuilder formBody = new FormEncodingBuilder();
            addParams( formBody, this.params );
            return formBody.build();
        }
        if ( this.bodyString != null && !this.bodyString.isEmpty() )
        {
            final MediaType mediaType = this.contentType != null ? MediaType.parse( this.contentType ) : null;
            return RequestBody.create( mediaType, this.bodyString );
        }
        if ( this.bodyStream != null )
        {
            final MediaType mediaType = this.contentType != null ? MediaType.parse( this.contentType ) : null;
            return new StreamRequestBody( mediaType, this.bodyStream );
        }
        if ( this.bodyMultipart != null )
        {
            return getMultipartRequestBody();
        }
        return null;
    }

    private RequestBody getMultipartRequestBody()
    {
        final MultipartBuilder multipartBuilder = new MultipartBuilder().type( MultipartBuilder.FORM );

        for ( Map<String, Object> multipartItem : this.bodyMultipart )
        {
            final String name = getValue( multipartItem, "name" );
            final String fileName = getValue( multipartItem, "fileName" );
            final String contentType = getValue( multipartItem, "contentType" );
            final Object value = multipartItem.get( "value" );

            if ( !StringUtils.isBlank( name ) && value != null )
            {
                if ( value instanceof ByteSource )
                {
                    final ByteSource stream = (ByteSource) value;
                    final String ct = contentType == null ? "application/octet-stream" : contentType;
                    final MediaType mediaType = MediaType.parse( ct );
                    final StreamRequestBody streamRequestBody = new StreamRequestBody( mediaType, stream );
                    multipartBuilder.addFormDataPart( name, fileName, streamRequestBody );
                }
                else
                {
                    multipartBuilder.addFormDataPart( name, value.toString() );
                }
            }
        }
        return multipartBuilder.build();
    }

    private String getValue( final Map<String, Object> object, final String key )
    {
        final Object value = object.get( key );
        return value == null ? null : value.toString();
    }


    private HttpUrl addParams( final HttpUrl url, final Map<String, Object> params )
    {
        HttpUrl.Builder urlBuilder = url.newBuilder();
        for ( Map.Entry<String, Object> param : params.entrySet() )
        {
            if ( param.getValue() != null )
            {
                urlBuilder.addEncodedQueryParameter( param.getKey(), param.getValue().toString() );
            }
        }
        return urlBuilder.build();
    }

    private void addParams( final FormEncodingBuilder formBody, final Map<String, Object> params )
    {
        for ( Map.Entry<String, Object> header : params.entrySet() )
        {
            if ( header.getValue() != null )
            {
                formBody.add( header.getKey(), header.getValue().toString() );
            }
        }
    }

    private Response sendRequest( final Request request )
        throws IOException
    {
        final OkHttpClient client = new OkHttpClient();
        client.setReadTimeout( this.readTimeout, TimeUnit.MILLISECONDS );
        client.setConnectTimeout( this.connectionTimeout, TimeUnit.MILLISECONDS );
        return client.newCall( request ).execute();
    }

}

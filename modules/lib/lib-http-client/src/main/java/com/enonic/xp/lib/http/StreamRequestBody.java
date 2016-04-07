package com.enonic.xp.lib.http;

import java.io.IOException;

import com.google.common.io.ByteSource;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.RequestBody;

import okio.BufferedSink;

public class StreamRequestBody
    extends RequestBody
{
    private MediaType mediaType;

    private ByteSource stream;

    public StreamRequestBody( final MediaType mediaType, final ByteSource stream )
    {
        this.mediaType = mediaType;
        this.stream = stream;
    }

    @Override
    public MediaType contentType()
    {
        return this.mediaType;
    }

    @Override
    public void writeTo( final BufferedSink sink )
        throws IOException
    {
        this.stream.copyTo( sink.outputStream() );
    }
}

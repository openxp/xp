package com.enonic.xp.extractor;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.apache.tika.detect.DefaultDetector;
import org.apache.tika.parser.DefaultParser;
import org.junit.Before;
import org.junit.Test;

import com.google.common.io.ByteSource;
import com.google.common.io.ByteStreams;

import static org.junit.Assert.*;

public class ExtractorImplTest
{
    private ExtractorImpl extractor;

    @Before
    public void setUp()
        throws Exception
    {
        this.extractor = new ExtractorImpl();
        this.extractor.setDetector( new DefaultDetector() );
        this.extractor.setParser( new DefaultParser() );
    }

    @Test
    public void testName()
        throws Exception
    {
        final ByteSource binary = getAsByteSource( "sommerfest.pdf" );

        final ExtractedData extractedData = this.extractor.extract( ExtractorParams.create().
            byteSource( binary ).
            cleanContent( true ).
            build() );

        assertEquals( getAsString( "sommerfest_extracted.txt" ), extractedData.getContent() );
    }

    private String getAsString( final String fileName )
        throws IOException
    {
        return new String( getAsByteSource( fileName ).read(), StandardCharsets.UTF_8 );
    }

    private ByteSource getAsByteSource( final String fileName )
        throws IOException
    {
        return ByteSource.wrap( ByteStreams.toByteArray( this.getClass().getResourceAsStream( fileName ) ) );
    }
}
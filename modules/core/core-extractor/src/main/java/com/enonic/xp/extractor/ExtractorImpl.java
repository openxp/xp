package com.enonic.xp.extractor;

import java.io.IOException;
import java.io.InputStream;

import org.apache.tika.detect.Detector;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.osgi.service.component.annotations.Reference;
import org.xml.sax.SAXException;

import com.enonic.xp.schema.content.ContentType;
import com.enonic.xp.util.Exceptions;

public class ExtractorImpl
    implements Extractor
{
    private Parser parser;

    private Detector detector;

    public boolean supports( final ContentType contentType )
    {
        return contentType.getName().isTextContainingMedia();
    }

    @Override
    public ExtractedData extract( final ExtractorParams params )
    {
        final ParseContext context = new ParseContext();
        final BodyContentHandler handler = new BodyContentHandler();
        final Metadata metadata = new Metadata();

        // Parse metadata
        try (final InputStream stream = params.getByteSource().openStream())
        {
            final AutoDetectParser autoDetectParser = new AutoDetectParser( this.detector, this.parser );

            autoDetectParser.parse( stream, handler, metadata, context );
        }
        catch ( IOException | SAXException | TikaException e )
        {
            throw Exceptions.unchecked( e );
        }

        final ExtractedData.Builder builder = ExtractedData.create();

        for ( final String name : metadata.names() )
        {
            builder.addProperty( name, metadata.get( name ) );
        }

        builder.content( getContent( handler, params.isCleanContent() ) );

        return builder.build();

    }

    private String getContent( final BodyContentHandler contentHandler, final boolean clean )
    {
        return clean ? ExtractedTextCleaner.clean( contentHandler.toString() ) : contentHandler.toString();
    }


    @Reference
    public void setParser( final Parser parser )
    {
        this.parser = parser;
    }

    @Reference
    public void setDetector( final Detector detector )
    {
        this.detector = detector;
    }

}

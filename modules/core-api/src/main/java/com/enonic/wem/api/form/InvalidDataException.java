package com.enonic.wem.api.form;


import com.enonic.wem.api.data.Property;

public class InvalidDataException
    extends RuntimeException
{
    private Property property;

    public InvalidDataException( final Property property, final Throwable e )
    {
        super( buildMessage( property ), e );
        this.property = property;
    }

    public InvalidDataException( final Property property, final String message )
    {
        super( buildMessage( property, message ) );
        this.property = property;
    }

    public Property getProperty()
    {
        return property;
    }

    private static String buildMessage( final Property property )
    {
        StringBuilder s = new StringBuilder();
        s.append( "Invalid data: " ).append( property );
        return s.toString();
    }

    private static String buildMessage( final Property property, final String message )
    {
        StringBuilder s = new StringBuilder();
        s.append( "Invalid data [" ).append( property ).append( "]: " ).append( message );
        return s.toString();
    }
}
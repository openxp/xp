package com.enonic.xp.portal.impl.websocket;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.branch.Branch;

final class WebSocketLocation
{
    private final static Pattern PATTERN = Pattern.compile( "/portal/([^/]+)/.+/_/ws/([^/]+)/([^/]+)" );

    private Branch branch;

    private ApplicationKey application;

    private String name;

    public Branch getBranch()
    {
        return this.branch;
    }

    public ApplicationKey getApplication()
    {
        return this.application;
    }

    public String getName()
    {
        return this.name;
    }

    public static WebSocketLocation parse( final String str )
    {
        final Matcher matcher = PATTERN.matcher( str );
        if ( !matcher.matches() )
        {
            return null;
        }

        final WebSocketLocation location = new WebSocketLocation();
        location.branch = Branch.from( matcher.group( 1 ) );
        location.application = ApplicationKey.from( matcher.group( 2 ) );
        location.name = matcher.group( 3 );
        return location;
    }
}

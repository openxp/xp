package com.enonic.wem.web.boot;

import org.apache.commons.lang.SystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.wem.api.Version;
import com.enonic.wem.core.home.HomeResolver;

final class BootEnvironment
{
    private final static Logger LOG = LoggerFactory.getLogger( BootEnvironment.class );

    public void initialize()
    {
        try
        {
            doInitialize();
        }
        catch ( final Exception e )
        {
            LOG.error( "Error occurred starting system", e );

            if ( e instanceof RuntimeException )
            {
                throw (RuntimeException) e;
            }
            else
            {
                throw new RuntimeException( e );
            }
        }
    }

    private void doInitialize()
        throws Exception
    {
        logBanner();
        resolveHomeDir();
    }

    public void destroy()
    {
        // Do nothing for now
    }

    private void resolveHomeDir()
    {
        final HomeResolver resolver = new HomeResolver();
        resolver.addSystemProperties( System.getenv() );
        resolver.addSystemProperties( System.getProperties() );
        resolver.resolve();
    }

    private void logBanner()
    {
        final StringBuilder str = new StringBuilder();
        str.append( "\n" ).append( Version.get().getBanner() ).append( "\n" );
        str.append( "  # " ).append( Version.get().getNameAndVersion() ).append( "\n" );
        str.append( "  # " ).append( getFormattedJvmInfo() ).append( "\n" );
        str.append( "  # " ).append( getFormattedOsInfo() ).append( "\n" );

        LOG.info( str.toString() );
    }

    private String getFormattedJvmInfo()
    {
        final StringBuilder str = new StringBuilder();
        str.append( SystemUtils.JAVA_RUNTIME_NAME ).append( " " ).append( SystemUtils.JAVA_RUNTIME_VERSION ).append( " (" ).append(
            SystemUtils.JAVA_VENDOR ).append( ")" );
        return str.toString();
    }

    private String getFormattedOsInfo()
    {
        final StringBuilder str = new StringBuilder();
        str.append( SystemUtils.OS_NAME ).append( " " ).append( SystemUtils.OS_VERSION ).append( " (" ).append(
            SystemUtils.OS_ARCH ).append( ")" );
        return str.toString();
    }
}

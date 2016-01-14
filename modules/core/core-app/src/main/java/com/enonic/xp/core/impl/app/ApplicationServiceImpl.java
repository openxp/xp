package com.enonic.xp.core.impl.app;

import java.io.IOException;
import java.io.InputStream;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.google.common.io.ByteSource;

import com.enonic.xp.app.Application;
import com.enonic.xp.app.ApplicationInvalidator;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationKeys;
import com.enonic.xp.app.ApplicationNotFoundException;
import com.enonic.xp.app.ApplicationService;
import com.enonic.xp.app.Applications;
import com.enonic.xp.util.Exceptions;

@Component
public final class ApplicationServiceImpl
    implements ApplicationService, ApplicationInvalidator
{
    private ApplicationRegistry registry;

    private BundleContext context;

    private ApplicationRepoService repoService;

    @Activate
    public void activate( final BundleContext context )
    {
        this.registry = new ApplicationRegistry( context );
        this.context = context;
    }

    @Override
    public Application getApplication( final ApplicationKey key )
        throws ApplicationNotFoundException
    {
        final Application application = this.registry.get( key );
        if ( application == null )
        {
            throw new ApplicationNotFoundException( key );
        }
        return application;
    }

    @Override
    public ApplicationKeys getApplicationKeys()
    {
        return this.registry.getKeys();
    }

    @Override
    public Applications getAllApplications()
    {
        return Applications.from( this.registry.getAll() );
    }

    @Override
    public void startApplication( final ApplicationKey key )
    {
        startApplication( getApplication( key ) );
    }

    @Override
    public void stopApplication( final ApplicationKey key )
    {
        stopApplication( getApplication( key ) );
    }

    @Override
    public Application installApplication( final ByteSource byteSource )
    {
        final String applicationName = getApplicationName( byteSource );

        final Application existingApp = this.registry.get( ApplicationKey.from( applicationName ) );

        if ( existingApp != null )
        {
            return doUpdateApplication( applicationName, byteSource );
        }
        else
        {
            return doInstallApplication( byteSource, applicationName );
        }

    }

    private Application doUpdateApplication( final String applicationName, final ByteSource source )
    {
        uninstallBundle( applicationName );

        this.registry.invalidate( ApplicationKey.from( applicationName ) );

        final Bundle bundle = doInstallBundle( source, applicationName );

        final Application application = this.registry.get( ApplicationKey.from( bundle ) );

        repoService.updateApplicationNode( application, source );

        return application;
    }

    private void uninstallBundle( final String applicationName )
    {
        try
        {
            this.context.getBundle( applicationName ).uninstall();
        }
        catch ( BundleException e )
        {
            e.printStackTrace();
        }
    }

    private Application doInstallApplication( final ByteSource byteSource, final String applicationName )
    {
        final Bundle bundle = doInstallBundle( byteSource, applicationName );

        final Application application = this.registry.get( ApplicationKey.from( bundle ) );

        repoService.createApplicationNode( application, byteSource );

        return application;
    }

    private String getApplicationName( final ByteSource byteSource )
    {
        final String applicationName;

        try
        {
            applicationName = ApplicationNameResolver.resolve( byteSource );
        }
        catch ( Exception e )
        {
            throw new ApplicationInstallException( "Cannot install application: " + e.getMessage(), e );
        }
        return applicationName;
    }

    private Bundle doInstallBundle( final ByteSource source, final String symbolicName )
    {
        try (final InputStream in = source.openStream())
        {
            return this.context.installBundle( symbolicName, in );
        }
        catch ( BundleException e )
        {
            throw new ApplicationInstallException( "Could not install application bundle:   '" + symbolicName + "'", e );
        }
        catch ( IOException e )
        {
            throw new RuntimeException( "Failed to install bundle", e );
        }
    }

    private void startApplication( final Application application )
    {
        try
        {
            application.getBundle().start();
        }
        catch ( final Exception e )
        {
            throw Exceptions.unchecked( e );
        }
    }

    private void stopApplication( final Application application )
    {
        try
        {
            application.getBundle().stop();
        }
        catch ( final Exception e )
        {
            throw Exceptions.unchecked( e );
        }
    }

    @Override
    public void invalidate( final ApplicationKey key )
    {
        this.registry.invalidate( key );
    }

    @Reference
    public void setRepoService( final ApplicationRepoService repoService )
    {
        this.repoService = repoService;
    }
}

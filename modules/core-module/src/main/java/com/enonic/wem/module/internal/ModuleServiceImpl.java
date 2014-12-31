package com.enonic.wem.module.internal;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.google.common.collect.ImmutableList;

import com.enonic.wem.api.module.Module;
import com.enonic.wem.api.module.ModuleKey;
import com.enonic.wem.api.module.ModuleKeys;
import com.enonic.wem.api.module.ModuleNotFoundException;
import com.enonic.wem.api.module.ModuleService;
import com.enonic.wem.api.module.Modules;

@Component
public final class ModuleServiceImpl
    implements ModuleService
{
    private ModuleRegistry registry;

    @Override
    public Module getModule( final ModuleKey key )
        throws ModuleNotFoundException
    {
        final Module module = this.registry.get( key );
        if ( module == null )
        {
            throw new ModuleNotFoundException( key );
        }
        return module;
    }

    @Override
    public Modules getModules( final ModuleKeys keys )
    {
        final ImmutableList.Builder<Module> moduleList = ImmutableList.builder();
        for ( final ModuleKey key : keys )
        {
            final Module module = this.registry.get( key );
            if ( module != null )
            {
                moduleList.add( module );
            }
        }
        return Modules.from( moduleList.build() );
    }

    @Override
    public Modules getAllModules()
    {
        return Modules.from( this.registry.getAll() );
    }

    @Reference
    public void setRegistry( final ModuleRegistry registry )
    {
        this.registry = registry;
    }
}
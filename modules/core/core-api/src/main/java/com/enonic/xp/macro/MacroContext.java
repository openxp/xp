package com.enonic.xp.macro;

import java.util.Objects;

import com.google.common.annotations.Beta;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableMap;

@Beta
public final class MacroContext
{
    private final String name;

    private final String body;

    private final ImmutableMap<String, String> params;

    private MacroContext( final Builder builder )
    {
        this.name = builder.name;
        this.body = builder.body;
        this.params = builder.paramsBuilder.build();
    }

    public String getName()
    {
        return name;
    }

    public String getBody()
    {
        return body;
    }

    public Iterable<String> getParamNames()
    {
        return params.keySet();
    }

    public String getParam( final String name )
    {
        return this.params.get( name );
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( o == null || getClass() != o.getClass() )
        {
            return false;
        }

        final MacroContext that = (MacroContext) o;
        return Objects.equals( this.name, that.name ) && Objects.equals( this.body, that.body ) && this.params.equals( that.params );
    }

    @Override
    public int hashCode()
    {
        return Objects.hash( name, body, params );
    }

    public String toString()
    {
        return this.name + "=" + this.body + "[" + Joiner.on( "," ).withKeyValueSeparator( "=" ).join( this.params ) + "]";
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static Builder copyOf( final MacroContext macroContext )
    {
        return new Builder( macroContext );
    }

    public static class Builder
    {

        private String name;

        private String body;

        private final ImmutableMap.Builder<String, String> paramsBuilder;

        public Builder()
        {
            this.paramsBuilder = ImmutableMap.builder();
        }

        private Builder( final MacroContext macroContext )
        {
            this.name = macroContext.name;
            this.body = macroContext.body;
            this.paramsBuilder = ImmutableMap.builder();
            this.paramsBuilder.putAll( macroContext.params );
        }

        public Builder name( final String name )
        {
            this.name = name;
            return this;
        }

        public Builder body( final String body )
        {
            this.body = body;
            return this;
        }

        public Builder param( final String name, final String value )
        {
            this.paramsBuilder.put( name, value );
            return this;
        }

        public MacroContext build()
        {
            return new MacroContext( this );
        }
    }
}

package com.enonic.xp.security;

import com.google.common.annotations.Beta;
import com.google.common.collect.ImmutableList;

import static com.google.common.base.Preconditions.checkNotNull;

@Beta
public final class UpdatePathGuardParams
{
    private final PathGuardKey key;

    private final String displayName;

    private final AuthConfig authConfig;

    private final ImmutableList<String> paths;

    private UpdatePathGuardParams( final Builder builder )
    {
        this.key = checkNotNull( builder.key, "key is required" );
        this.displayName = checkNotNull( builder.displayName, "displayName is required" );
        this.authConfig = builder.authConfig;
        this.paths = builder.paths.build();
    }

    public PathGuardKey getKey()
    {
        return key;
    }

    public String getDisplayName()
    {
        return displayName;
    }

    public AuthConfig getAuthConfig()
    {
        return authConfig;
    }

    public ImmutableList<String> getPaths()
    {
        return paths;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static class Builder
    {
        private PathGuardKey key;

        private String displayName;

        private AuthConfig authConfig;

        private ImmutableList.Builder<String> paths = ImmutableList.builder();

        private Builder()
        {
        }

        public Builder key( final PathGuardKey value )
        {
            this.key = value;
            return this;
        }

        public Builder displayName( final String value )
        {
            this.displayName = value;
            return this;
        }

        public Builder authConfig( final AuthConfig value )
        {
            this.authConfig = value;
            return this;
        }

        public Builder addPaths( final String... paths )
        {
            for ( String path : paths )
            {
                this.paths.add( path );
            }
            return this;
        }

        public UpdatePathGuardParams build()
        {
            return new UpdatePathGuardParams( this );
        }
    }
}

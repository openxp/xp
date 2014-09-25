package com.enonic.wem.api.index;

import com.enonic.wem.api.data.DataPath;

public class PathIndexConfig
    implements Comparable<PathIndexConfig>
{
    private final DataPath path;

    private final IndexConfig indexConfig;

    private PathIndexConfig( Builder builder )
    {
        path = builder.path;
        indexConfig = builder.indexConfig;
    }

    public boolean matches( final DataPath dataPath )
    {
        return dataPath.startsWith( path );

    }

    public DataPath getPath()
    {
        return path;
    }

    public IndexConfig getIndexConfig()
    {
        return indexConfig;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private DataPath path;

        private IndexConfig indexConfig;

        private Builder()
        {
        }

        public Builder path( DataPath path )
        {
            this.path = path;
            return this;
        }

        public Builder indexConfig( IndexConfig indexConfig )
        {
            this.indexConfig = indexConfig;
            return this;
        }

        public PathIndexConfig build()
        {
            return new PathIndexConfig( this );
        }
    }

    @Override
    public int compareTo( final PathIndexConfig o )
    {
        final int BEFORE = -1;
        final int EQUAL = 0;
        final int AFTER = 1;

        if ( this == o )
        {
            return EQUAL;
        }

        final int thisElementCount = this.path.elementCount();
        final int thatElementCount = o.path.elementCount();

        if ( thisElementCount < thatElementCount )
        {
            return AFTER;
        }
        if ( thisElementCount > thatElementCount )
        {
            return BEFORE;
        }

        return this.hashCode() - o.hashCode();
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }
        if ( !( o instanceof PathIndexConfig ) )
        {
            return false;
        }

        final PathIndexConfig that = (PathIndexConfig) o;

        if ( indexConfig != null ? !indexConfig.equals( that.indexConfig ) : that.indexConfig != null )
        {
            return false;
        }
        if ( path != null ? !path.equals( that.path ) : that.path != null )
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        int result = path != null ? path.hashCode() : 0;
        result = 31 * result + ( indexConfig != null ? indexConfig.hashCode() : 0 );
        return result;
    }
}
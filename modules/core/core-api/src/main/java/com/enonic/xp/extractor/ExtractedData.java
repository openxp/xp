package com.enonic.xp.extractor;

import java.util.Map;
import java.util.Set;

import com.google.common.collect.Maps;

public class ExtractedData
{
    private final String content;

    private final Map<String, String> properties;

    private ExtractedData( final Builder builder )
    {
        content = builder.content;
        properties = builder.properties;
    }

    public Set<String> getPropertyNames()
    {
        return this.properties.keySet();
    }

    public String get( final String name )
    {
        return this.properties.get( name );
    }

    public String getContent()
    {
        return content;
    }

    public Map<String, String> getProperties()
    {
        return properties;
    }

    public static Builder create()
    {
        return new Builder();
    }


    public static final class Builder
    {
        private String content;

        private Map<String, String> properties = Maps.newHashMap();

        private Builder()
        {
        }

        public Builder content( final String val )
        {
            content = val;
            return this;
        }

        public Builder addProperty( final String key, final String value )
        {
            this.properties.put( key, value );
            return this;
        }

        public Builder setProperties( final Map<String, String> val )
        {
            properties = val;
            return this;
        }

        public ExtractedData build()
        {
            return new ExtractedData( this );
        }
    }
}

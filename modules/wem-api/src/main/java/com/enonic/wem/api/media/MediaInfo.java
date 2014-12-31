package com.enonic.wem.api.media;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;

import com.enonic.wem.api.form.FormItemName;

public class MediaInfo
{
    private String mediaType;

    private ImmutableMap<String, String> metadata;

    private MediaInfo( final Builder builder )
    {
        this.mediaType = builder.mediaType;
        this.metadata = builder.metadata.build();
        Preconditions.checkNotNull( this.metadata, "metadata cannot be null" );
    }

    public String getMediaType()
    {
        return mediaType;
    }

    public ImmutableMap<String, String> getMetadata()
    {
        return metadata;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static class Builder
    {
        private String mediaType;

        private ImmutableMap.Builder<String, String> metadata = ImmutableMap.builder();

        public Builder mediaType( final String value )
        {
            this.mediaType = value;
            return this;
        }

        public Builder addMetadata( final String name, final String value )
        {
            this.metadata.put( FormItemName.safeName( name ), value );
            return this;
        }

        public MediaInfo build()
        {
            return new MediaInfo( this );
        }
    }
}
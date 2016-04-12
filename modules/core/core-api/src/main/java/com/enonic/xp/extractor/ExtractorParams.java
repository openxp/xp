package com.enonic.xp.extractor;

import com.google.common.base.Preconditions;
import com.google.common.io.ByteSource;

public class ExtractorParams
{

    private ByteSource byteSource;

    private boolean cleanContent;

    private ExtractorParams( final Builder builder )
    {
        byteSource = builder.byteSource;
        cleanContent = builder.cleanContent;
    }

    public ByteSource getByteSource()
    {
        return byteSource;
    }

    public boolean isCleanContent()
    {
        return cleanContent;
    }

    public static Builder create()
    {
        return new Builder();
    }


    public static final class Builder
    {
        private ByteSource byteSource;

        private boolean cleanContent = true;

        private Builder()
        {
        }

        public Builder byteSource( final ByteSource val )
        {
            byteSource = val;
            return this;
        }

        public Builder cleanContent( final boolean val )
        {
            cleanContent = val;
            return this;
        }

        private void validate()
        {
            Preconditions.checkNotNull( byteSource, "ByteSource must be set" );
        }

        public ExtractorParams build()
        {
            return new ExtractorParams( this );
        }
    }
}

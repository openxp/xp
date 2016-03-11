package com.enonic.xp.media;

import com.google.common.annotations.Beta;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMultimap;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.form.FormItemName;
import com.enonic.xp.schema.mixin.MixinName;

@Beta
public final class MediaInfo
{
    private final String mediaType;

    private final ImmutableMultimap<String, String> metadata;

    private final ExtractedTextInfo extractedTextInfo;

    public static final String CAMERA_INFO = "cameraInfo";

    public static final String GPS_INFO = "gpsInfo";

    public static final String IMAGE_INFO = "imageInfo";

    public static final String EXTRACTED_TEXT_INFO = "extractedText";

    public static final String EXTRACTED_TEXT_CONTENT = "content";

    public static final String IMAGE_INFO_PIXEL_SIZE = "pixelSize";

    public static final String IMAGE_INFO_IMAGE_HEIGHT = "imageHeight";

    public static final String IMAGE_INFO_IMAGE_WIDTH = "imageWidth";

    public static final String MEDIA_INFO_BYTE_SIZE = "byteSize";

    public static final MixinName IMAGE_INFO_METADATA_NAME = MixinName.from( ApplicationKey.MEDIA_MOD, IMAGE_INFO );

    public static final MixinName CAMERA_INFO_METADATA_NAME = MixinName.from( ApplicationKey.MEDIA_MOD, CAMERA_INFO );

    public static final MixinName GPS_INFO_METADATA_NAME = MixinName.from( ApplicationKey.BASE, GPS_INFO );

    public static final MixinName EXTRACTED_TEXT_MIXIN_NAME = MixinName.from( ApplicationKey.MEDIA_MOD, EXTRACTED_TEXT_INFO );

    private MediaInfo( final Builder builder )
    {
        this.mediaType = builder.mediaType;
        this.metadata = builder.metadata.build();
        this.extractedTextInfo = builder.extractedTextInfo;
        Preconditions.checkNotNull( this.metadata, "metadata cannot be null" );
    }

    public String getMediaType()
    {
        return mediaType;
    }

    public ExtractedTextInfo getExtractedTextInfo()
    {
        return extractedTextInfo;
    }

    public ImmutableMultimap<String, String> getMetadata()
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

        private final ImmutableMultimap.Builder<String, String> metadata = ImmutableMultimap.builder();

        private ExtractedTextInfo extractedTextInfo;

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


        public Builder setExtratedTextInfo( final ExtractedTextInfo extractedTextInfo )
        {
            this.extractedTextInfo = extractedTextInfo;
            return this;
        }

        public MediaInfo build()
        {
            return new MediaInfo( this );
        }
    }
}

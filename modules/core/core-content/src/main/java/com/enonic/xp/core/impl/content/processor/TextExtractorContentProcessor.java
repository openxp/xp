package com.enonic.xp.core.impl.content.processor;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.content.CreateContentParams;
import com.enonic.xp.content.ExtraDatas;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.media.MediaInfo;
import com.enonic.xp.schema.content.ContentType;
import com.enonic.xp.schema.content.ContentTypeService;
import com.enonic.xp.schema.mixin.MixinService;
import com.enonic.xp.schema.mixin.Mixins;


@Component
public class TextExtractorContentProcessor
    implements ContentProcessor
{

    protected ContentTypeService contentTypeService;

    protected MixinService mixinService;

    @Override
    public boolean supports( final ContentType contentType )
    {
        return contentType.getName().isTextContainingMedia();
    }

    public ProcessCreateResult processCreate( final ProcessCreateParams params )
    {
        final CreateContentParams createContentParams = params.getCreateContentParams();

        final PropertyTree data = createContentParams.getData();

        final MediaInfo mediaInfo = params.getMediaInfo();

        if ( mediaInfo != null && mediaInfo.getExtractedTextInfo() != null )
        {
            data.setString( "extractedText", ExtractedTextCleaner.clean( mediaInfo.getExtractedTextInfo().getExtractedText() ) );
        }

        return new ProcessCreateResult( createContentParams );
    }


    @Override
    public ProcessUpdateResult processUpdate( final ProcessUpdateParams params )
    {
        return null;
    }

    private ExtraDatas extractMetadata( final MediaInfo mediaInfo, final Mixins mixins )
    {
        return null;
    }

    @Reference
    public void setContentTypeService( final ContentTypeService contentTypeService )
    {
        this.contentTypeService = contentTypeService;
    }

    @Reference
    public void setMixinService( final MixinService mixinService )
    {
        this.mixinService = mixinService;
    }
}

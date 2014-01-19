package com.enonic.wem.core.content;

import com.enonic.wem.api.command.content.CreateContent;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.data.ContentData;
import com.enonic.wem.api.data.DataSet;
import com.enonic.wem.api.data.RootDataSet;
import com.enonic.wem.api.data.Value;
import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.api.support.serializer.AbstractDataSetSerializer;
import com.enonic.wem.core.content.page.PageDataSerializer;
import com.enonic.wem.core.content.site.SiteDataSerializer;
import com.enonic.wem.core.form.FormDataSerializer;

public class ContentDataSerializer
    extends AbstractDataSetSerializer<Content, Content.Builder>
{
    public static final String DISPLAY_NAME = "displayName";

    public static final String DRAFT = "draft";

    public static final String CONTENT_DATA = "contentdata";

    public static final String CONTENT_TYPE = "contentType";

    public static final String FORM = "form";

    public static final String PAGE = "page";

    public static final String SITE = "site";

    private static final FormDataSerializer FORM_SERIALIZER = new FormDataSerializer( FORM );

    private static final PageDataSerializer PAGE_SERIALIZER = new PageDataSerializer( PAGE );

    private static final SiteDataSerializer SITE_SERIALIZER = new SiteDataSerializer( SITE );


    public RootDataSet toData( final Content content )
    {
        final RootDataSet contentAsData = new RootDataSet();

        addPropertyIfNotNull( contentAsData, DRAFT, content.isDraft() );
        addPropertyIfNotNull( contentAsData, DISPLAY_NAME, content.getDisplayName() );
        addPropertyIfNotNull( contentAsData, CONTENT_TYPE, content.getType().getContentTypeName() );

        contentAsData.add( content.getContentData().toDataSet( CONTENT_DATA ) );
        contentAsData.add( FORM_SERIALIZER.toData( content.getForm() ) );

        if ( content.isPage() )
        {
            contentAsData.add( PAGE_SERIALIZER.toData( content.getPage() ) );
        }
        if ( content.isSite() )
        {
            contentAsData.add( SITE_SERIALIZER.toData( content.getSite() ) );
        }

        return contentAsData;
    }


    public Content.Builder fromData( final DataSet dataSet )
    {
        final Content.Builder builder = Content.newContent();

        if ( dataSet.hasData( DISPLAY_NAME ) )
        {
            builder.displayName( dataSet.getProperty( DISPLAY_NAME ).getString() );
        }

        if ( dataSet.hasData( CONTENT_TYPE ) )
        {
            builder.type( ContentTypeName.from( dataSet.getProperty( CONTENT_TYPE ).getString() ) );
        }

        if ( dataSet.hasData( CONTENT_DATA ) )
        {
            builder.contentData( new ContentData( dataSet.getDataSet( CONTENT_DATA ).toRootDataSet() ) );
        }

        if ( dataSet.hasData( FORM ) )
        {
            builder.form( FORM_SERIALIZER.fromData( dataSet.getDataSet( FORM ) ) );
        }

        if ( dataSet.hasData( PAGE ) )
        {
            builder.page( PAGE_SERIALIZER.fromData( dataSet.getDataSet( PAGE ) ) );
        }

        if ( dataSet.hasData( SITE ) )
        {
            builder.site( SITE_SERIALIZER.fromData( dataSet.getDataSet( SITE ) ) );
        }

        return builder;
    }

    RootDataSet toData( final CreateContent command )
    {
        final RootDataSet contentAsData = new RootDataSet();

        addPropertyIfNotNull( contentAsData, DRAFT, command.isDraft() );
        addPropertyIfNotNull( contentAsData, DISPLAY_NAME, command.getDisplayName() );
        addPropertyIfNotNull( contentAsData, CONTENT_TYPE, command.getContentType() );

        if ( command.getContentData() != null )
        {
            contentAsData.add( command.getContentData().toDataSet( ContentDataSerializer.CONTENT_DATA ) );
        }

        if ( command.getForm() != null )
        {
            contentAsData.add( FORM_SERIALIZER.toData( command.getForm() ) );
        }

        return contentAsData;
    }

    private void addPropertyIfNotNull( final RootDataSet rootDataSet, final String propertyName, final Object value )
    {
        if ( value != null )
        {
            rootDataSet.setProperty( propertyName, new Value.String( value.toString() ) );
        }
    }
}

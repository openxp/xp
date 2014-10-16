package com.enonic.wem.core.elasticsearch.xcontent;

import java.time.Instant;

import org.elasticsearch.common.xcontent.XContentBuilder;

import com.enonic.wem.core.index.IndexException;
import com.enonic.wem.core.version.EntityVersionDocument;

public class VersionXContentBuilderFactory
    extends AbstractXContentBuilderFactor
{
    public static final String NODE_VERSION_ID_FIELD_NAME = "versionId";

    public static final String ENTITY_ID_FIELD_NAME = "entityId";

    public static final String TIMESTAMP_ID_FIELD_NAME = "timestamp";

    public static XContentBuilder create( final EntityVersionDocument entityVersionDocument )
    {
        try
        {
            final XContentBuilder builder = startBuilder();

            addField( builder, NODE_VERSION_ID_FIELD_NAME, entityVersionDocument.getNodeVersionId().toString() );
            addField( builder, ENTITY_ID_FIELD_NAME, entityVersionDocument.getEntityId().toString() );
            addField( builder, TIMESTAMP_ID_FIELD_NAME, Instant.now() );

            endBuilder( builder );
            return builder;
        }
        catch ( Exception e )
        {
            throw new IndexException( "Failed to build xContent for WorkspaceDocument", e );
        }

    }

}
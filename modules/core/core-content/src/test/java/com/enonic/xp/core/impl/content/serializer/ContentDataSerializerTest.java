package com.enonic.xp.core.impl.content.serializer;

import org.junit.Test;

import com.google.common.io.ByteSource;

import com.enonic.xp.attachment.CreateAttachment;
import com.enonic.xp.attachment.CreateAttachments;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentPropertyNames;
import com.enonic.xp.content.CreateContentTranslatorParams;
import com.enonic.xp.core.impl.content.serializer.ContentDataSerializer;
import com.enonic.xp.data.PropertySet;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.index.ChildOrder;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.security.PrincipalKey;

import static org.junit.Assert.*;

public class ContentDataSerializerTest
{
    @Test
    public void propertyTree_populated_with_attachment_properties()
        throws Exception
    {
        final ContentDataSerializer contentDataSerializer = new ContentDataSerializer();

        final String binaryName = "myName";
        final String binaryLabel = "myLabel";
        final String binaryMimeType = "myMimeType";
        final byte[] binaryData = "my binary".getBytes();

        final CreateContentTranslatorParams params = CreateContentTranslatorParams.create().
            parent( ContentPath.ROOT ).
            name( "myContentName" ).
            contentData( new PropertyTree() ).
            displayName( "myDisplayName" ).
            type( ContentTypeName.codeMedia() ).
            creator( PrincipalKey.ofAnonymous() ).
            childOrder( ChildOrder.defaultOrder() ).
            createAttachments( CreateAttachments.from( CreateAttachment.create().
                byteSource( ByteSource.wrap( binaryData ) ).
                label( binaryLabel ).
                mimeType( binaryMimeType ).
                name( binaryName ).
                build() ) ).
            build();

        final PropertyTree data = new PropertyTree();

        contentDataSerializer.toCreateNodeData( params, data.getRoot() );

        final PropertySet attachmentData = data.getSet( ContentPropertyNames.ATTACHMENT );
        assertNotNull( attachmentData );
        assertEquals( binaryName, attachmentData.getString( ContentPropertyNames.ATTACHMENT_NAME ) );
        assertEquals( binaryLabel, attachmentData.getString( ContentPropertyNames.ATTACHMENT_LABEL ) );
        assertEquals( binaryMimeType, attachmentData.getString( ContentPropertyNames.ATTACHMENT_MIMETYPE ) );
        assertEquals( binaryData.length + "", attachmentData.getString( ContentPropertyNames.ATTACHMENT_SIZE ) );
        assertEquals( binaryName, attachmentData.getString( ContentPropertyNames.ATTACHMENT_BINARY_REF ) );
    }
}
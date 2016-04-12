package com.enonic.xp.attachment;

import org.junit.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.io.ByteSource;

import com.enonic.xp.data.Property;
import com.enonic.xp.data.PropertyTree;

import static org.junit.Assert.*;

public class AttachmentSerializerTest
{
    @Test
    public void testName()
        throws Exception
    {
        final PropertyTree data = new PropertyTree();

        final CreateAttachments createAttachments = CreateAttachments.create().
            add( CreateAttachment.create().
                byteSource( ByteSource.wrap( "Hei".getBytes() ) ).
                label( "myLabel" ).
                mimeType( "text/plain" ).
                name( "myName" ).
                build() ).
            add( CreateAttachment.create().
                byteSource( ByteSource.wrap( "Hei2".getBytes() ) ).
                label( "myLabel2" ).
                mimeType( "text/plain" ).
                name( "myName2" ).
                build() ).
            build();

        AttachmentSerializer.create( data, createAttachments );

        final ImmutableList<Property> attachments = data.getProperties( "attachment" );

        assertEquals( 2, attachments.size() );
    }
}
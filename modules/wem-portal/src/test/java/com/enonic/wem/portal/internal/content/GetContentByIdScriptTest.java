package com.enonic.wem.portal.internal.content;

import java.time.Instant;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.api.account.UserKey;
import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.content.ContentService;
import com.enonic.wem.api.context.Context;
import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.script.AbstractScriptTest;

import static com.enonic.wem.api.content.Content.newContent;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;

public class GetContentByIdScriptTest
    extends AbstractScriptTest
{
    private ContentService contentService;

    @Before
    public void setUp()
    {
        this.contentService = Mockito.mock( ContentService.class );
        addHandler( new GetContentByIdHandler( this.contentService ) );
    }

    @Test
    public void getContentByIdTest()
    {
        final Content content = newContent().
            id( ContentId.from( "123" ) ).
            path( ContentPath.from( "/some/path" ) ).
            createdTime( Instant.now() ).
            owner( UserKey.from( "myStore:me" ) ).
            displayName( "My Content" ).
            modifiedTime( Instant.now() ).
            modifier( UserKey.superUser() ).
            type( ContentTypeName.from( "contenttype" ) ).
            build();
        Mockito.when( this.contentService.getById( eq( ContentId.from( "123" ) ), any( Context.class ) ) ).thenReturn( content );

        runTestScript( "getContentById-test.js" );
    }
}
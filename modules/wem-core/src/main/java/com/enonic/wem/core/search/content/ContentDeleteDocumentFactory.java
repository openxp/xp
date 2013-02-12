package com.enonic.wem.core.search.content;

import java.util.Collection;
import java.util.Set;

import com.google.common.collect.Sets;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.core.search.DeleteDocument;
import com.enonic.wem.core.search.IndexConstants;
import com.enonic.wem.core.search.IndexType;

public class ContentDeleteDocumentFactory
{

    private ContentDeleteDocumentFactory()
    {
    }

    public static Collection<DeleteDocument> create( final Content content )
    {
        Set<DeleteDocument> deleteDocuments = Sets.newLinkedHashSet();

        deleteDocuments.add( new DeleteDocument( IndexConstants.WEM_INDEX.value(), IndexType.CONTENT, content.getId().toString() ) );
        deleteDocuments.add( new DeleteDocument( IndexConstants.WEM_INDEX.value(), IndexType.BINARIES, content.getId().toString() ) );

        return deleteDocuments;
    }
}

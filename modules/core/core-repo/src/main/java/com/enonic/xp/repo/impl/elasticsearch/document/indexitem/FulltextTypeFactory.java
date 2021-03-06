package com.enonic.xp.repo.impl.elasticsearch.document.indexitem;

import java.util.List;

import com.google.common.collect.Lists;

import com.enonic.xp.data.Value;
import com.enonic.xp.index.IndexConfig;
import com.enonic.xp.index.IndexPath;

public class FulltextTypeFactory
{
    public static List<IndexItem> create( final IndexPath indexPath, final Value value, final IndexConfig indexConfig )
    {
        List<IndexItem> fulltextItems = Lists.newArrayList();

        if ( indexConfig.isDecideByType() )
        {
            if ( value.isText() )
            {
                fulltextItems.add( new IndexItemAnalyzed( indexPath, value.asString() ) );
                fulltextItems.add( new IndexItemNgram( indexPath, value.asString() ) );
            }
        }
        else
        {
            if ( indexConfig.isFulltext() )
            {
                fulltextItems.add( new IndexItemAnalyzed( indexPath, value.asString() ) );
            }

            if ( indexConfig.isnGram() )
            {
                fulltextItems.add( new IndexItemNgram( indexPath, value.asString() ) );
            }
        }

        return fulltextItems;
    }
}

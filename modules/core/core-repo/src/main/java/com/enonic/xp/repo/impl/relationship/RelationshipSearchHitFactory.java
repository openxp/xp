package com.enonic.xp.repo.impl.relationship;

import com.enonic.xp.relationship.Relationship;
import com.enonic.xp.relationship.RelationshipId;
import com.enonic.xp.repo.impl.search.result.SearchHit;

public class RelationshipSearchHitFactory
{

    static Relationship create( final SearchHit hit )
    {
        return Relationship.create().
            id( RelationshipId.from( hit.getId() ) ).
            source( RelationshipMemberFactory.create( hit.getStringValue( RelationshipIndexPath.SOURCE.getPath() ) ) ).
            target( RelationshipMemberFactory.create( hit.getStringValue( RelationshipIndexPath.TARGET.getPath() ) ) ).
            build();
    }

}

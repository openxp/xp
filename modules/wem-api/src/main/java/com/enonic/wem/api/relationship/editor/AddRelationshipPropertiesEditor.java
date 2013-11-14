package com.enonic.wem.api.relationship.editor;

import java.util.Map;

import com.enonic.wem.api.relationship.Relationship;

import static com.enonic.wem.api.relationship.Relationship.newRelationship;

final class AddRelationshipPropertiesEditor
    implements RelationshipEditor
{
    private final Map<String, String> source;

    AddRelationshipPropertiesEditor( final Map<String, String> source )
    {
        this.source = source;
    }

    @Override
    public Relationship edit( final Relationship relationship )
    {
        final Relationship.Builder builder = newRelationship( relationship );
        for ( Map.Entry<String, String> property : source.entrySet() )
        {
            builder.property( property.getKey(), property.getValue() );
        }
        return builder.build();
    }
}

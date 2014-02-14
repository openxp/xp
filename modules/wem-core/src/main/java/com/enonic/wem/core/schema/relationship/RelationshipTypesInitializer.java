package com.enonic.wem.core.schema.relationship;


import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.schema.relationship.CreateRelationshipType;
import com.enonic.wem.api.command.schema.relationship.UpdateRelationshipType;
import com.enonic.wem.api.schema.content.ContentTypeNames;
import com.enonic.wem.api.schema.relationship.RelationshipType;
import com.enonic.wem.api.schema.relationship.RelationshipTypeName;
import com.enonic.wem.api.schema.relationship.RelationshipTypeNames;
import com.enonic.wem.api.schema.relationship.editor.RelationshipTypeEditor;
import com.enonic.wem.core.support.BaseInitializer;

import static com.enonic.wem.api.schema.relationship.RelationshipType.newRelationshipType;


public class RelationshipTypesInitializer
    extends BaseInitializer
{
    private static final RelationshipType DEFAULT =
        createRelationshipType( RelationshipTypeName.DEFAULT, "Default", "relates to", "related of" );

    private static final RelationshipType PARENT = createRelationshipType( RelationshipTypeName.PARENT, "Parent", "parent of", "child of" );

    private static final RelationshipType LINK = createRelationshipType( RelationshipTypeName.LINK, "Link", "links to", "linked by" );

    private static final RelationshipType LIKE = createRelationshipType( RelationshipTypeName.LIKE, "Like", "likes", "liked by" );

    private static final RelationshipType CITATION =
        createRelationshipType( RelationshipTypeName.from( "citation" ), "Citation", "citation in", "cited by",
                                ContentTypeNames.from( "article" ) );

    private static final RelationshipType IMAGE =
        createRelationshipType( RelationshipTypeName.from( "image" ), "Image", "relates to image", "related of image",
                                ContentTypeNames.from( "image" ) );

    private static final RelationshipType[] SYSTEM_TYPES = {DEFAULT, PARENT, LINK, LIKE, CITATION, IMAGE};

    protected RelationshipTypesInitializer()
    {
        super( 10, "relationship-types" );
    }

    @Override
    public void initialize()
        throws Exception
    {
        for ( RelationshipType relationshipType : SYSTEM_TYPES )
        {
            relationshipType = RelationshipType.newRelationshipType( relationshipType ).
                icon( loadSchemaIcon( relationshipType.getName().toString() ) ).
                build();
            createOrUpdate( relationshipType );
        }
    }

    private void createOrUpdate( final RelationshipType relationshipType )
    {
        final RelationshipTypeNames relationshipTypeNames = RelationshipTypeNames.from( relationshipType.getName() );
        final boolean notExists = client.execute( Commands.relationshipType().exists().names( relationshipTypeNames ) ).isEmpty();
        if ( notExists )
        {
            final CreateRelationshipType createCommand = Commands.relationshipType().create();
            createCommand.
                name( relationshipType.getName() ).
                displayName( relationshipType.getDisplayName() ).
                fromSemantic( relationshipType.getFromSemantic() ).
                toSemantic( relationshipType.getToSemantic() ).
                allowedFromTypes( relationshipType.getAllowedFromTypes() ).
                allowedToTypes( relationshipType.getAllowedToTypes() ).
                schemaIcon( relationshipType.getIcon() );

            client.execute( createCommand );
        }
        else
        {
            final UpdateRelationshipType updateCommand = Commands.relationshipType().update();
            updateCommand.name( relationshipType.getName() );
            updateCommand.editor( new RelationshipTypeEditor()
            {
                @Override
                public RelationshipType edit( final RelationshipType relationshipType )
                {
                    return RelationshipType.newRelationshipType( relationshipType ).
                        displayName( relationshipType.getDisplayName() ).
                        fromSemantic( relationshipType.getFromSemantic() ).
                        toSemantic( relationshipType.getToSemantic() ).
                        addAllowedFromTypes( relationshipType.getAllowedFromTypes() ).
                        addAllowedToTypes( relationshipType.getAllowedToTypes() ).
                        icon( relationshipType.getIcon() ).
                        build();
                }
            } );

            client.execute( updateCommand );
        }
    }

    private static RelationshipType createRelationshipType( final RelationshipTypeName relationshipTypeName, final String displayName,
                                                            final String fromSemantic, final String toSemantic )
    {
        return createRelationshipType( relationshipTypeName, displayName, fromSemantic, toSemantic, ContentTypeNames.empty() );
    }

    private static RelationshipType createRelationshipType( final RelationshipTypeName relationshipTypeName, final String displayName,
                                                            final String fromSemantic, final String toSemantic,
                                                            final ContentTypeNames toContentTypes )
    {
        return newRelationshipType().
            name( relationshipTypeName ).
            displayName( displayName ).
            fromSemantic( fromSemantic ).
            toSemantic( toSemantic ).
            addAllowedToTypes( toContentTypes ).
            build();
    }
}

package com.enonic.wem.core.schema.metadata;

import java.io.InputStream;
import java.time.Instant;
import java.util.List;

import com.google.common.collect.Lists;

import com.enonic.wem.api.Icon;
import com.enonic.wem.api.form.Form;
import com.enonic.wem.api.form.Input;
import com.enonic.wem.api.form.inputtype.InputTypes;
import com.enonic.wem.api.schema.metadata.MetadataProvider;
import com.enonic.wem.api.schema.metadata.MetadataSchema;
import com.enonic.wem.api.schema.metadata.MetadataSchemaName;
import com.enonic.wem.api.schema.metadata.MetadataSchemas;

public final class CoreMetadataSchemasProvider
    implements MetadataProvider
{

    private static final String METADATA_SCHEMAS_FOLDER = "metadata-schemas";

    // System Metadata schemas
    private static final MetadataSchema MENU =
        MetadataSchema.newMetadataSchema().name( MetadataSchemaName.MENU ).displayName( "Menu" ).form( createMenuMetadataForm() ).build();

    private static final MetadataSchema[] METADATA_SCHEMAS = {MENU};

    private static Form createMenuMetadataForm()
    {
        return Form.newForm().
            addFormItem( Input.newInput().
                name( "menu" ).
                label( "Menu" ).
                inputType( InputTypes.CHECKBOX ).
                occurrences( 0, 1 ).
                helpText( "Check this to include this Page in the menu" ).
                build() ).
            addFormItem( Input.newInput().name( "menuName" ).
                inputType( InputTypes.TEXT_LINE ).
                label( "Menu name" ).
                occurrences( 0, 1 ).
                helpText( "Name to be used in menu. Optional" ).
                build() ).
            build();
    }

    private List<MetadataSchema> generateSystemMetadataSchemas()
    {
        final List<MetadataSchema> metadataSchemas = Lists.newArrayList();
        for ( MetadataSchema metadataSchema : METADATA_SCHEMAS )
        {
            metadataSchema = MetadataSchema.newMetadataSchema( metadataSchema ).
                icon( loadSchemaIcon( METADATA_SCHEMAS_FOLDER, metadataSchema.getName().getLocalName() ) ).
                build();
            metadataSchemas.add( metadataSchema );
        }
        return metadataSchemas;
    }

    @Override
    public MetadataSchemas get()
    {
        return MetadataSchemas.from( generateSystemMetadataSchemas() );
    }

    private Icon loadSchemaIcon( final String metaInfFolderName, final String name )
    {
        final String metaInfFolderBasePath = "/" + "META-INF" + "/" + metaInfFolderName;
        final String filePath = metaInfFolderBasePath + "/" + name.toLowerCase() + ".png";
        try
        {
            final InputStream stream = this.getClass().getResourceAsStream( filePath );
            if ( stream == null )
            {
                return null;
            }
            return Icon.from( stream, "image/png", Instant.now() );
        }
        catch ( Exception e )
        {
            throw new RuntimeException( "Failed to load icon file: " + filePath, e );
        }
    }

}

package com.enonic.wem.admin.rest.resource.schema.metadata;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.enonic.wem.admin.json.schema.metadata.MetadataSchemaJson;
import com.enonic.wem.api.schema.metadata.GetMetadataSchemaParams;
import com.enonic.wem.api.schema.metadata.MetadataSchema;
import com.enonic.wem.api.schema.metadata.MetadataSchemaName;
import com.enonic.wem.api.schema.metadata.MetadataSchemaService;

@Path("schema/metadata")
@Produces(MediaType.APPLICATION_JSON)
public class MetadataSchemaResource
{
    private MetadataSchemaService metadataSchemaService;

    private MetadataSchemaIconUrlResolver metadataSchemaIconUrlResolver;

    private MetadataSchemaIconResolver metadataSchemaIconResolver;


    @GET
    public MetadataSchemaJson get( @QueryParam("name") final String name )
    {
        final MetadataSchemaName metadataSchemaName = MetadataSchemaName.from( name );
        final MetadataSchema metadataSchema = fetchMetadataSchema( metadataSchemaName );

        if ( metadataSchema == null )
        {
            String message = String.format( "MetadataSchema [%s] was not found.", metadataSchemaName );
            throw new WebApplicationException( Response.status( Response.Status.NOT_FOUND ).
                entity( message ).type( MediaType.TEXT_PLAIN_TYPE ).build() );
        }

        return new MetadataSchemaJson( metadataSchema, this.metadataSchemaIconUrlResolver );
    }

    public MetadataSchema fetchMetadataSchema( final MetadataSchemaName name )
    {
        final GetMetadataSchemaParams params = new GetMetadataSchemaParams( name );
        return metadataSchemaService.getByName( params );
    }


    public void setMetadataSchemaService( final MetadataSchemaService metadataSchemaService )
    {
        this.metadataSchemaService = metadataSchemaService;
        this.metadataSchemaIconResolver = new MetadataSchemaIconResolver( metadataSchemaService );
        this.metadataSchemaIconUrlResolver = new MetadataSchemaIconUrlResolver( this.metadataSchemaIconResolver );
    }
}
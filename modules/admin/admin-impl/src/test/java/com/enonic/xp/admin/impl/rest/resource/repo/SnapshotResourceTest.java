package com.enonic.xp.admin.impl.rest.resource.repo;

import java.time.Instant;
import java.util.Arrays;

import javax.ws.rs.core.MediaType;

import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.xp.admin.impl.rest.resource.AdminResourceTestSupport;
import com.enonic.xp.node.DeleteSnapshotParams;
import com.enonic.xp.node.DeleteSnapshotsResult;
import com.enonic.xp.node.NodeService;
import com.enonic.xp.node.RestoreParams;
import com.enonic.xp.node.RestoreResult;
import com.enonic.xp.node.SnapshotParams;
import com.enonic.xp.node.SnapshotResult;
import com.enonic.xp.node.SnapshotResults;
import com.enonic.xp.repository.RepositoryId;

import static org.mockito.Matchers.isA;

public class SnapshotResourceTest
    extends AdminResourceTestSupport
{
    private NodeService nodeService;

    @Test
    public void snapshot()
        throws Exception
    {

        final SnapshotResult snapshotResult = SnapshotResult.create().
            name( "name" ).
            reason( "because reasons" ).
            timestamp( Instant.ofEpochMilli( 1438866915875L ) ).
            state( SnapshotResult.State.SUCCESS ).
            indices( Arrays.asList( "46f2a9" ) ).
            build();

        Mockito.when( this.nodeService.snapshot( isA( SnapshotParams.class ) ) ).thenReturn( snapshotResult );

        final String result = request().path( "repo/snapshot" ).
            entity( readFromFile( "snapshot_params.json" ), MediaType.APPLICATION_JSON_TYPE ).
            post().getAsString();

        assertJson( "snapshot.json", result );
    }

    @Test
    public void restore()
        throws Exception
    {
        final RestoreResult restoreResult = RestoreResult.create().
            repositoryId( RepositoryId.from( "repoId" ) ).
            name( "name" ).
            message( "He's dead, Jim." ).
            indices( Arrays.asList( "bc02aa" ) ).
            failed( false ).
            build();

        Mockito.when( this.nodeService.restore( isA( RestoreParams.class ) ) ).thenReturn( restoreResult );

        final String result = request().path( "repo/restore" ).
            entity( readFromFile( "restore_params.json" ), MediaType.APPLICATION_JSON_TYPE ).
            post().getAsString();

        assertJson( "restore.json", result );
    }

    @Test
    public void delete()
        throws Exception
    {
        final DeleteSnapshotsResult deleteResult = DeleteSnapshotsResult.create().
            add( "snapshot1" ).
            build();

        Mockito.when( this.nodeService.deleteSnapshot( isA( DeleteSnapshotParams.class ) ) ).thenReturn( deleteResult );

        final String result = request().path( "repo/delete" ).
            entity( readFromFile( "delete_params.json" ), MediaType.APPLICATION_JSON_TYPE ).
            post().getAsString();

        assertJson( "delete.json", result );
    }

    @Test
    public void list()
        throws Exception
    {
        final SnapshotResult snapshotResult1 = SnapshotResult.create().
            name( "name1" ).
            reason( "because reasons" ).
            timestamp( Instant.ofEpochMilli( 1438866915875L ) ).
            state( SnapshotResult.State.SUCCESS ).
            indices( Arrays.asList( "ac27b1" ) ).
            build();

        final SnapshotResults snapshotResults = SnapshotResults.create().
            add( snapshotResult1 ).
            build();

        Mockito.when( this.nodeService.listSnapshots() ).thenReturn( snapshotResults );

        String result = request().path( "repo/list" ).get().getAsString();

        assertJson( "list.json", result );
    }


    @Override
    protected Object getResourceInstance()
    {
        this.nodeService = Mockito.mock( NodeService.class );

        final SnapshotResource resource = new SnapshotResource();
        resource.setNodeService( nodeService );
        return resource;
    }
}

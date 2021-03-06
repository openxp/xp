package com.enonic.xp.admin.impl.rest.resource.security;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jparsec.util.Lists;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.admin.impl.rest.resource.ResourceConstants;
import com.enonic.xp.admin.impl.rest.resource.security.json.CreateGroupJson;
import com.enonic.xp.admin.impl.rest.resource.security.json.CreateRoleJson;
import com.enonic.xp.admin.impl.rest.resource.security.json.CreateUserJson;
import com.enonic.xp.admin.impl.rest.resource.security.json.CreateUserStoreJson;
import com.enonic.xp.admin.impl.rest.resource.security.json.DeletePrincipalJson;
import com.enonic.xp.admin.impl.rest.resource.security.json.DeletePrincipalResultJson;
import com.enonic.xp.admin.impl.rest.resource.security.json.DeletePrincipalsResultJson;
import com.enonic.xp.admin.impl.rest.resource.security.json.DeleteUserStoreJson;
import com.enonic.xp.admin.impl.rest.resource.security.json.DeleteUserStoreResultJson;
import com.enonic.xp.admin.impl.rest.resource.security.json.DeleteUserStoresResultJson;
import com.enonic.xp.admin.impl.rest.resource.security.json.EmailAvailabilityJson;
import com.enonic.xp.admin.impl.rest.resource.security.json.GroupJson;
import com.enonic.xp.admin.impl.rest.resource.security.json.PrincipalJson;
import com.enonic.xp.admin.impl.rest.resource.security.json.PrincipalsJson;
import com.enonic.xp.admin.impl.rest.resource.security.json.RoleJson;
import com.enonic.xp.admin.impl.rest.resource.security.json.UpdateGroupJson;
import com.enonic.xp.admin.impl.rest.resource.security.json.UpdatePasswordJson;
import com.enonic.xp.admin.impl.rest.resource.security.json.UpdateRoleJson;
import com.enonic.xp.admin.impl.rest.resource.security.json.UpdateUserJson;
import com.enonic.xp.admin.impl.rest.resource.security.json.UpdateUserStoreJson;
import com.enonic.xp.admin.impl.rest.resource.security.json.UserJson;
import com.enonic.xp.admin.impl.rest.resource.security.json.UserStoreJson;
import com.enonic.xp.admin.impl.rest.resource.security.json.UserStoresJson;
import com.enonic.xp.jaxrs.JaxRsComponent;
import com.enonic.xp.jaxrs.JaxRsExceptions;
import com.enonic.xp.security.Group;
import com.enonic.xp.security.Principal;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.PrincipalKeys;
import com.enonic.xp.security.PrincipalQuery;
import com.enonic.xp.security.PrincipalQueryResult;
import com.enonic.xp.security.PrincipalRelationship;
import com.enonic.xp.security.PrincipalRelationships;
import com.enonic.xp.security.PrincipalType;
import com.enonic.xp.security.Principals;
import com.enonic.xp.security.Role;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.SecurityService;
import com.enonic.xp.security.User;
import com.enonic.xp.security.UserStore;
import com.enonic.xp.security.UserStoreKey;
import com.enonic.xp.security.UserStores;
import com.enonic.xp.security.acl.UserStoreAccessControlList;

import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang.StringUtils.isBlank;


@SuppressWarnings("UnusedDeclaration")
@Path(ResourceConstants.REST_ROOT + "security")
@Produces(MediaType.APPLICATION_JSON)
@RolesAllowed(RoleKeys.ADMIN_LOGIN_ID)
@Component(immediate = true)
public final class SecurityResource
    implements JaxRsComponent
{
    private SecurityService securityService;

    @GET
    @Path("userstore/list")
    public UserStoresJson getUserStores()
    {
        final UserStores userStores = securityService.getUserStores();
        return new UserStoresJson( userStores );
    }

    @GET
    @Path("userstore")
    public UserStoreJson getUserStore( @QueryParam("key") final String keyParam )
    {
        if ( keyParam == null )
        {
            return null;
        }

        final UserStoreKey userStoreKey = UserStoreKey.from( keyParam );
        final UserStore userStore = securityService.getUserStore( userStoreKey );
        if ( userStore == null )
        {
            throw JaxRsExceptions.notFound( String.format( "User Store [%s] not found", keyParam ) );
        }

        final UserStoreAccessControlList userStorePermissions = securityService.getUserStorePermissions( userStoreKey );

        final Principals principals = securityService.getPrincipals( userStorePermissions.getAllPrincipals() );
        return new UserStoreJson( userStore, userStorePermissions, principals );
    }

    @GET
    @Path("userstore/default")
    public UserStoreJson getDefaultUserStore()
    {
        final UserStore userStore = UserStore.create().displayName( "" ).key( UserStoreKey.createDefault() ).build();

        final UserStoreAccessControlList userStorePermissions = securityService.getDefaultUserStorePermissions();

        final Principals principals = securityService.getPrincipals( userStorePermissions.getAllPrincipals() );
        return new UserStoreJson( userStore, userStorePermissions, principals );
    }

    @POST
    @Path("userstore/create")
    public UserStoreJson createUserStore( final CreateUserStoreJson params )
    {
        final UserStore userStore = securityService.createUserStore( params.getCreateUserStoreParams() );
        final UserStoreAccessControlList permissions = securityService.getUserStorePermissions( userStore.getKey() );

        final Principals principals = securityService.getPrincipals( permissions.getAllPrincipals() );
        return new UserStoreJson( userStore, permissions, principals );
    }

    @POST
    @Path("userstore/update")
    public UserStoreJson updateUserStore( final UpdateUserStoreJson params )
    {
        final UserStore userStore = securityService.updateUserStore( params.getUpdateUserStoreParams() );
        final UserStoreAccessControlList permissions = securityService.getUserStorePermissions( userStore.getKey() );

        final Principals principals = securityService.getPrincipals( permissions.getAllPrincipals() );
        return new UserStoreJson( userStore, permissions, principals );
    }

    @POST
    @Path("userstore/delete")
    public DeleteUserStoresResultJson deleteUserStore( final DeleteUserStoreJson params )
    {
        final DeleteUserStoresResultJson resultsJson = new DeleteUserStoresResultJson();
        params.getKeys().stream().map( UserStoreKey::from ).forEach( ( userStoreKey ) -> {
            try
            {
                securityService.deleteUserStore( userStoreKey );
                resultsJson.add( DeleteUserStoreResultJson.success( userStoreKey ) );
            }
            catch ( Exception e )
            {
                resultsJson.add( DeleteUserStoreResultJson.failure( userStoreKey, e.getMessage() ) );
            }
        } );
        return resultsJson;
    }

    @GET
    @Path("principals")
    public PrincipalsJson findPrincipals( @QueryParam("userStoreKey") final String storeKey, @QueryParam("types") final String types,
                                          @QueryParam("query") final String query )
    {

        UserStoreKey userStoreKey = null;
        if ( StringUtils.isNotEmpty( storeKey ) )
        {
            userStoreKey = UserStoreKey.from( storeKey );
        }
        final List<PrincipalType> principalTypes = new ArrayList<>();
        if ( StringUtils.isNotBlank( types ) )
        {
            final String[] typeItems = types.split( "," );
            for ( String typeItem : typeItems )
            {
                try
                {
                    principalTypes.add( PrincipalType.valueOf( typeItem.toUpperCase() ) );
                }
                catch ( IllegalArgumentException e )
                {
                    throw new WebApplicationException( "Invalid principal type: " + typeItem );
                }
            }
        }
        final Principals principals = securityService.findPrincipals( userStoreKey, principalTypes, query );
        return new PrincipalsJson( principals );
    }

    @GET
    @Path("principals/{key:.+}")
    public PrincipalJson getPrincipalByKey( @PathParam("key") final String keyParam,
                                            @QueryParam("memberships") final String resolveMembershipsParam )
    {
        final boolean resolveMemberships = "true".equals( resolveMembershipsParam );
        final PrincipalKey principalKey = PrincipalKey.from( keyParam );
        final Optional<? extends Principal> principalResult = securityService.getPrincipal( principalKey );

        if ( !principalResult.isPresent() )
        {
            throw JaxRsExceptions.notFound( String.format( "Principal [%s] was not found", keyParam ) );
        }

        final Principal principal = principalResult.get();
        switch ( principalKey.getType() )
        {
            case USER:
                if ( resolveMemberships )
                {
                    final PrincipalKeys membershipKeys = securityService.getMemberships( principalKey );
                    final Principals memberships = securityService.getPrincipals( membershipKeys );
                    return new UserJson( (User) principal, memberships );
                }
                else
                {
                    return new UserJson( (User) principal );
                }

            case GROUP:
                final PrincipalKeys groupMembers = getMembers( principalKey );
                return new GroupJson( (Group) principal, groupMembers );

            case ROLE:
                final PrincipalKeys roleMembers = getMembers( principalKey );
                return new RoleJson( (Role) principal, roleMembers );
        }

        throw JaxRsExceptions.notFound( String.format( "Principal [%s] was not found", keyParam ) );
    }

    @GET
    @Path("principals/emailAvailable")
    public EmailAvailabilityJson isEmailAvailable( @QueryParam("userStoreKey") final String userStoreKeyParam,
                                                   @QueryParam("email") final String email )
    {
        if ( isBlank( email ) )
        {
            throw new WebApplicationException( "Expected email parameter" );
        }
        final UserStoreKey userStoreKey = isBlank( userStoreKeyParam ) ? UserStoreKey.system() : UserStoreKey.from( userStoreKeyParam );
        final PrincipalQuery query = PrincipalQuery.create().email( email ).userStore( userStoreKey ).build();
        final PrincipalQueryResult queryResult = securityService.query( query );
        return new EmailAvailabilityJson( queryResult.isEmpty() );
    }

    @POST
    @Path("principals/createUser")
    public UserJson createUser( final CreateUserJson params )
    {
        if ( StringUtils.isEmpty( params.password ) )
        {
            throw new WebApplicationException( "Password has not been set." );
        }

        final User user = this.securityService.createUser( params.toCreateUserParams() );
        final PrincipalKey userKey = user.getKey();

        this.securityService.setPassword( userKey, params.password );

        for ( final PrincipalKey membershipToAdd : params.toMembershipKeys() )
        {
            final PrincipalRelationship rel = PrincipalRelationship.from( membershipToAdd ).to( userKey );
            this.securityService.addRelationship( rel );
        }

        final Principals memberships = this.securityService.getPrincipals( this.securityService.getMemberships( userKey ) );
        return new UserJson( user, memberships );
    }

    @POST
    @Path("principals/createGroup")
    public GroupJson createGroup( final CreateGroupJson params )
    {
        final Group group = securityService.createGroup( params.toCreateGroupParams() );
        final PrincipalKey groupKey = group.getKey();
        final PrincipalKeys members = params.toMemberKeys();

        for ( final PrincipalKey member : members )
        {
            final PrincipalRelationship rel = PrincipalRelationship.from( groupKey ).to( member );
            securityService.addRelationship( rel );
        }

        return new GroupJson( group, members );
    }

    @POST
    @Path("principals/createRole")
    public RoleJson createRole( final CreateRoleJson params )
    {
        final Role role = securityService.createRole( params.toCreateRoleParams() );
        final PrincipalKey roleKey = role.getKey();
        final PrincipalKeys members = params.toMemberKeys();

        for ( final PrincipalKey member : members )
        {
            final PrincipalRelationship rel = PrincipalRelationship.from( roleKey ).to( member );
            securityService.addRelationship( rel );
        }

        return new RoleJson( role, members );
    }

    @POST
    @Path("principals/updateUser")
    public UserJson updateUser( final UpdateUserJson params )
    {
        final User user = securityService.updateUser( params.getUpdateUserParams() );

        final PrincipalKey userKey = user.getKey();
        for ( PrincipalKey membershipToAdd : params.getAddMemberships() )
        {
            final PrincipalRelationship rel = PrincipalRelationship.from( membershipToAdd ).to( userKey );
            securityService.addRelationship( rel );
        }
        for ( PrincipalKey membershipToRemove : params.getRemoveMemberships() )
        {
            final PrincipalRelationship rel = PrincipalRelationship.from( membershipToRemove ).to( userKey );
            securityService.removeRelationship( rel );
        }

        final Principals memberships = securityService.getPrincipals( securityService.getMemberships( userKey ) );
        return new UserJson( user, memberships );
    }

    @POST
    @Path("principals/setPassword")
    public UserJson setPassword( final UpdatePasswordJson params )
    {
        final PrincipalKey userKey = params.getUserKey();

        if ( StringUtils.isEmpty( params.getPassword() ) )
        {
            throw new WebApplicationException( "Password has not been set." );
        }

        final User user = securityService.setPassword( userKey, params.getPassword() );
        return new UserJson( user );
    }

    @POST
    @Path("principals/updateGroup")
    public GroupJson updateGroup( final UpdateGroupJson params )
    {
        final Group group = securityService.updateGroup( params.getUpdateGroupParams() );
        final PrincipalKey groupKey = group.getKey();

        updateMemberships( groupKey, params.getRemoveMembers(), params.getAddMembers() );

        final PrincipalKeys groupMembers = getMembers( groupKey );
        return new GroupJson( group, groupMembers );
    }

    @POST
    @Path("principals/updateRole")
    public RoleJson updateRole( final UpdateRoleJson params )
    {
        final Role role = securityService.updateRole( params.getUpdateRoleParams() );
        final PrincipalKey roleKey = role.getKey();

        updateMemberships( roleKey, params.getRemoveMembers(), params.getAddMembers() );

        final PrincipalKeys roleMembers = getMembers( roleKey );
        return new RoleJson( role, roleMembers );
    }

    @POST
    @Path("principals/delete")
    public DeletePrincipalsResultJson deletePrincipals( final DeletePrincipalJson principalKeysParam )
    {
        final DeletePrincipalsResultJson resultsJson = new DeletePrincipalsResultJson();
        principalKeysParam.getKeys().stream().map( PrincipalKey::from ).forEach( ( principalKey ) -> {
            try
            {
                securityService.deletePrincipal( principalKey );
                resultsJson.add( DeletePrincipalResultJson.success( principalKey ) );
            }
            catch ( Exception e )
            {
                resultsJson.add( DeletePrincipalResultJson.failure( principalKey, e.getMessage() ) );
            }
        } );
        return resultsJson;
    }

    private void updateMemberships( final PrincipalKey target, PrincipalKeys membersToRemove, PrincipalKeys membersToAdd )
    {
        for ( PrincipalKey memberToAdd : membersToAdd )
        {
            final PrincipalRelationship rel = PrincipalRelationship.from( target ).to( memberToAdd );
            securityService.addRelationship( rel );
        }

        for ( PrincipalKey memberToRemove : membersToRemove )
        {
            final PrincipalRelationship rel = PrincipalRelationship.from( target ).to( memberToRemove );
            securityService.removeRelationship( rel );
        }
    }

    private PrincipalKeys getMembers( final PrincipalKey principal )
    {
        final PrincipalRelationships relationships = this.securityService.getRelationships( principal );
        final List<PrincipalKey> members = relationships.stream().map( PrincipalRelationship::getTo ).collect( toList() );
        return PrincipalKeys.from( members );
    }

    private PrincipalKeys getUserMembers( final PrincipalKey principal )
    {
        PrincipalKeys members = this.getMembers( principal );

        List<PrincipalKey> subMembers = Lists.arrayList();
        members.stream().filter( member -> member.isGroup() || member.isRole() ).forEach( member -> {
            subMembers.addAll( getUserMembers( member ).getSet() );
        } );
        members = PrincipalKeys.from( members, subMembers );

        return PrincipalKeys.from( members.stream().filter( PrincipalKey::isUser ).collect( toList() ) );
    }


    @Reference
    public void setSecurityService( final SecurityService securityService )
    {
        this.securityService = securityService;
    }
}

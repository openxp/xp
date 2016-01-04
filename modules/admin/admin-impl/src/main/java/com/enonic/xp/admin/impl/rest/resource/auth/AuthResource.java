package com.enonic.xp.admin.impl.rest.resource.auth;

import java.util.List;
import java.util.stream.Collectors;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.admin.impl.app.AdminApplicationsRegistry;
import com.enonic.xp.admin.impl.rest.resource.ResourceConstants;
import com.enonic.xp.admin.impl.rest.resource.auth.json.AuthServiceJson;
import com.enonic.xp.admin.impl.rest.resource.auth.json.LoginResultJson;
import com.enonic.xp.admin.impl.security.AuthHelper;
import com.enonic.xp.auth.AuthServiceRegistry;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.jaxrs.JaxRsComponent;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.SecurityService;
import com.enonic.xp.security.auth.AuthenticationInfo;
import com.enonic.xp.session.Session;

@Path(ResourceConstants.REST_ROOT + "auth")
@Produces(MediaType.APPLICATION_JSON)
@Component(immediate = true)
public final class AuthResource
    implements JaxRsComponent
{
    private final AdminApplicationsRegistry appRegistry;

    private SecurityService securityService;

    private AuthServiceRegistry authServiceRegistry;

    public AuthResource()
    {
        this.appRegistry = new AdminApplicationsRegistry();
    }

    @POST
    @Path("login")
    public LoginResultJson login( final LoginRequest request )
    {
        final AuthHelper helper = new AuthHelper( this.securityService );
        final AuthenticationInfo authInfo = helper.login( request.getUser(), request.getPassword(), request.isRememberMe() );

        if ( authInfo.isAuthenticated() && !authInfo.hasRole( RoleKeys.ADMIN_LOGIN ) )
        {
            AuthHelper.logout();
            return new LoginResultJson( AuthenticationInfo.unAuthenticated(), "Access Denied" );
        }
        if ( !authInfo.isAuthenticated() )
        {
            return new LoginResultJson( AuthenticationInfo.unAuthenticated() );
        }

        return new LoginResultJson( authInfo, this.appRegistry.getAllowedApplications( authInfo.getPrincipals() ) );
    }

    @POST
    @Path("logout")
    public void logout()
    {
        AuthHelper.logout();
    }

    @GET
    @Path("authenticated")
    public LoginResultJson isAuthenticated()
    {
        final Session session = ContextAccessor.current().getLocalScope().getSession();
        if ( session == null )
        {
            return new LoginResultJson( AuthenticationInfo.unAuthenticated() );
        }

        final AuthenticationInfo authInfo = ContextAccessor.current().getAuthInfo();
        return new LoginResultJson( authInfo, appRegistry.getAllowedApplications( authInfo.getPrincipals() ) );
    }

    @GET
    @Path("services")
    public List<AuthServiceJson> services()
    {
        return authServiceRegistry.getAuthServices().
            stream().
            map( authService -> new AuthServiceJson( authService.getKey(), authService.getDisplayName() ) ).
            collect( Collectors.toList() );
    }

    @Reference
    public void setSecurityService( final SecurityService securityService )
    {
        this.securityService = securityService;
    }

    @Reference
    public void setAuthServiceRegistry( final AuthServiceRegistry authServiceRegistry )
    {
        this.authServiceRegistry = authServiceRegistry;
    }
}

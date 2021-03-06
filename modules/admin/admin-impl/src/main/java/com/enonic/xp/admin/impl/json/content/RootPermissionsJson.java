package com.enonic.xp.admin.impl.json.content;

import java.util.ArrayList;
import java.util.List;

import com.enonic.xp.admin.impl.rest.resource.content.ContentPrincipalsResolver;
import com.enonic.xp.admin.impl.rest.resource.content.json.AccessControlEntryJson;
import com.enonic.xp.security.Principals;
import com.enonic.xp.security.acl.AccessControlEntry;
import com.enonic.xp.security.acl.AccessControlList;

@SuppressWarnings("UnusedDeclaration")
public final class RootPermissionsJson
{
    private final List<AccessControlEntryJson> accessControlList;

    public RootPermissionsJson( final AccessControlList contentPermissions, final ContentPrincipalsResolver contentPrincipalsResolver )
    {
        final Principals principals = contentPrincipalsResolver.resolveAccessControlListPrincipals( contentPermissions );
        this.accessControlList = aclToJson( contentPermissions, principals );
    }

    private List<AccessControlEntryJson> aclToJson( final AccessControlList acl, final Principals principals )
    {
        final List<AccessControlEntryJson> jsonList = new ArrayList<>();
        for ( AccessControlEntry entry : acl )
        {
            jsonList.add( new AccessControlEntryJson( entry, principals.getPrincipal( entry.getPrincipal() ) ) );
        }
        return jsonList;
    }

    public List<AccessControlEntryJson> getPermissions()
    {
        return this.accessControlList;
    }

}

package com.enonic.wem.api.content.site;


import com.enonic.wem.Version;
import com.enonic.wem.api.module.ModuleKeys;
import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.api.schema.content.ContentTypeNames;

public final class SiteTemplate
{
    private final SiteTemplateId id;

    private final SiteTemplateName name;

    private final Version version;

    private final String displayName;

    private final String description;

    private final Vendor vendor;

    private final ModuleKeys modules;

    private final ContentTypeNames supportedContentTypes;

    private final ContentTypeName rootContentType;

    private SiteTemplate( final Builder builder )
    {
        this.name = builder.name;
        this.id = builder.id;
        this.version = builder.version;
        this.displayName = builder.displayName;
        this.description = builder.description;
        this.vendor = builder.vendor;
        this.modules = builder.modules;
        this.supportedContentTypes = builder.supportedContentTypes;
        this.rootContentType = builder.rootContentType;
    }

    public SiteTemplateId getId()
    {
        return id;
    }

    public SiteTemplateName getName()
    {
        return name;
    }

    public Version getVersion()
    {
        return version;
    }

    public String getDisplayName()
    {
        return displayName;
    }

    public String getDescription()
    {
        return description;
    }

    public Vendor getVendor()
    {
        return vendor;
    }

    public ModuleKeys getModules()
    {
        return modules;
    }

    public ContentTypeNames getSupportedContentTypes()
    {
        return supportedContentTypes;
    }

    public ContentTypeName getRootContentType()
    {
        return rootContentType;
    }

    public static Builder newSiteTemplate()
    {
        return new Builder();
    }

    public static class Builder
    {
        private Version version;

        private SiteTemplateId id;

        private SiteTemplateName name;

        private String displayName;

        private String description;

        private Vendor vendor;

        private ModuleKeys modules = ModuleKeys.empty();

        private ContentTypeNames supportedContentTypes = ContentTypeNames.empty();

        private ContentTypeName rootContentType;

        private Builder()
        {
        }

        public Builder id( final SiteTemplateId id )
        {
            this.id = id;
            return this;
        }

        public Builder name( final SiteTemplateName name )
        {
            this.name = name;
            return this;
        }

        public Builder version( final Version version )
        {
            this.version = version;
            return this;
        }

        public Builder displayName( final String displayName )
        {
            this.displayName = displayName;
            return this;
        }

        public Builder description( final String description )
        {
            this.description = description;
            return this;
        }

        public Builder vendor( final Vendor vendor )
        {
            this.vendor = vendor;
            return this;
        }

        public Builder modules( final ModuleKeys modules )
        {
            this.modules = modules;
            return this;
        }

        public Builder supportedContentTypes( final ContentTypeNames supportedContentTypes )
        {
            this.supportedContentTypes = supportedContentTypes;
            return this;
        }

        public Builder rootContentType( final ContentTypeName rootContentType )
        {
            this.rootContentType = rootContentType;
            return this;
        }

        public SiteTemplate build()
        {
            return new SiteTemplate( this );
        }
    }
}

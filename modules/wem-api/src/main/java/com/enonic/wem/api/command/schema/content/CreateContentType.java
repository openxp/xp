package com.enonic.wem.api.command.schema.content;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

import com.enonic.wem.api.Icon;
import com.enonic.wem.api.command.Command;
import com.enonic.wem.api.form.Form;
import com.enonic.wem.api.schema.content.ContentType;
import com.enonic.wem.api.schema.content.ContentTypeName;

public final class CreateContentType
    extends Command<ContentType>
{
    private String name;

    private String displayName;

    private ContentTypeName superType;

    private boolean isAbstract;

    private boolean isFinal;

    private boolean allowChildContent = true;

    private boolean isBuiltIn = false;

    private Form form;

    private Icon icon;

    private String contentDisplayNameScript;

    public String getName()
    {
        return name;
    }


    public CreateContentType name( final String name )
    {
        this.name = name;
        return this;
    }

    public String getDisplayName()
    {
        return displayName;
    }

    public CreateContentType displayName( final String displayName )
    {
        this.displayName = displayName;
        return this;
    }

    public ContentTypeName getSuperType()
    {
        return superType;
    }

    public CreateContentType superType( final ContentTypeName superType )
    {
        this.superType = superType;
        return this;
    }

    public boolean isAbstract()
    {
        return isAbstract;
    }

    public CreateContentType setAbstract( final boolean isAbstract )
    {
        this.isAbstract = isAbstract;
        return this;
    }

    public boolean isFinal()
    {
        return isFinal;
    }

    public CreateContentType setFinal( final boolean isFinal )
    {
        this.isFinal = isFinal;
        return this;
    }

    public boolean getAllowChildContent()
    {
        return allowChildContent;
    }

    public CreateContentType allowChildContent( final boolean value )
    {
        this.allowChildContent = value;
        return this;
    }

    public boolean isBuiltIn()
    {
        return isBuiltIn;
    }

    public CreateContentType builtIn( final boolean builtIn )
    {
        isBuiltIn = builtIn;
        return this;
    }

    public Form getForm()
    {
        return form;
    }

    public CreateContentType form( final Form form )
    {
        this.form = form;
        return this;
    }

    public Icon getIcon()
    {
        return icon;
    }

    public CreateContentType icon( final Icon icon )
    {
        this.icon = icon;
        return this;
    }


    public String getContentDisplayNameScript()
    {
        return contentDisplayNameScript;
    }

    public CreateContentType contentDisplayNameScript( final String contentDisplayNameScript )
    {
        this.contentDisplayNameScript = contentDisplayNameScript;
        return this;
    }

    @Override
    public boolean equals( final Object o )
    {
        if ( this == o )
        {
            return true;
        }

        if ( !( o instanceof CreateContentType ) )
        {
            return false;
        }

        final CreateContentType that = (CreateContentType) o;
        return Objects.equal( this.name, that.name ) && Objects.equal( this.displayName, that.displayName ) &&
            Objects.equal( this.superType, that.superType ) && Objects.equal( this.isAbstract, that.isAbstract ) &&
            Objects.equal( this.isFinal, that.isFinal ) && Objects.equal( this.allowChildContent, that.allowChildContent ) &&
            Objects.equal( this.isBuiltIn, that.isBuiltIn ) &&
            Objects.equal( this.form, that.form ) &&
            Objects.equal( this.icon, that.icon );
    }

    @Override
    public int hashCode()
    {
        return Objects.hashCode( name, displayName, superType, isAbstract, isFinal, allowChildContent, isBuiltIn, form, icon );
    }

    @Override
    public void validate()
    {
        Preconditions.checkNotNull( this.name, "name cannot be null" );
        Preconditions.checkNotNull( this.displayName, "displayName cannot be null" );
    }
}

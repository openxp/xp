package com.enonic.wem.api.content.space.editor;

import com.enonic.wem.api.content.space.Space;

final class CompositeSpaceEditor
    implements SpaceEditor
{
    private final SpaceEditor[] editors;

    CompositeSpaceEditor( final SpaceEditor... editors )
    {
        this.editors = editors;
    }

    @Override
    public Space edit( final Space space )
        throws Exception
    {
        boolean modified = false;
        Space spaceEdited = space;
        for ( final SpaceEditor editor : this.editors )
        {
            final Space updatedSpace = editor.edit( spaceEdited );
            if ( updatedSpace != null )
            {
                spaceEdited = updatedSpace;
                modified = true;
            }
        }
        return modified ? spaceEdited : null;
    }
}

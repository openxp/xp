package com.enonic.xp.export;

import com.google.common.annotations.Beta;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.vfs.VirtualFile;

@Beta
public final class ContentImportParams
{
    private final ContentPath targetPath;

    private final Branch targetBranch;

    private final VirtualFile source;

    private ContentImportParams( final Builder builder )
    {
        this.targetPath = builder.targetPath;
        this.targetBranch = builder.targetBranch;
        this.source = builder.source;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public ContentPath getTargetPath()
    {
        return targetPath;
    }

    public VirtualFile getSource()
    {
        return source;
    }

    public Branch getTargetBranch()
    {
        return targetBranch;
    }

    public static final class Builder
    {
        private ContentPath targetPath;

        private VirtualFile source;

        private Branch targetBranch;

        private Builder()
        {
        }

        public Builder targetPath( ContentPath targetPath )
        {
            this.targetPath = targetPath;
            return this;
        }

        public Builder targetBranch( final Branch targetBranch )
        {
            this.targetBranch = targetBranch;
            return this;
        }

        public Builder source( final VirtualFile source )
        {
            this.source = source;
            return this;
        }

        public ContentImportParams build()
        {
            return new ContentImportParams( this );
        }
    }
}

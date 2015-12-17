package com.enonic.xp.export;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.common.annotations.Beta;
import com.google.common.collect.Lists;

import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentPaths;

@Beta
public final class ContentImportResult
{
    public final ContentPaths added;

    public final ContentPaths updated;

    private List<ImportError> importErrors = Lists.newArrayList();

    private ContentImportResult( final Builder builder )
    {
        this.added = ContentPaths.from( builder.addedNodes );
        this.updated = ContentPaths.from( builder.updatedNodes );
        this.importErrors = builder.importErrors;
    }

    public List<ImportError> getImportErrors()
    {
        return importErrors;
    }

    public ContentPaths getAddedContent()
    {
        return added;
    }

    public ContentPaths getUpdatedContent()
    {
        return updated;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static Builder copyOf( final ContentImportResult result )
    {
        return new Builder( result );
    }

    @Override
    public String toString()
    {
        return "NodeImportResult{" +
            ", addedNodes=" + added +
            ", updateNodes=" + updated +
            ", importErrors=" + Arrays.toString( importErrors.toArray() ) +
            '}';
    }

    public static final class Builder
    {
        private final List<ContentPath> addedNodes = new ArrayList<>();

        private final List<ContentPath> updatedNodes = new ArrayList<>();

        private final List<ImportError> importErrors = new ArrayList<>();

        private Builder()
        {
        }

        private Builder( final ContentImportResult source )
        {
            for ( ContentPath added : source.getAddedContent() )
            {
                this.addedNodes.add( added );
            }
            for ( ContentPath updated : source.getUpdatedContent() )
            {
                this.updatedNodes.add( updated );
            }
            this.importErrors.addAll( source.getImportErrors() );
        }

        public Builder added( ContentPath contentPath )
        {
            this.addedNodes.add( contentPath );
            return this;
        }

        public Builder updated( final ContentPath contentPath )
        {
            this.updatedNodes.add( contentPath );
            return this;
        }

        public Builder addError( final Exception e )
        {
            this.importErrors.add( new ImportError( e, null ) );
            return this;
        }

        public Builder addError( final String message, final Exception e )
        {
            this.importErrors.add( new ImportError( e, message ) );
            return this;
        }

        public Builder addError( final String message, final String e )
        {
            this.importErrors.add( new ImportError( e, message ) );
            return this;
        }

        public ContentImportResult build()
        {
            return new ContentImportResult( this );
        }
    }

    public static final class ImportError
    {
        private final String exception;

        private final String message;

        public ImportError( final Exception exception, final String message )
        {
            this.exception = exception.toString();
            this.message = message;
        }

        public ImportError( final String exception, final String message )
        {
            this.exception = exception;
            this.message = message;
        }

        public String getException()
        {
            return exception;
        }

        public String getMessage()
        {
            return message;
        }

        @Override
        public String toString()
        {
            return "ImportError{" +
                "exception=" + exception +
                ", message='" + message + '\'' +
                '}';
        }
    }

}
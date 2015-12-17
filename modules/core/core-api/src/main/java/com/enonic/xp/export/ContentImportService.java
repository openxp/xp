package com.enonic.xp.export;

import com.google.common.annotations.Beta;

@Beta
public interface ContentImportService
{
    ContentImportResult importContent( final ContentImportParams params );
}

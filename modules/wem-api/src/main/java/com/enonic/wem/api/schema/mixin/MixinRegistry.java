package com.enonic.wem.api.schema.mixin;

import com.enonic.wem.api.module.ModuleKey;

public interface MixinRegistry
{

    Mixin getMixin( MixinName mixinName );

    Mixins getMixinsByModule( ModuleKey moduleKey );

    Mixins getAllMixins();

}

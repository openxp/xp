package com.enonic.xp.inputtype;

import org.apache.commons.lang.StringUtils;

import com.enonic.xp.data.Property;
import com.enonic.xp.data.Value;
import com.enonic.xp.data.ValueFactory;
import com.enonic.xp.data.ValueTypes;

final class RadioButtonType
    extends InputTypeBase
{
    public final static RadioButtonType INSTANCE = new RadioButtonType();

    private RadioButtonType()
    {
        super( InputTypeName.RADIO_BUTTON );
    }

    @Override
    public Value createValue( final Value value, final InputTypeConfig config )
    {
        return ValueFactory.newString( value.asString() );
    }

    @Override
    public Value createDefaultValue( final InputTypeDefault defaultConfig )
    {
        final String defaultValue = defaultConfig.getRootValue();
        if ( StringUtils.isNotEmpty( defaultValue ) )
        {
            return ValueFactory.newString( defaultValue );
        }
        return super.createDefaultValue( defaultConfig );
    }

    @Override
    public void validate( final Property property, final InputTypeConfig config )
    {
        validateType( property, ValueTypes.STRING );

        final String valueAsString = property.getString();
        final boolean flag = ( valueAsString == null ) || config.hasAttributeValue( "option", "value", valueAsString );
        validateValue( property, flag, "Value is not a valid option" );
    }
}

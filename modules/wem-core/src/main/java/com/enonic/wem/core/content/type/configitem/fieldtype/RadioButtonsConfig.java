package com.enonic.wem.core.content.type.configitem.fieldtype;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.enonic.wem.core.content.data.Value;

public class RadioButtonsConfig
    implements FieldTypeConfig
{
    private List<Option> options = new ArrayList<Option>();

    private HashMap<String, Option> optionsAsMap = new HashMap<String, Option>();

    public void add( Option option )
    {
        this.options.add( option );
    }

    public List<Option> getOptions()
    {
        return options;
    }

    @Override
    public boolean isValid( final Value value )
    {
        String valueAsString = String.valueOf( value.getValue() );
        return optionsAsMap.containsKey( valueAsString );
    }

    public static class Option
    {

        private String label;

        private String value;

        Option( final String label, final String value )
        {
            this.label = label;
            this.value = value;
        }

        public String getLabel()
        {
            return label;
        }

        public String getValue()
        {
            return value;
        }

    }

    public static Builder newBuilder()
    {
        return new Builder();
    }

    public static class Builder
    {
        private RadioButtonsConfig config = new RadioButtonsConfig();

        public Builder addOption( String label, String value )
        {
            config.add( new Option( label, value ) );
            config.optionsAsMap.put( value, new Option( label, value ) );
            return this;
        }

        public RadioButtonsConfig build()
        {
            return config;
        }
    }
}

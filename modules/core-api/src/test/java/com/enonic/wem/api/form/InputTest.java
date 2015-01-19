package com.enonic.wem.api.form;


import org.junit.Test;

import com.enonic.wem.api.form.inputtype.InputTypes;

import static com.enonic.wem.api.form.FormItemSet.newFormItemSet;
import static com.enonic.wem.api.form.Input.newInput;
import static org.junit.Assert.*;

public class InputTest
{
    @Test
    public void copy()
    {
        // setup
        Input original = newInput().name( "myInput" ).inputType( InputTypes.TEXT_LINE ).build();

        // exercise
        Input copy = original.copy();

        // verify
        assertNotSame( original, copy );
        assertEquals( "myInput", copy.getName() );
        assertSame( original.getName(), copy.getName() );
        assertSame( original.getLabel(), copy.getLabel() );
        assertSame( original.getCustomText(), copy.getCustomText() );
        assertSame( original.getInputType(), copy.getInputType() );
    }

    @Test
    public void toInput_given_FormItem_of_type_Input_then_Input_is_returned()
    {
        // setup
        FormItem formItem = newInput().name( "myInput" ).inputType( InputTypes.TEXT_LINE ).build();

        // exercise
        Input input = formItem.toInput();

        // verify
        assertSame( formItem, input );
    }

    @Test
    public void toInput_given_FormItem_of_type_FormItemSet_then_exception_is_thrown()
    {
        // setup
        FormItem formItem = newFormItemSet().name( "mySet" ).build();

        // exercise
        try
        {
            formItem.toInput();
            fail( "Expected exception" );
        }
        catch ( Exception e )
        {
            assertTrue( e instanceof IllegalArgumentException );
            assertEquals( "This FormItem [mySet] is not an Input: FormItemSet", e.getMessage() );
        }
    }
}
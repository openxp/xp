package com.enonic.wem.api.form.inputtype;


import org.junit.Test;

import com.enonic.wem.api.data.PropertyTree;
import com.enonic.wem.api.form.BreaksRequiredContractException;

public class XmlTest
{
    @Test(expected = BreaksRequiredContractException.class)
    public void checkBreaksRequiredContract_throws_exception_when_value_is_empty_string()
    {
        new Xml().checkBreaksRequiredContract( new PropertyTree().setXml( "myXml", "" ) );
    }

    @Test(expected = BreaksRequiredContractException.class)
    public void checkBreaksRequiredContract_throws_exception_when_value_is_blank_string()
    {
        new Xml().checkBreaksRequiredContract( new PropertyTree().setXml( "myXml", "  " ) );
    }
}
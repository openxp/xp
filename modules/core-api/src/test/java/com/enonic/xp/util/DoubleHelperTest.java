package com.enonic.xp.util;

import org.junit.Test;

import static org.junit.Assert.*;


public class DoubleHelperTest
{

    private static final double ZERO = 0.0;

    private static final double ONE = 1.0;

    @Test
    public void test_compare()
    {
        assertTrue( DoubleHelper.fuzzyCompare( ONE, 1.0 ) );
        assertTrue( DoubleHelper.fuzzyCompare( ONE, 1.00000000000000001 ) );

        assertFalse( DoubleHelper.fuzzyCompare( ONE, 1.0001 ) );
        assertFalse( DoubleHelper.fuzzyCompare( ONE, -1.0 ) );
    }

    @Test
    public void test_compare_zero()
    {
        assertTrue( DoubleHelper.fuzzyCompare( ZERO, 0.0 ) );
        assertTrue( DoubleHelper.fuzzyCompare( ZERO, 0.000000000000000001 ) );

        assertFalse( DoubleHelper.fuzzyCompare( ZERO, 0.0001 ) );
    }

    @Test
    public void test_compare_with_diff_zero()
    {
        assertTrue( DoubleHelper.fuzzyCompare( ZERO, -0.0 ) );
        assertTrue( DoubleHelper.fuzzyCompare( ZERO, -0.0000000000000001 ) );

        assertFalse( DoubleHelper.fuzzyCompare( ZERO, -0.00001 ) );
    }


}

package com.enonic.wem.core.search.elastic.indexsource;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.joda.time.DateTime;
import org.junit.Test;

import com.google.common.collect.Lists;

import static org.junit.Assert.*;

public class IndexSourceOrderbyValueResolverTest
{

    @Test
    public void testNull()
    {
        assertNull( OrderByValueResolver.getOrderbyValue( null ) );
    }

    @Test
    public void testOrderbyValuesForInt()
        throws Exception
    {

        List<String> orderedList = Lists.newArrayList();

        String one = OrderByValueResolver.getOrderbyValue( 1 );
        String two = OrderByValueResolver.getOrderbyValue( 2 );
        String ten = OrderByValueResolver.getOrderbyValue( 10 );
        String hundred = OrderByValueResolver.getOrderbyValue( 100 );
        String two_hundred = OrderByValueResolver.getOrderbyValue( 200 );

        orderedList.add( two_hundred );
        orderedList.add( one );
        orderedList.add( hundred );
        orderedList.add( two );
        orderedList.add( ten );

        final Iterator<String> iterator = orderedList.iterator();

        Collections.sort( orderedList );

        assertEquals( one, iterator.next() );
        assertEquals( two, iterator.next() );
        assertEquals( ten, iterator.next() );
        assertEquals( hundred, iterator.next() );
        assertEquals( two_hundred, iterator.next() );
    }


    @Test
    public void testOrderbyValuesForNumberAsString()
        throws Exception
    {

        List<String> orderedList = Lists.newArrayList();

        String one = OrderByValueResolver.getOrderbyValue( "1" );
        String two = OrderByValueResolver.getOrderbyValue( "2" );
        String ten = OrderByValueResolver.getOrderbyValue( "10" );
        String hundred = OrderByValueResolver.getOrderbyValue( "100" );
        String two_hundred = OrderByValueResolver.getOrderbyValue( "200" );

        orderedList.add( two_hundred );
        orderedList.add( one );
        orderedList.add( hundred );
        orderedList.add( two );
        orderedList.add( ten );

        final Iterator<String> iterator = orderedList.iterator();

        Collections.sort( orderedList );

        assertEquals( one, iterator.next() );
        assertEquals( ten, iterator.next() );
        assertEquals( hundred, iterator.next() );
        assertEquals( two, iterator.next() );
        assertEquals( two_hundred, iterator.next() );
    }


    @Test
    public void testOrderbyValuesForLong()
        throws Exception
    {

        List<String> orderedList = Lists.newArrayList();

        String one = OrderByValueResolver.getOrderbyValue( 1L );
        String two = OrderByValueResolver.getOrderbyValue( 2L );
        String ten = OrderByValueResolver.getOrderbyValue( 10L );
        String hundred = OrderByValueResolver.getOrderbyValue( 100L );
        String two_hundred = OrderByValueResolver.getOrderbyValue( 200L );

        orderedList.add( two_hundred );
        orderedList.add( one );
        orderedList.add( hundred );
        orderedList.add( two );
        orderedList.add( ten );

        final Iterator<String> iterator = orderedList.iterator();

        Collections.sort( orderedList );

        assertEquals( one, iterator.next() );
        assertEquals( two, iterator.next() );
        assertEquals( ten, iterator.next() );
        assertEquals( hundred, iterator.next() );
        assertEquals( two_hundred, iterator.next() );
    }


    @Test
    public void testOrderbyValuesForDouble()
        throws Exception
    {

        List<String> orderedList = Lists.newArrayList();

        String one = OrderByValueResolver.getOrderbyValue( 1.0 );
        String two = OrderByValueResolver.getOrderbyValue( 2.0 );
        String ten = OrderByValueResolver.getOrderbyValue( 10.0 );
        String hundred = OrderByValueResolver.getOrderbyValue( 100.0 );
        String two_hundred = OrderByValueResolver.getOrderbyValue( 200.0 );

        orderedList.add( two_hundred );
        orderedList.add( one );
        orderedList.add( hundred );
        orderedList.add( two );
        orderedList.add( ten );

        final Iterator<String> iterator = orderedList.iterator();

        Collections.sort( orderedList );

        assertEquals( one, iterator.next() );
        assertEquals( two, iterator.next() );
        assertEquals( ten, iterator.next() );
        assertEquals( hundred, iterator.next() );
        assertEquals( two_hundred, iterator.next() );
    }


    @Test
    public void testOrderbyValuesForFloat()
        throws Exception
    {

        List<String> orderedList = Lists.newArrayList();

        String one = OrderByValueResolver.getOrderbyValue( 1f );
        String two = OrderByValueResolver.getOrderbyValue( 2f );
        String ten = OrderByValueResolver.getOrderbyValue( 10f );
        String hundred = OrderByValueResolver.getOrderbyValue( 100f );
        String two_hundred = OrderByValueResolver.getOrderbyValue( 200f );

        orderedList.add( two_hundred );
        orderedList.add( one );
        orderedList.add( hundred );
        orderedList.add( two );
        orderedList.add( ten );

        final Iterator<String> iterator = orderedList.iterator();

        Collections.sort( orderedList );

        assertEquals( one, iterator.next() );
        assertEquals( two, iterator.next() );
        assertEquals( ten, iterator.next() );
        assertEquals( hundred, iterator.next() );
        assertEquals( two_hundred, iterator.next() );
    }


    @Test
    public void testOrderbyValuesForDate()
        throws Exception
    {

        List<String> orderedList = Lists.newArrayList();
        OrderByValueResolver.getOrderbyValue( 1 );

        String first = OrderByValueResolver.getOrderbyValue( new DateTime( 2013, 1, 1, 1, 1 ).toDate() );
        String second = OrderByValueResolver.getOrderbyValue( new DateTime( 2013, 1, 1, 1, 2 ).toDate() );
        String third = OrderByValueResolver.getOrderbyValue( new DateTime( 2013, 1, 1, 1, 10 ).toDate() );
        String fourth = OrderByValueResolver.getOrderbyValue( new DateTime( 2014, 1, 1, 1, 2 ).toDate() );
        String fifth = OrderByValueResolver.getOrderbyValue( new DateTime( 2014, 1, 1, 1, 10 ).toDate() );

        orderedList.add( fifth );
        orderedList.add( first );
        orderedList.add( fourth );
        orderedList.add( second );
        orderedList.add( third );

        final Iterator<String> iterator = orderedList.iterator();

        Collections.sort( orderedList );

        assertEquals( first, iterator.next() );
        assertEquals( second, iterator.next() );
        assertEquals( third, iterator.next() );
        assertEquals( fourth, iterator.next() );
        assertEquals( fifth, iterator.next() );
    }


}

package com.enonic.wem.core.search.elastic.indexsource;

import java.util.Date;
import java.util.Set;

import com.google.common.collect.Sets;

import com.enonic.wem.core.search.indexdocument.IndexDocumentEntry;

final class IndexSourceEntryFactory
{
    public static final String ORDERBY_FIELD_POSTFIX = "orderby";

    public static final String NUMERIC_FIELD_POSTFIX = "numeric";

    public static final String DATE_FIELD_POSTFIX = "date";

    public static final String FIELD_TYPE_SEPARATOR = ".";

    public static final String DEFAULT_EMPTY_STRING_VALUE = "";

    protected static Set<IndexSourceEntry> create( IndexDocumentEntry indexDocumentEntry )
    {
        Set<IndexSourceEntry> indexSourceEntries = Sets.newHashSet();

        if ( indexDocumentEntry.doIncludeOrderBy() )
        {
            appendOrderBy( indexDocumentEntry, indexSourceEntries );
        }

        final Object value = indexDocumentEntry.getValue();

        if ( value instanceof Number )
        {
            appendNumericField( indexDocumentEntry, indexSourceEntries );
        }
        else if ( value instanceof Date )
        {
            appendDateField( indexDocumentEntry, indexSourceEntries );
        }

        appendStringField( indexDocumentEntry, indexSourceEntries );

        return indexSourceEntries;
    }

    private static void appendNumericField( final IndexDocumentEntry indexDocumentEntry, final Set<IndexSourceEntry> indexSourceEntries )
    {
        final String baseFieldName = indexDocumentEntry.getKey();
        final Double doubleValue = ( (Number) indexDocumentEntry.getValue() ).doubleValue();

        indexSourceEntries.add( new IndexSourceEntry( generateNumericFieldName( baseFieldName ), doubleValue ) );
    }

    private static void appendDateField( final IndexDocumentEntry indexDocumentEntry, final Set<IndexSourceEntry> indexSourceEntries )
    {
        final String baseFieldName = indexDocumentEntry.getKey();
        final Date dateValue = ( (Date) indexDocumentEntry.getValue() );

        indexSourceEntries.add( new IndexSourceEntry( generateDateFieldName( baseFieldName ), dateValue ) );
    }

    private static void appendStringField( final IndexDocumentEntry indexDocumentEntry, final Set<IndexSourceEntry> indexSourceEntries )
    {
        String baseFieldName = indexDocumentEntry.getKey();

        final String stringValue = indexDocumentEntry.getValueAsString();

        indexSourceEntries.add( new IndexSourceEntry( generateStringTypeFieldName( baseFieldName ), genereateStringValue( stringValue ) ) );
    }

    private static void appendOrderBy( final IndexDocumentEntry indexDocumentEntry, final Set<IndexSourceEntry> indexSourceEntries )
    {
        final String orderByValue = OrderByValueResolver.getOrderbyValue( indexDocumentEntry.getValue() );
        final String orderByFieldName = generateOrderbyFieldName( indexDocumentEntry.getKey() );

        indexSourceEntries.add( new IndexSourceEntry( orderByFieldName, orderByValue ) );
    }

    private static String generateOrderbyFieldName( final String originalFieldName )
    {
        return originalFieldName + FIELD_TYPE_SEPARATOR + ORDERBY_FIELD_POSTFIX;

    }

    private static String generateNumericFieldName( final String originalFieldName )
    {
        return originalFieldName + FIELD_TYPE_SEPARATOR + NUMERIC_FIELD_POSTFIX;
    }

    private static String generateDateFieldName( final String originalFieldName )
    {
        return originalFieldName + FIELD_TYPE_SEPARATOR + DATE_FIELD_POSTFIX;
    }

    private static String generateStringTypeFieldName( final String originalFieldName )
    {
        return originalFieldName;
    }

    private static String genereateStringValue( final String stringValue )
    {
        return stringValue != null ? stringValue.toLowerCase() : DEFAULT_EMPTY_STRING_VALUE;
    }
}

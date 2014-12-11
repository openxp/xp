package com.enonic.wem.admin.rest.resource.content.json;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Strings;

import com.enonic.wem.api.query.aggregation.AggregationQuery;
import com.enonic.wem.api.query.aggregation.DateHistogramAggregationQuery;
import com.enonic.wem.api.query.aggregation.DateInterval;

public class DateHistogramAggregationQueryJson
    extends AggregationQueryJson
{

    private DateHistogramAggregationQuery dateHistogramAggregationQuery;

    @JsonCreator
    public DateHistogramAggregationQueryJson( @JsonProperty(value = "name", required = true) final String name, //
                                              @JsonProperty(value = "fieldName", required = true) final String fieldName, //
                                              @JsonProperty(value = "interval", required = true) final String interval, //
                                              @JsonProperty("format") final String format, //
                                              @JsonProperty("minDocCount") final Integer minDocCount )
    {
        final DateHistogramAggregationQuery.Builder builder = DateHistogramAggregationQuery.create( name ).
            fieldName( fieldName ).
            interval( DateInterval.from( interval ) );

        if ( !Strings.isNullOrEmpty( format ) )
        {
            builder.format( format );
        }

        if ( minDocCount != null )
        {
            builder.minDocCount( Integer.toUnsignedLong( minDocCount ) );
        }

        this.dateHistogramAggregationQuery = builder.build();
    }

    @Override
    public AggregationQuery getAggregationQuery()
    {
        return dateHistogramAggregationQuery;
    }
}

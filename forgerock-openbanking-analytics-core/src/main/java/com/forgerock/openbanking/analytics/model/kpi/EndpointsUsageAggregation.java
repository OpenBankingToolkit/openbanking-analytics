/**
 * Copyright 2019 ForgeRock AS. All Rights Reserved
 *
 * Use of this code requires a commercial software license with ForgeRock AS.
 * or with one of its affiliates. All use shall be exclusively subject
 * to such license between the licensee and ForgeRock AS.
 */
package com.forgerock.openbanking.analytics.model.kpi;

import com.forgerock.openbanking.analytics.model.entries.EndpointUsageAggregate;
import com.forgerock.openbanking.analytics.rest.annotations.openbanking.OBGroupName;
import com.google.common.collect.ImmutableMap;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.DurationFieldType;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
public enum EndpointsUsageAggregation {
        BY_TPP {
                @Override
                public List getValuesFromEntry(EndpointUsageAggregate entry, DateTimeFormatter dateGranularityFormatter, List definitionsSet) {
                        return Collections.singletonList(entry.getIdentityId());
                }

                @Override
                public List getSetDefinition(GetDistinctField getDistinctField, DateTime from, DateTime to, EndpointsUsageKPI.DateGranularity dateGranularityFormat) {
                        return getDistinctField.op(from, to, "identityId").stream().filter(key -> key != null).collect(Collectors.toList());
                }
        },
        BY_RESPONSE_STATUS {
                @Override
                public List getValuesFromEntry(EndpointUsageAggregate entry, DateTimeFormatter dateGranularityFormatter, List definitionsSet) {
                        return Collections.singletonList(getStatusRange(entry.getResponseStatus()));
                }

                @Override
                public List getSetDefinition(GetDistinctField getDistinctField, DateTime from, DateTime to, EndpointsUsageKPI.DateGranularity dateGranularityFormat) {
                        return getDistinctField.op(from, to, "responseStatus").stream().map(d -> getStatusRange(d)).filter(key -> key != null).distinct().collect(Collectors.toList());
                }
        },
        BY_DATE {
                @Override
                public List formatDefinitionsSet(EndpointsUsageKPI.DateGranularity dateGranularity, List definitions) {
                        List<String> datesSerialised = new ArrayList<>();
                        for (DateTime date : (List<DateTime>) definitions) {
                                datesSerialised.add(formatterByGranularity.get(dateGranularity).print(date));
                        }
                        return datesSerialised;
                }

                @Override
                public List getValuesFromEntry(EndpointUsageAggregate entry, DateTimeFormatter dateGranularityFormatter, List definitionsSet) {
                        return Collections.singletonList(roundDate(entry.getDate(), dateGranularityFormatter));
                }

                @Override
                public List getSetDefinition(GetDistinctField getDistinctField, DateTime from, DateTime to, EndpointsUsageKPI.DateGranularity dateGranularityFormat) {
                        //For the date, we need iterate through the data definition set we build and use the granularity to compute the next date
                        List<DateTime> dateTimes = new ArrayList<>();
                        DateTime currentDate = new DateTime(from);

                        // We compute the next date
                        while (currentDate.isBefore(to.plusMinutes(1))) {
                                dateTimes.add(currentDate);
                                switch (dateGranularityFormat) {
                                        case BY_YEAR:
                                                currentDate = currentDate.withFieldAdded(DurationFieldType.years(), 1);
                                                break;
                                        case BY_MONTH:
                                                currentDate = currentDate.withFieldAdded(DurationFieldType.months(), 1);
                                                break;
                                        case BY_DAY:
                                                currentDate = currentDate.withFieldAdded(DurationFieldType.days(), 1);
                                                break;
                                        case BY_HOUR:
                                                currentDate = currentDate.withFieldAdded(DurationFieldType.hours(), 1);
                                                break;

                                }
                        }
                        return dateTimes;
                }
        },
        BY_WEEK_DAY {
                @Override
                public List formatDefinitionsSet(EndpointsUsageKPI.DateGranularity dateGranularity, List definitions) {
                        List<String> datesSerialised = new ArrayList<>();
                        for (DateTime date : (List<DateTime>) definitions) {
                                if (date.getHourOfDay() == 0 && date.getMinuteOfHour() == 0) {
                                        datesSerialised.add(FORMAT_WEEK_DAY_WITH_HOURS.print(date));
                                } else {
                                        datesSerialised.add(FORMAT_HOURS_ONLY.print(date));
                                }
                        }
                        return datesSerialised;
                }

                @Override
                public List getValuesFromEntry(EndpointUsageAggregate entry, DateTimeFormatter dateGranularityFormatter, List definitionsSet) {
                        return Collections.singletonList(roundDateToDayWeek(entry.getDate()));
                }

                @Override
                public List getSetDefinition(GetDistinctField getDistinctField, DateTime from, DateTime to, EndpointsUsageKPI.DateGranularity dateGranularityFormat) {
                        List<DateTime> dateTimes = new ArrayList<>();
                        DateTime currentDate = BEGINING_WEEK.withZone(DateTimeZone.UTC); //A monday
                        // We compute the next date
                        while (currentDate.isBefore(END_OF_WEEK)) {
                                dateTimes.add(currentDate);
                                currentDate = currentDate.withFieldAdded(DurationFieldType.hours(), 1);
                        }
                        return dateTimes;
                }
        },
        BY_OB_VERSIONS {
                @Override
                public List getValuesFromEntry(EndpointUsageAggregate entry, DateTimeFormatter dateGranularityFormatter, List definitionsSet) {
                        return Collections.singletonList(entry.getObVersion());
                }

                @Override
                public List getSetDefinition(GetDistinctField getDistinctField, DateTime from, DateTime to, EndpointsUsageKPI.DateGranularity dateGranularityFormat) {
                        return getDistinctField.op(from, to, "obVersion").stream().filter(key -> key != null).collect(Collectors.toList());
                }

        },
        BY_OB_TYPE {
                @Override
                public List getValuesFromEntry(EndpointUsageAggregate entry, DateTimeFormatter dateGranularityFormatter, List definitionsSet) {
                        return Arrays.asList(entry.getEndpointType());
                }

                @Override
                public List getSetDefinition(GetDistinctField getDistinctField, DateTime from, DateTime to, EndpointsUsageKPI.DateGranularity dateGranularityFormat) {
                        return Arrays.asList(OBGroupName.values());
                }
        },
        BY_AGGREGATION_METHODS {
                @Override
                public List getValuesFromEntry(EndpointUsageAggregate entry, DateTimeFormatter dateGranularityFormatter, List definitionsSet) {
                        return Arrays.asList(
                                AggregationMethod.BY_NB_CALLS,

                                AggregationMethod.BY_RESPONSE_TIME_PERCENTILE_95,
                                AggregationMethod.BY_RESPONSE_TIME_PERCENTILE_85,
                                AggregationMethod.BY_RESPONSE_TIME_MEDIAN,
                                AggregationMethod.BY_RESPONSE_TIME_AVERAGE
                        );
                }

                @Override
                public AggregationMethod getAggregatorMethod(AggregationMethod aggregationMethodFromRequest, Object lineKey) {
                        return (AggregationMethod) lineKey;
                }

                @Override
                public List getSetDefinition(GetDistinctField getDistinctField, DateTime from, DateTime to, EndpointsUsageKPI.DateGranularity dateGranularityFormat) {
                        return Arrays.asList(
                                AggregationMethod.BY_NB_CALLS,

                                AggregationMethod.BY_RESPONSE_TIME_PERCENTILE_95,
                                AggregationMethod.BY_RESPONSE_TIME_PERCENTILE_85,
                                AggregationMethod.BY_RESPONSE_TIME_MEDIAN,
                                AggregationMethod.BY_RESPONSE_TIME_AVERAGE
                        );
                }
        },
        BY_RESPONSE_TIME {

                @Override
                public List formatDefinitionsSet(EndpointsUsageKPI.DateGranularity dateGranularity, List definitions) {
                        return formatDefinitionForResponseTime(definitions);
                }


                @Override
                public List getValuesFromEntry(EndpointUsageAggregate entry, DateTimeFormatter dateGranularityFormatter, List definitionsSet) {
                        return getValuesFromEntryForMs(entry.getResponseTimesHistory(), definitionsSet);
                }

                @Override
                public List getSetDefinition(GetDistinctField getDistinctField, DateTime from, DateTime to, EndpointsUsageKPI.DateGranularity dateGranularityFormat) {
                        return getSetDefinitionForMS(getDistinctField, from, to, dateGranularityFormat, "responseTimesHistory");
                }
        },
        BY_RESPONSE_TIME_MB {

                @Override
                public List formatDefinitionsSet(EndpointsUsageKPI.DateGranularity dateGranularity, List definitions) {
                        return formatDefinitionForResponseTime(definitions);
                }


                @Override
                public List getValuesFromEntry(EndpointUsageAggregate entry, DateTimeFormatter dateGranularityFormatter, List definitionsSet) {
                       return getValuesFromEntryForMs(entry.getResponseTimesByKbHistory(), definitionsSet);
                }

                @Override
                public List getSetDefinition(GetDistinctField getDistinctField, DateTime from, DateTime to, EndpointsUsageKPI.DateGranularity dateGranularityFormat) {
                        return getSetDefinitionForMS(getDistinctField, from, to, dateGranularityFormat, "responseTimesByMbHistory");
                }
        };

        private static List formatDefinitionForResponseTime(List<Double> definitions) {
                List<String> responseTimesFormatted = new ArrayList<>();
                for (Double responseTime : definitions) {
                        DateTime dateTime = DateTime.now().withMillis(Math.round(responseTime)).withZone(DateTimeZone.UTC);
                        if (dateTime.getHourOfDay() > 0) {
                                responseTimesFormatted.add(dateTime.toString(FORMAT_MS_WITH_HOURS));
                        } else if (dateTime.getMinuteOfHour() > 0) {
                                responseTimesFormatted.add(dateTime.toString(FORMAT_MS_WITH_MINUTES));
                        } else if (dateTime.getSecondOfMinute() > 0) {
                                responseTimesFormatted.add(dateTime.toString(FORMAT_MS_WITH_SECONDS));
                        } else {
                                responseTimesFormatted.add(dateTime.toString(FORMAT_MS_WITH_MS));
                        }
                }
                return responseTimesFormatted;
        }


        public static final double NB_X_ITEMS = 100.0;

        public static Map<EndpointsUsageKPI.DateGranularity, DateTimeFormatter> formatterByGranularity = ImmutableMap.<EndpointsUsageKPI.DateGranularity, DateTimeFormatter>builder()
                .put(EndpointsUsageKPI.DateGranularity.BY_YEAR, DateTimeFormat.forPattern("yyyy"))
                .put(EndpointsUsageKPI.DateGranularity.BY_MONTH, DateTimeFormat.forPattern("yyyy-MM"))
                .put(EndpointsUsageKPI.DateGranularity.BY_DAY, DateTimeFormat.forPattern("yyyy-MM-dd"))
                .put(EndpointsUsageKPI.DateGranularity.BY_HOUR, DateTimeFormat.forPattern("yyyy-MM-dd HH:mm"))
                .build();

        public static DateTimeFormatter FORMAT_HOURS_ONLY = DateTimeFormat.forPattern("HH:mm");
        public static DateTimeFormatter FORMAT_WEEK_DAY_WITH_HOURS = DateTimeFormat.forPattern("E HH:mm");

        public static DateTimeFormatter FORMAT_MS_WITH_HOURS = DateTimeFormat.forPattern("H 'h' m 'm' s 's' SSS 'ms'");
        public static DateTimeFormatter FORMAT_MS_WITH_MINUTES = DateTimeFormat.forPattern("m 'm' s 's' SSS 'ms'");
        public static DateTimeFormatter FORMAT_MS_WITH_SECONDS = DateTimeFormat.forPattern("s 's' SSS 'ms'");
        public static DateTimeFormatter FORMAT_MS_WITH_MS = DateTimeFormat.forPattern("SSS 'ms'");

        public static DateTime BEGINING_WEEK = DateTime.parse("2019-05-13T00:00:00.000Z"); //A monday
        public static DateTime END_OF_WEEK = DateTime.parse("2019-05-20T00:00:00.000Z");

        public List formatDefinitionsSet(EndpointsUsageKPI.DateGranularity dateGranularity, List definitions) {
                return definitions;
        }

        public Boolean skipEntry(EndpointUsageAggregate entry, DateTimeFormatter dateGranularityFormatter, List list) {
                return this.getValuesFromEntry(entry, dateGranularityFormatter, list) == null
                        || this.getValuesFromEntry(entry, dateGranularityFormatter, list).isEmpty();
        }

        public abstract List getValuesFromEntry(EndpointUsageAggregate entry, DateTimeFormatter dateGranularityFormatter, List definitionsSet);

        public abstract List getSetDefinition(GetDistinctField getDistinctField, DateTime from, DateTime to, EndpointsUsageKPI.DateGranularity dateGranularityFormat);

        public AggregationMethod getAggregatorMethod(AggregationMethod aggregationMethodFromRequest, Object lineKey) {
                return aggregationMethodFromRequest;
        }

        public interface GetDistinctField {
                List<String> op( DateTime from, DateTime to, String field);
        }

        /**
         * Dates need to be rounded with the current date granularity.
         * @param dateTime
         * @param dateGranularity
         * @return
         */
        private static DateTime roundDate(DateTime dateTime, DateTimeFormatter dateGranularity) {
                return DateTime.parse(dateGranularity.print(dateTime), dateGranularity).withMinuteOfHour(0);
        }

        /**
         * Round by week.
         * @param date
         * @return
         */
        private static DateTime roundDateToDayWeek(DateTime date) {
                DateTime roundedDate = BEGINING_WEEK.plusDays(date.getDayOfWeek() - 1);
                roundedDate = roundedDate.withHourOfDay(date.withZone(DateTimeZone.UTC).getHourOfDay());
                return roundedDate;
        }


        /**
         * Aggregate status by range type. This makes the size of the status set smaller and easier to interpret from a businesss level
         * @param statusInString
         * @return
         */
        private static String getStatusRange(String statusInString) {

                try {
                        Integer status = Integer.valueOf(statusInString);
                        if (100 <= status && status < 200) {
                                return "1xx";
                        }
                        if (200 <= status && status < 300) {
                                return "2xx";
                        }
                        if (300 <= status && status < 400) {
                                return "3xx";
                        }
                        if (400 <= status && status < 500) {
                                return "4xx";
                        }
                        if (500 <= status && status < 600) {
                                return "5xx";
                        }
                } catch (NumberFormatException e) {
                        log.warn("The status '" + statusInString + "' wasn't a number", e);
                }
                return statusInString;
        }

        public List getSetDefinitionForMS(GetDistinctField getDistinctField, DateTime from, DateTime to, EndpointsUsageKPI.DateGranularity dateGranularityFormat, String field) {
                List<String> responseTimesDistinct = getDistinctField.op(from, to, field);
                if (responseTimesDistinct.size() == 0) {
                        return Collections.emptyList();
                }
                Set<Long> responseTimes = responseTimesDistinct.stream()
                        .filter(Objects::nonNull)
                        .map(s -> Stream.of(s.split(",")).map(s1 -> Long.valueOf(s1)).collect(Collectors.toList()))
                        .flatMap(List::stream)
                        .collect(Collectors.toSet());

                Long maxResponseTime = responseTimes.stream().max((l1, l2) -> l1<l2?-1:l1>l2?1:0).get();

                // We find the best logarithmic scale based on the max value
                double logScale = Math.pow(maxResponseTime, 1.0 / NB_X_ITEMS);
                logScale = Math.ceil(logScale * 1000) / 1000.0;

                List<Double> responseTimesDefinitionSet = new ArrayList<>();

                //We add 0 as logScale ^ 0 is indefined
                responseTimesDefinitionSet.add(0.0);

                for(int i = 1; i <= NB_X_ITEMS; i++) {
                        double value = Math.pow(logScale, i);
                        value = Math.round(value * 1000) / 1000.0;
                        responseTimesDefinitionSet.add(value);
                }
                return responseTimesDefinitionSet;
        }

        public List getValuesFromEntryForMs(List<Long> ms, List definitionsSet) {
                //The log scale would always be in x=1
                double logScale = (double) definitionsSet.get(1);
                List<Double> values =  ms.stream().map(e -> {
                        if (e == 0 || e < logScale) {
                                return 0.0;
                        }
                        //We find the closest position in the abscisse
                        long i = Math.round(Math.log(e) / Math.log(logScale));
                        //Compute what is the actual value of the abcisse at this position
                        double value = Math.pow(logScale, i);
                        value = Math.round(value * 1000) / 1000.0;

                        if(!definitionsSet.contains(value)) {
                               log.warn("Value {} should be contained in definition set {}", value, definitionsSet);
                               return null;
                        }
                        return value;
                }).filter(Objects::nonNull).collect(Collectors.toList());
                return values;
        }
}

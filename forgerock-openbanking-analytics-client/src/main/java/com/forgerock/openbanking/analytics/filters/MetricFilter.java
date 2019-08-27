/**
 * Copyright 2019 ForgeRock AS. All Rights Reserved
 *
 * Use of this code requires a commercial software license with ForgeRock AS.
 * or with one of its affiliates. All use shall be exclusively subject
 * to such license between the licensee and ForgeRock AS.
 */
package com.forgerock.openbanking.analytics.filters;

import com.forgerock.openbanking.analytics.configuration.MetricsConfigurationProperties;
import com.forgerock.openbanking.analytics.model.entries.EndpointUsageEntry;
import com.forgerock.openbanking.analytics.model.entries.GeoIP;
import com.forgerock.openbanking.analytics.model.openbanking.OpenBankingAPI;
import com.forgerock.openbanking.analytics.services.MetricService;
import com.forgerock.openbanking.auth.UserContext;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

import static com.forgerock.openbanking.analytics.utils.MetricUtils.ANALYTICS_ENABLED_HEADER_NAME;

@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class MetricFilter implements Filter {

    private final MetricService metricService;
    private final String applicationName;
    private final MetricsConfigurationProperties metricsConfigurationProperties;
    private final RequestMappingHandlerMapping mappings;

    private static final String GEO_IP_HEADER = "x-forwarded-for";

    public MetricFilter(MetricService metricService, @Value("${spring.application.name}") String applicationName,
                        MetricsConfigurationProperties metricsConfigurationProperties, RequestMappingHandlerMapping mappings) {
        this.metricService = metricService;
        this.applicationName = applicationName;
        this.metricsConfigurationProperties = metricsConfigurationProperties;
        this.mappings = mappings;
    }

    @Override
    public void init(FilterConfig filterConfig) {
        log.debug("Init metric filter");
    }


    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws java.io.IOException, ServletException {
        DateTime receivedRequestTime = DateTime.now();

        HttpServletResponse httpres = (HttpServletResponse) response;
        MetricResponseWrapper counter = new MetricResponseWrapper(httpres);
        HttpServletRequest httpreq = (HttpServletRequest) request;
        httpreq.setAttribute("counter", counter);

        try {
            chain.doFilter(request, counter);
        } finally {
            if (CollectionUtils.isEmpty(metricsConfigurationProperties.getEndpoints())) {
                log.trace("No configured endpoints for metrics in {} so no endpoint metrics will be captured", applicationName);
                return;
            }
            if (counter.getHeaderNames().contains(ANALYTICS_ENABLED_HEADER_NAME)
                    && !counter.getHeader(ANALYTICS_ENABLED_HEADER_NAME).equals("true")) {
                log.trace("This trace id is marked as excluded");
                return;
            }
            long counterValue;
            try {
                counterValue = counter.getByteCount();
            } catch (Exception e) {
                counterValue= 0l;
            }
            final long bodySize = counterValue;
            DateTime sendResponseTime = DateTime.now();



            final HttpServletRequest httpRequest = ((HttpServletRequest) request);
            final String uriFromRequest = httpRequest.getRequestURI();
            metricsConfigurationProperties.getEndpoints()
                    .stream()
                    .filter(whitelistEP -> whitelistEP.getMatcher(uriFromRequest).matches())
                    .peek(whitelistEP -> log.trace("Matched URI: {} to whitelist entry: {}", uriFromRequest, whitelistEP))
                    .findFirst()  // Finds first matching URL in whitelist
                    // If found, map the matching URL into a metric usage record
                    .map(formattedEndpoint -> buildMetricsEntry(httpRequest, counter, bodySize,  receivedRequestTime, sendResponseTime)) // Turns the URI and response code into an EndpointUsageEntry.class
                    .ifPresent(metricService::addEndpointUsageEntry); // If match was found then send to metrics microservice
        }
    }

    private EndpointUsageEntry buildMetricsEntry(HttpServletRequest request, MetricResponseWrapper response, long count, DateTime receivedRequestTime, DateTime sendResponseTime) {

        Double contentSizeInKB = count / 1024.0;

        Long responseTime = sendResponseTime.getMillis() - receivedRequestTime.getMillis();

        String templatePath = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);

        UserContext.UserType userType = UserContext.UserType.ANONYMOUS;
        if (response.getHeaderNames().contains("UserType")) {
            userType = UserContext.UserType.valueOf(response.getHeader("UserType"));
            response.setHeader("UserType", "");
        }

        String userId = "";
        if (response.getHeaderNames().contains("UserId")) {
            userId = response.getHeader("UserId");
            response.setHeader("UserId", "");
        }

        log.debug("Request header '{}' received from the gateway and potentially containing the user IP : {}",
                GEO_IP_HEADER, request.getHeader(GEO_IP_HEADER));


        EndpointUsageEntry.EndpointUsageEntryBuilder endpointUsageEntryBuilder = EndpointUsageEntry.builder()
                .date(receivedRequestTime.withSecondOfMinute(0).withMillisOfSecond(0))
                .responseTime(responseTime)
                .responseTimeByKb(contentSizeInKB != null && contentSizeInKB != 0.0 ? Math.round(responseTime / contentSizeInKB): null)
                .endpoint(templatePath)
                .method(request.getMethod())
                .application(applicationName)
                .identityId(userId)
                .geoIP(GeoIP.builder().ipAddress(request.getHeader(GEO_IP_HEADER)).build())
                .userType(userType)
                .responseStatus(String.valueOf(response.getStatus()));
        getHandlerOBAPI(request).ifPresent(obApi -> {
            endpointUsageEntryBuilder.endpointType(obApi.obGroupName());
            endpointUsageEntryBuilder.obVersion(obApi.obVersion());
        });
        getMethodOBAPI(request).ifPresent(obApi -> {
            endpointUsageEntryBuilder.obReference(obApi.obReference());
        });

        final EndpointUsageEntry endpointUsageEntry = endpointUsageEntryBuilder.build();
        log.trace("Created EndpointUsageEntry : {}", endpointUsageEntry);
        return endpointUsageEntry;
    }


    private Optional<OpenBankingAPI> getHandlerOBAPI(HttpServletRequest request) {
        try {
            HandlerExecutionChain handlerChain = mappings.getHandler(request);
            if (handlerChain == null) {
                return Optional.empty();
            }
            HandlerMethod handler = (HandlerMethod) handlerChain.getHandler();
            OpenBankingAPI annotation = AnnotationUtils.findAnnotation(handler.getBeanType(), OpenBankingAPI.class);
            if (annotation == null) {
                return Optional.empty();
            }

            return Optional.of(annotation);
        } catch (Exception e) {
            String templatePath = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
            log.debug("Could not get endpoint handler for path={}", templatePath);
            return Optional.empty();
        }
    }

    private Optional<OpenBankingAPI> getMethodOBAPI(HttpServletRequest request) {
        try {
            HandlerExecutionChain handlerChain = mappings.getHandler(request);
            if (handlerChain == null) {
                return Optional.empty();
            }
            HandlerMethod handler = (HandlerMethod) handlerChain.getHandler();
            OpenBankingAPI annotation = AnnotationUtils.findAnnotation(handler.getMethod(), OpenBankingAPI.class);
            if (annotation == null) {
                return Optional.empty();
            }
            return Optional.of(annotation);
        } catch (Exception e) {
            String templatePath = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
            log.debug("Could not get endpoint handler for path={}", templatePath);
            return Optional.empty();
        }
    }
}
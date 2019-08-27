/**
 * Copyright 2019 ForgeRock AS. All Rights Reserved
 *
 * Use of this code requires a commercial software license with ForgeRock AS.
 * or with one of its affiliates. All use shall be exclusively subject
 * to such license between the licensee and ForgeRock AS.
 */
package com.forgerock.openbanking.analytics.filters;

import brave.Tracer;
import com.forgerock.openbanking.analytics.auth.UserContext;
import com.forgerock.openbanking.analytics.utils.MetricUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletResponse;

import static com.forgerock.openbanking.analytics.utils.MetricUtils.ANALYTICS_ENABLED_HEADER_NAME;

@Slf4j
@Component
@Order(Ordered.LOWEST_PRECEDENCE)
public class MetricFilterPopulate implements Filter {
    @Autowired
    private Tracer tracer;

    @Override
    public void init(FilterConfig filterConfig) {
        log.debug("Init metric filter");
    }


    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws java.io.IOException, ServletException {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserContext.UserType userType = authentication.getPrincipal() instanceof UserContext ? ((UserContext) authentication.getPrincipal()).getUserType() : UserContext.UserType.ANONYMOUS;
        HttpServletResponse httpres = (HttpServletResponse) response;

        Boolean isAnalyticsEnabled = MetricUtils.isRequestEnabledForAnalytics(tracer);

        httpres.setHeader("UserType", userType.name());
        httpres.setHeader("UserId", authentication.getName());
        httpres.setHeader(ANALYTICS_ENABLED_HEADER_NAME, isAnalyticsEnabled.toString());

        chain.doFilter(request, response);
    }
}
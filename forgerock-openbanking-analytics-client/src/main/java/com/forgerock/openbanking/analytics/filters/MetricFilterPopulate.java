/**
 * Copyright 2019 ForgeRock AS.
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.forgerock.openbanking.analytics.filters;

import brave.Tracer;
import com.forgerock.openbanking.analytics.utils.MetricUtils;
import com.forgerock.openbanking.model.UserContext;
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
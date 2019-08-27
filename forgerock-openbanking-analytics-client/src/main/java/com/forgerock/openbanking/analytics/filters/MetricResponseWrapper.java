/**
 * Copyright 2019 ForgeRock AS. All Rights Reserved
 *
 * Use of this code requires a commercial software license with ForgeRock AS.
 * or with one of its affiliates. All use shall be exclusively subject
 * to such license between the licensee and ForgeRock AS.
 */
package com.forgerock.openbanking.analytics.filters;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.IOException;
import java.io.PrintWriter;

public class MetricResponseWrapper extends HttpServletResponseWrapper {

    private final CountingServletOutputStream output;
    private final PrintWriter writer;

    public MetricResponseWrapper(HttpServletResponse response) throws IOException {
        super(response);
        output = new CountingServletOutputStream(response.getOutputStream());
        writer = new PrintWriter(output, true);
    }

    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        return output;
    }

    @Override
    public PrintWriter getWriter() throws IOException {
        return writer;
    }

    @Override
    public void flushBuffer() throws IOException {
        writer.flush();
    }

    public long getByteCount() throws IOException {
        flushBuffer(); // Ensure that all bytes are written at this point.
        return output.getByteCount();
    }

}
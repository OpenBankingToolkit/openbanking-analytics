/**
 * Copyright 2019 ForgeRock AS. All Rights Reserved
 *
 * Use of this code requires a commercial software license with ForgeRock AS.
 * or with one of its affiliates. All use shall be exclusively subject
 * to such license between the licensee and ForgeRock AS.
 */
package com.forgerock.openbanking.analytics.filters;

import com.google.common.io.CountingOutputStream;

import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import java.io.IOException;

public class CountingServletOutputStream extends ServletOutputStream {

    private final CountingOutputStream output;

    public CountingServletOutputStream(ServletOutputStream output) {
        this.output = new CountingOutputStream(output);
    }

    @Override
    public void write(int b) throws IOException {
        output.write(b);
    }

    @Override
    public void flush() throws IOException {
        output.flush();
    }

    public long getByteCount() {
        return output.getCount();
    }

    @Override
    public boolean isReady() {
        return false;
    }

    @Override
    public void setWriteListener(WriteListener writeListener) {

    }
}
/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.mulesoft.muleesb.cloudhub;

import com.mulesoft.cloudhub.client.Connection;
import com.mulesoft.cloudhub.client.DomainConnection;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Parameter;

public abstract class AbstractCloudHubMojo extends AbstractMojo
{

    /**
     * CloudHub URL, defaults to https://cloudhub.io.
     *
     * @since 1.0
     */
    @Parameter(property = "cloudhub.url", defaultValue = "https://cloudhub.io/")
    protected String url;

    /**
     * CloudHub domain.
     * User property: cloudhub.domain
     *
     * @since 1.0
     */
    @Parameter(property = "cloudhub.domain", required = true)
    protected String domain;

    /**
     * CloudHub username.
     * User property: cloudhub.username
     *
     * @since 1.0
     */
    @Parameter(property = "cloudhub.username", required = true)
    protected String username;

    /**
     * CloudHub password.
     * User property: cloudhub.password
     *
     * @since 1.0
     */
    @Parameter(property = "cloudhub.password", required = true)
    protected String password;

    /**
     * Plugin timeout. Maven will wait at most this number of millis before cancelling the operation.
     * User property: cloudhub.timeout
     *
     * @since 1.0
     */
    @Parameter(property = "cloudhub.timeout", defaultValue = "500000")
    protected long timeout;

    protected CloudHubAdapter cloudhub;

    @Override
    public void execute() throws MojoExecutionException
    {
        Connection connection = new Connection(url, username, password);
        DomainConnection domainService = connection.on(domain);
        cloudhub = new CloudHubAdapter(connection, domainService);
        doExecute();
    }

    protected abstract void doExecute() throws MojoExecutionException;

}

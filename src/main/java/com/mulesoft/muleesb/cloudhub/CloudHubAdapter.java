/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.mulesoft.muleesb.cloudhub;

import com.mulesoft.cloudhub.client.Application;
import com.mulesoft.cloudhub.client.CloudHubException;
import com.mulesoft.cloudhub.client.Connection;
import com.mulesoft.cloudhub.client.DomainConnection;

import java.io.File;
import java.util.Map;
import java.util.logging.Logger;

import org.apache.maven.plugin.MojoExecutionException;

public class CloudHubAdapter
{

    private static Logger logger = Logger.getLogger(CloudHubAdapter.class.getName());
    private final Connection connection;
    private final DomainConnection domain;

    public CloudHubAdapter(Connection connection, DomainConnection domain)
    {
        this.connection = connection;
        this.domain = domain;
    }

    public void deploy(File application, String muleVersion, int workers, long timeout, Map<String, String> properties) throws MojoExecutionException
    {
        try
        {
            Application applicationMetadata = getApplication(muleVersion, workers, properties);
            domain.available();
            connection.createApplication(applicationMetadata);
            logger.info("Deploying application: " + application);
            logger.info("to domain " + domain.getDomain() + " with Mule Runtime " + muleVersion + " using " + workers + " workers");
            logger.info("using properties: " + properties);
            domain.deploy(application, muleVersion, workers, timeout, properties);
        }
        catch (CloudHubException e)
        {
            throw new MojoExecutionException("Failed to deploy application: " + application, e);
        }

    }

    public void undeploy() throws MojoExecutionException
    {
        try
        {
            domain.stop();
            domain.delete();
        }
        catch (CloudHubException e)
        {
            throw new MojoExecutionException("Failed to undeploy domain: " + domain.getDomain(), e);
        }

    }

    private Application getApplication(String muleVersion, int workers, Map<String, String> properties)
    {
        Application app = new Application();
        app.setDomain(domain.getDomain());
        app.setWorkers(workers);
        app.setProperties(properties);
        app.setMuleVersion(muleVersion);
        return app;
    }

}

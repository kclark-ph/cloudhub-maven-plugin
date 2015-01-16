/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.mulesoft.muleesb.cloudhub;

import static com.jayway.awaitility.Awaitility.await;
import static com.jayway.awaitility.Awaitility.setDefaultPollInterval;
import static com.jayway.awaitility.Awaitility.setDefaultTimeout;
import static com.mulesoft.cloudhub.client.Application.Status.STARTED;
import static java.lang.System.getProperty;
import static java.util.concurrent.TimeUnit.MINUTES;
import static java.util.concurrent.TimeUnit.SECONDS;

import com.mulesoft.cloudhub.client.Application;
import com.mulesoft.cloudhub.client.Connection;
import com.mulesoft.cloudhub.client.DomainConnection;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Callable;

import org.apache.maven.plugin.MojoExecutionException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class CloudHubAdapterITCase
{
    private static final String URL = "https://cloudhub.io";
    private static final String DOMAIN = "domain" + new Random().nextInt();
    private static final File APPLICATION = new File("src/it/deploy/app1.zip");
    private static final long ASYNC_DEPLOYMENT = 0L;
    public static final int WORKERS = 1;
    private static Map<String, String> PROPERTIES = new HashMap();
    private static final String MULE_VERSION = "3.5.0";
    private static String username = getProperty("cloudhub.username");
    private static String password = getProperty("cloudhub.password");
    private Connection connection;
    private DomainConnection domainService;
    private CloudHubAdapter cloudHubAdapter;

    static
    {
        setDefaultPollInterval(5, SECONDS);
        setDefaultTimeout(5, MINUTES);
    }

    @Before
    public void setUp()
    {
        connection = new Connection(URL, username, password);
        domainService = connection.on(DOMAIN);
        cloudHubAdapter = new CloudHubAdapter(connection, domainService);
    }

    @After
    public void undeploy()
    {
        try
        {
            domainService.stop();
            domainService.delete();
        }
        catch (Exception e)
        {
            // Domain fails to stop when not deployed, there is now way to know it.
        }
    }

    @Test
    public void successfulDeployment() throws MojoExecutionException
    {
        cloudHubAdapter.deploy(APPLICATION, MULE_VERSION, WORKERS, ASYNC_DEPLOYMENT, PROPERTIES);
        await().until(statusIs(STARTED));
    }

    @Test(expected = MojoExecutionException.class)
    public void failToDeployToExistingOwnDomain() throws MojoExecutionException
    {
        cloudHubAdapter.deploy(APPLICATION, MULE_VERSION, WORKERS, ASYNC_DEPLOYMENT, PROPERTIES);
        await().until(statusIs(STARTED));
        cloudHubAdapter.deploy(APPLICATION, MULE_VERSION, WORKERS, ASYNC_DEPLOYMENT, PROPERTIES);
    }

    @Test(expected = MojoExecutionException.class)
    public void failToDeployToExistingDomain() throws MojoExecutionException
    {
        cloudHubAdapter = new CloudHubAdapter(connection, connection.on("test"));
        cloudHubAdapter.deploy(APPLICATION, MULE_VERSION, WORKERS, ASYNC_DEPLOYMENT, PROPERTIES);
    }

    private Callable<Boolean> statusIs(final Application.Status status)
    {
        return new Callable<Boolean>()
        {
            @Override
            public Boolean call() throws Exception
            {
                return status.equals(domainService.get().getStatus());
            }
        };
    }


}

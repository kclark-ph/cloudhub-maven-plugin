/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.mulesoft.muleesb.cloudhub;

import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.verify;

import com.mulesoft.cloudhub.client.Connection;
import com.mulesoft.cloudhub.client.DomainConnection;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.apache.maven.plugin.MojoExecutionException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest(DomainConnection.class)
public class CloudHubAdapterTestCase
{

    private static final File APPLICATION = new File("app/path.zip");
    private static final String MULE_VERSION = "3.5.0";
    private static final int WORKERS = 1;
    private static final long TIMEOUT = 12000L;
    private static final String DOMAIN = "test-domain";
    private static Map<String, String> PROPERTIES = new HashMap();
    private CloudHubAdapter cloudHubAdapter;
    private Connection connection;
    private DomainConnection domain;

    @Before
    public void initializeMocks()
    {
        domain = PowerMockito.mock(DomainConnection.class);
        connection = PowerMockito.mock(Connection.class);
        PowerMockito.when(domain.getDomain()).thenReturn(DOMAIN);
        cloudHubAdapter = new CloudHubAdapter(connection, domain);
    }

    @Test
    public void undeploy() throws MojoExecutionException
    {
        cloudHubAdapter.undeploy();
        verify(domain).stop();
        verify(domain).delete();
    }

    @Test
    public void deploy() throws MojoExecutionException
    {
        cloudHubAdapter.deploy(APPLICATION, MULE_VERSION, WORKERS, TIMEOUT, PROPERTIES);
        verify(domain).deploy(eq(APPLICATION), eq(MULE_VERSION), eq(WORKERS), eq(TIMEOUT), eq(PROPERTIES));
    }

}

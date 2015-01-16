/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.mulesoft.muleesb.cloudhub;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;

@Mojo(name = "undeploy", requiresProject = false, defaultPhase = LifecyclePhase.POST_INTEGRATION_TEST)
public class CloudHubUndeployMojo extends AbstractCloudHubMojo
{

    @Override
    public void doExecute() throws MojoExecutionException
    {
        getLog().info("Uneploying domain: " + domain);
        try
        {
            cloudhub.undeploy();
        }
        catch (MojoExecutionException e)
        {
            getLog().error("Failed to undeploy application: " + e.getMessage());
            throw e;
        }
    }

}

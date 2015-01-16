/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package com.mulesoft.muleesb.cloudhub;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

@Mojo(name = "deploy", requiresProject = false, defaultPhase = LifecyclePhase.PRE_INTEGRATION_TEST)
public class CloudHubDeployMojo extends AbstractCloudHubMojo
{

    /**
     * Number of CloudHub workers, your account should have enough privileges.
     *
     * @since 1.0
     */
    @Parameter(property = "cloudhub.workers", defaultValue = "1")
    protected int workers;

    /**
     * Mule Runtime Version.
     *
     * @since 1.0
     */
    @Parameter(property = "cloudhub.muleVersion")
    protected String muleVersion;

    /**
     * Properties for CloudHub application.
     *
     * @since 1.0
     */
    @Parameter
    protected Map<String, String> properties;

    @Parameter(property = "project", required = true, readonly = true)
    protected MavenProject project;

    /**
     * Path of the application to be deployed, if not present uses project' default artifact.
     *
     * @since 1.0
     */
    @Parameter(property = "application")
    protected File application;

    @Override
    public void doExecute() throws MojoExecutionException
    {
        if (properties == null)
        {
            properties = new HashMap();
        }
        File file = getApplication();
        getLog().info("Deploying application: " + file);
        getLog().info("to domain " + domain + " with Mule Runtime " + muleVersion + " using " + workers + " workers");
        getLog().info("using properties: " + properties);
        try
        {
            cloudhub.deploy(file, muleVersion, workers, timeout, properties);
        }
        catch (MojoExecutionException e)
        {
            rollbackDeployment();
            getLog().error("Failed to deploy application: " + e.getMessage());
            throw e;
        }
    }

    private void rollbackDeployment()
    {
        try{
            cloudhub.undeploy();
        }
        catch (Exception e)
        {
            getLog().error("Couldn't rollback failed deployment.");
        }
    }

    protected File getApplication()
    {
        if (application == null)
        {
            String type = project.getArtifact().getType();
            if (!"mule".equals(type))
            {
                throw new IllegalArgumentException("Project packaging should be mule but is: " + type);
            }
            if (project.getAttachedArtifacts().isEmpty())
            {
                throw new IllegalArgumentException("No project artifact was found.");
            }
            return ((Artifact) this.project.getAttachedArtifacts().get(0)).getFile();
        }
        else
        {
            return application;
        }
    }

}

/**
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Eclipse Public License version 1.0, available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.jboss.forge.addon.angularjs;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.jboss.forge.addon.projects.Project;
import org.jboss.forge.addon.projects.facets.WebResourcesFacet;
import org.jboss.forge.addon.resource.Resource;
import org.jboss.forge.addon.scaffold.util.ScaffoldUtil;
import org.jboss.forge.furnace.util.Assert;

/**
 * An observer for the {@link CopyWebResourcesEvent} event. This observer copies scaffolding resources specified in the event to
 * the web root directory of the project.
 */
public class CopyWebResourcesCommand {

    @Inject
    private ResourceRegistry resourceRegistry;

    /**
     * Copies the {@link ScaffoldResource}s specified in the event to various locations under the Web root directory of the
     * project. The copied resources are then made available in the {@link ResourceRegistry}. Every invocation of this method
     * clears the contents of the {@link ResourceRegistry}.
     * 
     * @param event The event containing the {@link ScaffoldResource}s to be copied.
     */
    public void execute(@Observes CopyWebResourcesEvent event) {
        resourceRegistry.clear();
        List<Resource<?>> resources = new ArrayList<>();
        Project project = event.getProject();
        for (ScaffoldResource template : event.getResources()) {
            WebResourcesFacet web = project.getFacet(WebResourcesFacet.class);
            Assert.notNull(web, "WebResourcesFacet was not found.");
            Assert.notNull(getClass().getResourceAsStream(template.getSource()), "Source " + template.getSource() + " was not found.");
            Assert.notNull(template.getDestination(), "Destination " + template.getDestination() + " was not found.");
            Assert.notNull(web.getWebResource(template.getDestination()), "Could not obtain resource.");
            resources.add(ScaffoldUtil.createOrOverwrite(web.getWebResource(template.getDestination()), getClass()
                    .getResourceAsStream(template.getSource()), event.isOverwrite()));
        }
        resourceRegistry.addAll(resources);
        return;
    }

}

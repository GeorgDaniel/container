package org.opentosca.planbuilder.integration.layer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.xml.namespace.QName;

import org.eclipse.winery.model.tosca.TDefinitions;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TRelationshipTemplate;
import org.eclipse.winery.model.tosca.TServiceTemplate;

import com.google.common.collect.Lists;
import org.opentosca.container.core.common.Settings;
import org.opentosca.container.core.model.csar.Csar;
import org.opentosca.planbuilder.core.AbstractSimplePlanBuilder;
import org.opentosca.planbuilder.core.bpel.typebasedplanbuilder.BPELBackupManagementProcessBuilder;
import org.opentosca.planbuilder.core.bpel.typebasedplanbuilder.BPELBuildProcessBuilder;
import org.opentosca.planbuilder.core.bpel.typebasedplanbuilder.BPELDefrostProcessBuilder;
import org.opentosca.planbuilder.core.bpel.typebasedplanbuilder.BPELFreezeProcessBuilder;
import org.opentosca.planbuilder.core.bpel.typebasedplanbuilder.BPELScaleOutProcessBuilder;
import org.opentosca.planbuilder.core.bpel.typebasedplanbuilder.BPELSituationAwareBuildProcessBuilder;
import org.opentosca.planbuilder.core.bpel.typebasedplanbuilder.BPELTerminationProcessBuilder;
import org.opentosca.planbuilder.core.bpel.typebasedplanbuilder.BPELTestManagementProcessBuilder;
import org.opentosca.planbuilder.core.bpel.typebasedplanbuilder.BPELTransformationProcessBuilder;
import org.opentosca.planbuilder.core.bpel.typebasedplanbuilder.BPELUpdateProcessBuilder;
import org.opentosca.planbuilder.core.bpmn.typebasedplanbuilder.BPMNBuildProcessBuilder;
import org.opentosca.planbuilder.core.plugins.registry.PluginRegistry;
import org.opentosca.planbuilder.model.plan.AbstractPlan;
import org.opentosca.container.core.model.ModelUtils;

/**
 * <p>
 * This abstract class is used to define importers
 * </p>
 * Copyright 2013 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author Kalman Kepes - kepeskn@studi.informatik.uni-stuttgart.de
 * @author Jan Ruthardt - st107755@stud.uni-stuttgart.de
 */
public abstract class AbstractImporter {

    private final PluginRegistry pluginRegistry;

    protected AbstractImporter(PluginRegistry pluginRegistry) {
        this.pluginRegistry = pluginRegistry;
    }

    protected AbstractPlan buildAdaptationPlan(final Csar csar, final TDefinitions definitions,
                                               final QName serviceTemplateId,
                                               final Collection<TNodeTemplate> sourceNodeTemplates,
                                               final Collection<TRelationshipTemplate> sourceRelationshipTemplates,
                                               final Collection<TNodeTemplate> targetNodeTemplates,
                                               final Collection<TRelationshipTemplate> targetRelationshipTemplates) {
        final BPELTransformationProcessBuilder transformPlanBuilder = new BPELTransformationProcessBuilder(pluginRegistry);

        return transformPlanBuilder.buildPlan(csar, definitions, serviceTemplateId, sourceNodeTemplates,
            sourceRelationshipTemplates, targetNodeTemplates,
            targetRelationshipTemplates);
    }

    protected List<AbstractPlan> buildTransformationPlans(final Csar sourceCsarName,
                                                          final TDefinitions sourceDefinitions,
                                                          final Csar targetCsarName,
                                                          final TDefinitions targetDefinitions) {
        final List<AbstractPlan> plans = new ArrayList<>();

        final BPELTransformationProcessBuilder transformPlanBuilder = new BPELTransformationProcessBuilder(pluginRegistry);
        TServiceTemplate sourceServiceTemplate = Lists.newArrayList(sourceDefinitions.getServiceTemplates()).get(0);
        TServiceTemplate targetServiceTemplate = Lists.newArrayList(targetDefinitions.getServiceTemplates()).get(0);
        QName sourceQName = new QName(sourceServiceTemplate.getTargetNamespace(), sourceServiceTemplate.getId());
        QName targetQName = new QName(targetServiceTemplate.getTargetNamespace(), targetServiceTemplate.getId());

        plans.add(transformPlanBuilder.buildPlan(sourceCsarName, sourceDefinitions,
            sourceQName,
            targetCsarName, targetDefinitions,
            targetQName));

        return plans;
    }

    /**
     * Generates Plans for ServiceTemplates inside the given Definitions document
     *
     * @param defs an TDefinitions
     * @param csar the CSAR the given Definitions is contained in
     * @return a List of Plans
     */
    public List<AbstractPlan> buildPlans(final TDefinitions defs, final Csar csar) {

        final List<AbstractPlan> plans = new ArrayList<>();

        boolean foundTopo = false;
        for (TServiceTemplate servTemp : defs.getServiceTemplates()) {
            if (servTemp.getTopologyTemplate() != null) {
                foundTopo = true;
            }
        }

        if (!foundTopo) {
            return plans;
        }

        AbstractSimplePlanBuilder buildPlanBuilder = new BPELBuildProcessBuilder(pluginRegistry);
        AbstractSimplePlanBuilder bpmnBuildPlanBuilder = new BPMNBuildProcessBuilder(pluginRegistry);
        final BPELSituationAwareBuildProcessBuilder sitAwareBuilder = new BPELSituationAwareBuildProcessBuilder(pluginRegistry);

        if (!sitAwareBuilder.buildPlans(csar, defs).isEmpty()) {
            buildPlanBuilder = sitAwareBuilder;
        }

        // FIXME: This does not work for me (Michael W. - 2018-02-19)
        // if (!this.hasPolicies(defs)) {
        // buildPlanBuilder = new BPELBuildProcessBuildeplanr();
        // Because policies must be enforced when they are set on the the topology, if
        // the planbuilder doesn't understand them it doesn't generate a plan -> doesn't
        // work for you
        //
        // if (!this.hasPolicies(defs)) {
        // buildPlanBuilder = new BPELBuildProcessBuilder();
        // } else {
        // buildPlanBuilder = new BPELPolicyAwareBuildProcessBuilder();
        // }

        final AbstractSimplePlanBuilder terminationPlanBuilder = new BPELTerminationProcessBuilder(pluginRegistry);
        final AbstractSimplePlanBuilder scalingPlanBuilder = new BPELScaleOutProcessBuilder(pluginRegistry);

        final AbstractSimplePlanBuilder freezePlanBuilder = new BPELFreezeProcessBuilder(pluginRegistry);
        final AbstractSimplePlanBuilder defreezePlanBuilder = new BPELDefrostProcessBuilder(pluginRegistry);

        final AbstractSimplePlanBuilder backupPlanBuilder = new BPELBackupManagementProcessBuilder(pluginRegistry);
        final AbstractSimplePlanBuilder testPlanBuilder = new BPELTestManagementProcessBuilder(pluginRegistry);

        final AbstractSimplePlanBuilder updatePlanBuilder = new BPELUpdateProcessBuilder(pluginRegistry);

        TServiceTemplate servTemplate = defs.getServiceTemplates().iterator().next();

        if (ModelUtils.doesNotHaveBuildPlan(servTemplate) | !ModelUtils.hasTerminationPlan(servTemplate)) {
            if (Settings.OPENTOSCA_PLANBUILDER_LANGUAGE.equals("BPEL")) {
                plans.addAll(scalingPlanBuilder.buildPlans(csar, defs));
                plans.addAll(buildPlanBuilder.buildPlans(csar, defs));
                plans.addAll(terminationPlanBuilder.buildPlans(csar, defs));
                plans.addAll(freezePlanBuilder.buildPlans(csar, defs));
                plans.addAll(defreezePlanBuilder.buildPlans(csar, defs));
                plans.addAll(backupPlanBuilder.buildPlans(csar, defs));
                plans.addAll(testPlanBuilder.buildPlans(csar, defs));
                plans.addAll(updatePlanBuilder.buildPlans(csar, defs));
            } else if (Settings.OPENTOSCA_PLANBUILDER_LANGUAGE.equals("BPMN")) {
                plans.addAll(bpmnBuildPlanBuilder.buildPlans(csar, defs));
            }

        }

        return plans;
    }
}

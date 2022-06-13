package org.opentosca.planbuilder.type.plugin.dockercontainer.bpmn;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.winery.model.tosca.TArtifactReference;
import org.eclipse.winery.model.tosca.TArtifactTemplate;
import org.eclipse.winery.model.tosca.TDeploymentArtifact;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TNodeTypeImplementation;

import org.opentosca.container.core.convention.Interfaces;
import org.opentosca.container.core.model.csar.Csar;
import org.opentosca.planbuilder.core.bpmn.context.BPMNPlanContext;
import org.opentosca.planbuilder.core.bpmn.fragments.BPMNProcessFragments;
import org.opentosca.planbuilder.core.bpmn.handlers.BPMNSubprocessHandler;
import org.opentosca.planbuilder.core.plugins.context.PlanContext;
import org.opentosca.planbuilder.core.plugins.context.PropertyVariable;
import org.opentosca.planbuilder.core.plugins.context.Variable;
import org.opentosca.planbuilder.core.plugins.utils.PluginUtils;
import org.opentosca.container.core.model.ModelUtils;
import org.opentosca.planbuilder.provphase.plugin.invoker.bpmn.BPMNInvokerPlugin;
import org.opentosca.planbuilder.type.plugin.dockercontainer.core.DockerContainerTypePlugin;
import org.opentosca.planbuilder.type.plugin.dockercontainer.core.DockerContainerTypePluginPluginConstants;
import org.opentosca.planbuilder.type.plugin.dockercontainer.core.handler.DockerContainerTypePluginHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;

import static org.opentosca.planbuilder.type.plugin.dockercontainer.core.DockerContainerTypePlugin.getTDeploymentArtifact;

/**
 * <p>
 * This class contains all the logic to add BPMN Code which installs a PhpModule on an Apache HTTP Server
 * </p>
 * Copyright 2014 IAAS University of Stuttgart <br>
 * <br>
 *
 * @author Kalman Kepes - kepeskn@studi.informatik.uni-stuttgart.de
 */
public class BPMNDockerContainerTypePluginHandler implements DockerContainerTypePluginHandler<BPMNPlanContext> {
    private static final Logger LOG = LoggerFactory.getLogger(BPMNDockerContainerTypePluginHandler.class);

    private final BPMNInvokerPlugin invokerPlugin = new BPMNInvokerPlugin();

    private BPMNProcessFragments planBuilderFragments;

    private BPMNSubprocessHandler bpmnSubprocessHandler;

    public BPMNDockerContainerTypePluginHandler() {
        try {
            this.planBuilderFragments = new BPMNProcessFragments();
            this.bpmnSubprocessHandler = new BPMNSubprocessHandler();
        } catch (final ParserConfigurationException e) {
            BPMNDockerContainerTypePluginHandler.LOG.error("Couldn't initialize planBuilderFragments class");
            e.printStackTrace();
        }
    }

    public static TDeploymentArtifact fetchFirstDockerContainerDA(final TNodeTemplate nodeTemplate, Csar csar) {
        return getTDeploymentArtifact(nodeTemplate, csar);
    }

    public static List<TDeploymentArtifact> fetchVolumeDeploymentArtifacts(final TNodeTemplate nodeTemplate, Csar csar) {
        final List<TDeploymentArtifact> das = new ArrayList<>();

        for (final TDeploymentArtifact da : nodeTemplate.getDeploymentArtifacts()) {
            if (da.getArtifactType().equals(DockerContainerTypePluginPluginConstants.DOCKER_VOLUME_ARTIFACTTYPE)) {
                das.add(da);
            }
        }

        for (final TNodeTypeImplementation nodeTypeImpl : ModelUtils.findNodeTypeImplementation(nodeTemplate, csar)) {
            for (final TDeploymentArtifact da : nodeTypeImpl.getDeploymentArtifacts()) {
                if (da.getArtifactType().equals(DockerContainerTypePluginPluginConstants.DOCKER_VOLUME_ARTIFACTTYPE)) {
                    das.add(da);
                }
            }
        }

        return das;
    }

    public static void addProperties(Variable sshPortVar, Variable containerIpVar, Variable containerIdVar, Variable envMappingVar, Variable linksVar, Variable deviceMappingVar, Map<String, Variable> createDEInternalExternalPropsInput, Map<String, Variable> createDEInternalExternalPropsOutput) {
        if (envMappingVar != null) {
            createDEInternalExternalPropsInput.put("ContainerEnv", envMappingVar);
        }

        if (deviceMappingVar != null) {
            createDEInternalExternalPropsInput.put("Devices", deviceMappingVar);
        }

        if (linksVar != null) {
            createDEInternalExternalPropsInput.put("Links", linksVar);
        }

        if (sshPortVar != null) {
            // we expect a sshPort back -> add to output handling
            createDEInternalExternalPropsOutput.put("SSHPort", sshPortVar);
            createDEInternalExternalPropsInput.put("SSHPort", sshPortVar);
        }

        if (containerIpVar != null) {
            createDEInternalExternalPropsOutput.put("ContainerIP", containerIpVar);
        }

        if (containerIdVar != null) {
            createDEInternalExternalPropsOutput.put("ContainerID", containerIdVar);
        }
    }

    private boolean handleTerminate(final BPMNPlanContext context, Element elementToAppendTo) {
        final List<TNodeTemplate> nodes = new ArrayList<>();
        ModelUtils.getNodesFromNodeToSink(context.getNodeTemplate(), nodes, context.getCsar());

        for (TNodeTemplate node : nodes) {
            if (org.opentosca.container.core.convention.Utils.isSupportedDockerEngineNodeType(node.getType())) {

                final Map<String, Variable> createDEInternalExternalPropsInput = new HashMap<>();
                final Map<String, Variable> createDEInternalExternalPropsOutput = new HashMap<>();

                final Variable dockerEngineUrlVar = context.getPropertyVariable(node, "DockerEngineURL");
                final Variable dockerContainerIds = context.getPropertyVariable(context.getNodeTemplate(), "ContainerID");

                createDEInternalExternalPropsInput.put("DockerEngineURL", dockerEngineUrlVar);
                createDEInternalExternalPropsInput.put("ContainerID", dockerContainerIds);

                return this.invokerPlugin.handle(context, node, true,
                    Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_DOCKERENGINE_REMOVECONTAINER,
                    Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_DOCKERENGINE,
                    createDEInternalExternalPropsInput, createDEInternalExternalPropsOutput,
                    elementToAppendTo);
            }
        }

        return false;
    }

    public boolean handleTerminate(final BPMNPlanContext context) {
        return this.handleTerminate(context, context.getSubprocessElement().getBpmnSubprocessElement());
    }

    @Override
    public boolean handleCreate(final BPMNPlanContext templateContext) {
        if (templateContext.getNodeTemplate() == null) {
            BPMNDockerContainerTypePluginHandler.LOG.warn("Appending logic to relationshipTemplate plan is not possible by this plugin");
            return false;
        }
        LOG.info("inside BPMN docker container plugin handler method: handle create");
        final TNodeTemplate nodeTemplate = templateContext.getNodeTemplate();

        // fetch port binding variables (ContainerPort, Port)
        final PropertyVariable containerPortVar = templateContext.getPropertyVariable(nodeTemplate, "ContainerPort");
        final PropertyVariable portVar = templateContext.getPropertyVariable(nodeTemplate, "Port");

        if (containerPortVar == null | portVar == null) {
            BPMNDockerContainerTypePluginHandler.LOG.error("Couldn't fetch Property variables ContainerPort or Port");
            return false;
        }

        //final Variable portMappingVar =
        //    templateContext.createGlobalStringVariable("dockerContainerPortMappings" + System.currentTimeMillis(), "");
        final Variable portMappingVar = new Variable("dockerContainerPortMappings" + System.currentTimeMillis());

        // fetch (optional) SSHPort variable
        final Variable sshPortVar = templateContext.getPropertyVariable(nodeTemplate, "SSHPort");

        // fetch (optional) ContainerIP variable
        final Variable containerIpVar = templateContext.getPropertyVariable(nodeTemplate, "ContainerIP");

        // fetch (optional) ContainerID variable
        final Variable containerIdVar = templateContext.getPropertyVariable(nodeTemplate, "ContainerID");

        // fetch DockerEngine
        final TNodeTemplate dockerEngineNode = DockerContainerTypePlugin.getDockerEngineNode(nodeTemplate, templateContext.getCsar());

        if (dockerEngineNode == null) {
            BPMNDockerContainerTypePluginHandler.LOG.error("Couldn't fetch DockerEngineNode to install given DockerContainer NodeTemplate");
            return false;
        }

        // fetch the DockerIp
        final Variable dockerEngineUrlVar = templateContext.getPropertyVariable(dockerEngineNode, "DockerEngineURL");

        // determine whether we work with an ImageId or a zipped DockerContainer
        final PropertyVariable containerImageVar = templateContext.getPropertyVariable(nodeTemplate, "ImageID");

        /* volume data handling */
        // <ContainerMountPath>/etc/openmtc/certs</ContainerMountPath>
        // <HostMountFiles>/home/ubuntu/ca-smartorchestra.crt</HostMountFiles>

        final PropertyVariable containerMountPath =
            templateContext.getPropertyVariable(nodeTemplate, "ContainerMountPath");

        Variable remoteVolumeDataVariable = null;
        PropertyVariable hostVolumeDataVariable = null;
        Variable vmIpVariable = null;
        Variable vmPrivateKeyVariable = null;

        if (containerMountPath != null && !PluginUtils.isVariableValueEmpty(containerMountPath)) {

            final List<TDeploymentArtifact> volumeDas = fetchVolumeDeploymentArtifacts(nodeTemplate, templateContext.getCsar());

            if (!volumeDas.isEmpty()) {
                remoteVolumeDataVariable = createRemoteVolumeDataInputVariable(volumeDas, templateContext);
            }

            hostVolumeDataVariable = templateContext.getPropertyVariable(nodeTemplate, "HostMountFiles");

            if (hostVolumeDataVariable != null && !PluginUtils.isVariableValueEmpty(hostVolumeDataVariable)) {
                final TNodeTemplate infraNode = findInfrastructureTemplate(templateContext, dockerEngineNode);
                vmIpVariable = findVMIP(templateContext, infraNode);
                vmPrivateKeyVariable = findPrivateKey(templateContext, infraNode);
            }
        }

        if (containerImageVar == null || PluginUtils.isVariableValueEmpty(containerImageVar)) {
            // handle with DA -> construct URL to the DockerImage .zip

            LOG.info("handle create with da case");
            final TDeploymentArtifact da = fetchFirstDockerContainerDA(nodeTemplate, templateContext.getCsar());
            return handleWithDA(templateContext, dockerEngineNode, da, portMappingVar, dockerEngineUrlVar, sshPortVar,
                containerIpVar, containerIdVar,
                fetchEnvironmentVariables(templateContext, nodeTemplate), null, null,
                containerMountPath, remoteVolumeDataVariable, hostVolumeDataVariable, vmIpVariable,
                vmPrivateKeyVariable);
        } else {
            // handle with imageId

            LOG.info("handle create with image id");
            return handleWithImageId(templateContext, dockerEngineNode, containerImageVar, portMappingVar,
                dockerEngineUrlVar, sshPortVar, containerIpVar, containerIdVar,
                fetchEnvironmentVariables(templateContext, nodeTemplate), containerMountPath,
                remoteVolumeDataVariable, hostVolumeDataVariable, vmIpVariable,
                vmPrivateKeyVariable);
        }
    }

    private TNodeTemplate findInfrastructureTemplate(final PlanContext context,
                                                     final TNodeTemplate nodeTemplate) {
        final List<TNodeTemplate> infraNodes = new ArrayList<>();
        ModelUtils.getInfrastructureNodes(nodeTemplate, infraNodes, context.getCsar());

        for (final TNodeTemplate infraNode : infraNodes) {
            if (!infraNode.getId().equals(nodeTemplate.getId()) & ModelUtils.getPropertyNames(infraNode).contains("VMIP")) {
                // fetch the first which is not a dockercontainer
                return infraNode;
            }
        }

        return null;
    }

    private Variable findVMIP(final PlanContext templateContext, final TNodeTemplate infraTemplate) {
        Variable serverIpPropWrapper = null;
        for (final String serverIpName : org.opentosca.container.core.convention.Utils.getSupportedVirtualMachineIPPropertyNames()) {
            serverIpPropWrapper = templateContext.getPropertyVariable(infraTemplate, serverIpName);
            if (serverIpPropWrapper != null) {
                break;
            }
        }
        return serverIpPropWrapper;
    }

    private Variable findPrivateKey(final PlanContext templateContext, final TNodeTemplate infraTemplate) {
        Variable sshKeyVariable = null;
        for (final String vmLoginPassword : org.opentosca.container.core.convention.Utils.getSupportedVirtualMachineLoginPasswordPropertyNames()) {
            sshKeyVariable = templateContext.getPropertyVariable(infraTemplate, vmLoginPassword);
            if (sshKeyVariable != null) {
                break;
            }
        }
        return sshKeyVariable;
    }

    private Variable createRemoteVolumeDataInputVariable(final List<TDeploymentArtifact> das,
                                                         final BPMNPlanContext context) {

        //final Variable remoteVolumeDataVariable =
        //    context.createGlobalStringVariable("remoteVolumeData" + System.currentTimeMillis(), "");
        final Variable remoteVolumeDataVariable = new Variable("remoteVolumeData" + System.currentTimeMillis());

        StringBuilder remoteVolumeDataVarAssignQuery = new StringBuilder("concat(");

        for (final TDeploymentArtifact da : das) {
            for (final TArtifactReference ref : ModelUtils.findArtifactTemplate(da.getArtifactRef(), context.getCsar()).getArtifactReferences()) {
                // $input.payload//*[local-name()='instanceDataAPIUrl']
                remoteVolumeDataVarAssignQuery.append("$input.payload//*[local-name()='csarEntrypoint'],'/Content/").append(ref.getReference()).append(";',");
            }
        }

        remoteVolumeDataVarAssignQuery = new StringBuilder(remoteVolumeDataVarAssignQuery.substring(0, remoteVolumeDataVarAssignQuery.length() - 1));
        remoteVolumeDataVarAssignQuery.append(")");

        /*
        try {
            Node assignContainerEnvNode =
                this.planBuilderFragments.createAssignXpathQueryToStringVarFragmentAsNode("assignVolumeDataVariable",
                    remoteVolumeDataVarAssignQuery.toString(),
                    remoteVolumeDataVariable.getVariableName());
            assignContainerEnvNode = context.importNode(assignContainerEnvNode);
            context.getProvisioningPhaseElement().appendChild(assignContainerEnvNode);
        } catch (final IOException | SAXException e) {
            LOG.error("Error assigning container environment node", e);
        }
        */
        return remoteVolumeDataVariable;
    }

    /**
     * Checks whether there are properties which start with "ENV_" in the name and generates a variable for all of these
     * properties to pass them as environment variables to a docker container
     */
    private Variable fetchEnvironmentVariables(final BPMNPlanContext context, final TNodeTemplate nodeTemplate) {
        final Collection<String> propertyNames = ModelUtils.getPropertyNames(nodeTemplate);
        StringBuilder envVarXpathQuery = new StringBuilder("concat(");

        boolean foundEnvVar = false;
        for (final String propName : propertyNames) {
            if (propName.startsWith("ENV_")) {
                final PropertyVariable propVar = context.getPropertyVariable(nodeTemplate, propName);

                String varContent = propVar.getContent();

                // FIXME brutal hack right now
                if (varContent.contains("get_property")) {
                    // concatenation required
                    if (varContent.contains("[") && varContent.contains("]")) {

                        foundEnvVar = true;
                        final String envVarName = propName.replaceFirst("ENV_", "");
                        envVarXpathQuery.append("'").append(envVarName).append("='");

                        while (!varContent.isEmpty()) {

                            final int startIndex = varContent.indexOf("[");
                            final int endIndex = varContent.indexOf("]");

                            if (startIndex == 0) {

                                final String dynamicContent = varContent.substring(startIndex, endIndex);

                                final String[] splits = dynamicContent.split(" ");
                                final String nodeTemplateId = splits[1];
                                final String propertyName = splits[2];

                                final TNodeTemplate refNode = getNode(nodeTemplateId, context);
                                final Variable refProp = context.getPropertyVariable(refNode, propertyName);

                                envVarXpathQuery.append(",$").append(refProp.getVariableName());
                                varContent = varContent.replace(dynamicContent + "]", "");
                            } else {
                                String staticContent;
                                if (startIndex == -1) {
                                    staticContent = varContent;
                                } else {
                                    staticContent = varContent.substring(0, startIndex);
                                }

                                envVarXpathQuery.append(",'").append(staticContent).append("'");
                                varContent = varContent.replace(staticContent, "");
                            }
                        }
                        envVarXpathQuery.append(",';',");
                    } else {
                        final String[] splits = varContent.split(" ");
                        final String nodeTemplateId = splits[1];
                        final String propertyName = splits[2];

                        final TNodeTemplate refNode = getNode(nodeTemplateId, context);
                        final Variable refProp = context.getPropertyVariable(refNode, propertyName);
                        foundEnvVar = true;
                        final String envVarName = propName.replaceFirst("ENV_", "");
                        envVarXpathQuery.append("'").append(envVarName).append("=',$").append(refProp.getVariableName()).append(",';',");
                    }
                } else {
                    foundEnvVar = true;
                    final String envVarName = propName.replaceFirst("ENV_", "");
                    envVarXpathQuery.append("'").append(envVarName).append("=',$").append(propVar.getVariableName()).append(",';',");
                }
            }
        }

        if (!foundEnvVar) {
            return null;
        }

        //final Variable envMappingVar =
        //    context.createGlobalStringVariable("dockerContainerEnvironmentMappings" + System.currentTimeMillis(), "");
        final Variable envMappingVar = new Variable("dockerContainerEnvironmentMappings" + System.currentTimeMillis());

        envVarXpathQuery = new StringBuilder(envVarXpathQuery.substring(0, envVarXpathQuery.length() - 1));
        envVarXpathQuery.append(")");

        /*
        try {
            Node assignContainerEnvNode =
                this.planBuilderFragments.createAssignXpathQueryToStringVarFragmentAsNode("assignEnvironmentVariables",
                    envVarXpathQuery.toString(),
                    envMappingVar.getVariableName());
            assignContainerEnvNode = context.importNode(assignContainerEnvNode);
            context.getProvisioningPhaseElement().appendChild(assignContainerEnvNode);
        } catch (final IOException | SAXException e) {
            LOG.error("Error while assigning environment vars...", e);
        }
*/
        return envMappingVar;
    }

    private TNodeTemplate getNode(final String id, final PlanContext ctx) {

        for (final TNodeTemplate nodeTemplate : ctx.getNodeTemplates()) {
            if (nodeTemplate.getId().equals(id)) {
                return nodeTemplate;
            }
        }
        return null;
    }

    public String
    createXPathQueryForURLRemoteFilePathViaContainerAPI(final String artifactPath, final String csarId) {
        BPMNDockerContainerTypePluginHandler.LOG.debug("Generating XPATH Query for ArtifactPath: " + artifactPath);
        return "string(concat($input.payload//*[local-name()='containerApiAddress']/text(),'/csars/" + csarId + "', '/content/"
            + artifactPath + "'))";
    }

    /**
     * @param da      deployment artifact
     * @param context contains subprocess for current task
     * @return String containing the DA for processing inside callNodeOperation groovy script
     */
    public String createDAinput(TDeploymentArtifact da, BPMNPlanContext context) {
        final TArtifactTemplate artifactTemplate = ModelUtils.findArtifactTemplate(da.getArtifactRef(), context.getCsar());
        String reference = artifactTemplate.getArtifactReferences().get(0).getReference();
        // reference="artifacttemplates/http%253A%252F%252Fopentosca.org%252Fartifacttemplates/MyTinyToDo_DA/files/tinytodo.zip"/>
        String[] directories = reference.split("/");
        String fileName = null;
        //String id = artifactTemplate.getId();
        String id = "/content/artifacttemplates/" + directories[1] + "/" + artifactTemplate.getId();
        for (int i = 0; i < directories.length; i += 1) {
            if (directories[i].equals("files")) {
                fileName = directories[i + 1];
                break;
            }
        }
        return "DA!" + id + "/files/" + fileName;
    }

    protected boolean handleWithDA(final BPMNPlanContext context, final TNodeTemplate dockerEngineNode,
                                   final TDeploymentArtifact da, final Variable portMappingVar,
                                   final Variable dockerEngineUrlVar, final Variable sshPortVar,
                                   final Variable containerIpVar, final Variable containerIdVar,
                                   final Variable envMappingVar, final Variable linksVar,
                                   final Variable deviceMappingVar, final Variable containerMountPath,
                                   final Variable remoteVolumeDataVariable, final Variable hostVolumeDataVariable,
                                   final Variable vmIpVariable, final Variable vmPrivateKeyVariable) {

        //context.addStringValueToPlanRequest("containerApiAddress");

        final String artifactPathQuery =
            this.createXPathQueryForURLRemoteFilePathViaContainerAPI(ModelUtils.findArtifactTemplate(da.getArtifactRef(), context.getCsar()).getArtifactReferences().stream().findFirst().get()
                .getReference(), context.getCSARFileName());

        // create and set input for Input_DA
        final String DAinputVariable = createDAinput(da, context);
        context.getSubprocessElement().setDAstring(DAinputVariable);

        final String artefactVarName = "dockerContainerFile" + System.currentTimeMillis();

        //value = xpath query for DA artifact
        final Variable dockerContainerFileRefVar = new Variable(artefactVarName);
        LOG.info("DA query");
        LOG.info(artifactPathQuery);
        //final Variable dockerContainerFileRefVar = new Variable(artifactPathQuery);
        //final Variable dockerContainerFileRefVar = context.createGlobalStringVariable(artefactVarName, "");

        /*
        try {
            Node assignNode =
                this.planBuilderFragments.createAssignXpathQueryToStringVarFragmentAsNode("assignDockerContainerFileRef"
                    + System.currentTimeMillis(), artifactPathQuery, dockerContainerFileRefVar.getVariableName());
            assignNode = context.importNode(assignNode);
            context.getProvisioningPhaseElement().appendChild(assignNode);
        } catch (final IOException | SAXException e) {
            e.printStackTrace();
        }
       */

        // map properties to input and output parameters
        final Map<String, Variable> createDEInternalExternalPropsInput = new HashMap<>();
        final Map<String, Variable> createDEInternalExternalPropsOutput = new HashMap<>();

        //Variable richtigeDockerengineURL = new Variable("String!${DockerEngineURL}");
        //dockerEngineUrlVar.setVariablename("String!${DockerEngineURL}");
        createDEInternalExternalPropsInput.put("ImageLocation", dockerContainerFileRefVar);
        createDEInternalExternalPropsInput.put("DockerEngineURL", dockerEngineUrlVar);
        //createDEInternalExternalPropsInput.put("DockerEngineURL", richtigeDockerengineURL);
        //createDEInternalExternalPropsInput.put("ContainerPorts", portMappingVar);
        Variable test = new Variable(DockerContainerTypePluginPluginConstants.PROPERTY_CONTAINER_PORT + "," + DockerContainerTypePluginPluginConstants.PROPERTY_PORT);
        createDEInternalExternalPropsInput.put("ContainerPorts", test);
            // test
            //Variable davariable = new Variable(artifactPathQuery);
            //createDEInternalExternalPropsInput.put("DA", davariable);

            createPropertiesMapping(containerMountPath, remoteVolumeDataVariable, hostVolumeDataVariable, vmIpVariable, vmPrivateKeyVariable, createDEInternalExternalPropsInput);

        addProperties(sshPortVar, containerIpVar, containerIdVar, envMappingVar, linksVar, deviceMappingVar, createDEInternalExternalPropsInput, createDEInternalExternalPropsOutput);

        return this.invokerPlugin.handle(context, dockerEngineNode, true,
            Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_DOCKERENGINE_STARTCONTAINER,
            Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_DOCKERENGINE,
            createDEInternalExternalPropsInput, createDEInternalExternalPropsOutput,
            context.getSubprocessElement().getBpmnSubprocessElement());
        //&& this.handleTerminate(context, context.getSubprocessElement().getBpmnScopeElement());
    }

    protected boolean handleWithImageId(final BPMNPlanContext context, final TNodeTemplate dockerEngineNode,
                                        final Variable containerImageVar, final Variable portMappingVar,
                                        final Variable dockerEngineUrlVar, final Variable sshPortVar,
                                        final Variable containerIpVar, final Variable containerIdVar,
                                        final Variable envMappingVar, final Variable containerMountPath,
                                        final Variable remoteVolumeDataVariable, final Variable hostVolumeDataVariable,
                                        final Variable vmIpVariable, final Variable vmPrivateKeyVariable) {

        // map properties to input and output parameters
        final Map<String, Variable> createDEInternalExternalPropsInput = new HashMap<>();
        final Map<String, Variable> createDEInternalExternalPropsOutput = new HashMap<>();

        createDEInternalExternalPropsInput.put("ContainerImage", containerImageVar);
        createDEInternalExternalPropsInput.put("DockerEngineURL", dockerEngineUrlVar);
        createDEInternalExternalPropsInput.put("ContainerPorts", portMappingVar);

        if (envMappingVar != null) {
            createDEInternalExternalPropsInput.put("ContainerEnv", envMappingVar);
        }

        if (sshPortVar != null) {
            // we expect a sshPort back -> add to output handling
            createDEInternalExternalPropsOutput.put("SSHPort", sshPortVar);
        }

        if (containerIpVar != null) {
            createDEInternalExternalPropsOutput.put("ContainerIP", containerIpVar);
        }

        if (containerIdVar != null) {
            createDEInternalExternalPropsOutput.put("ContainerID", containerIdVar);
        }

        createPropertiesMapping(containerMountPath, remoteVolumeDataVariable, hostVolumeDataVariable, vmIpVariable, vmPrivateKeyVariable, createDEInternalExternalPropsInput);

        boolean check = this.invokerPlugin.handle(context, dockerEngineNode, true,
            Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_DOCKERENGINE_STARTCONTAINER,
            Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_DOCKERENGINE,
            createDEInternalExternalPropsInput, createDEInternalExternalPropsOutput,
            context.getSubprocessElement().getBpmnSubprocessElement());

        //check &= this.handleTerminate(context, context.getSubprocessElement().getBpmnScopeElement());

        return check;
    }

    private void createPropertiesMapping(Variable containerMountPath, Variable remoteVolumeDataVariable, Variable hostVolumeDataVariable, Variable vmIpVariable, Variable vmPrivateKeyVariable, Map<String, Variable> createDEInternalExternalPropsInput) {
        if (containerMountPath != null) {
            if (remoteVolumeDataVariable != null) {
                createDEInternalExternalPropsInput.put("RemoteVolumeData", remoteVolumeDataVariable);
            }
            if (hostVolumeDataVariable != null && vmIpVariable != null && vmPrivateKeyVariable != null) {
                createDEInternalExternalPropsInput.put("HostVolumeData", hostVolumeDataVariable);
                createDEInternalExternalPropsInput.put("VMIP", vmIpVariable);
                createDEInternalExternalPropsInput.put("VMPrivateKey", vmPrivateKeyVariable);
            }
            createDEInternalExternalPropsInput.put("ContainerMountPath", containerMountPath);
        }
    }
}

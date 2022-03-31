package org.opentosca.planbuilder.type.plugin.callnodeoperation.bpmn;

import javax.xml.namespace.QName;

/**
 * reference: org/opentosca/planbuilder/type/plugin/dockercontainer/core/DockerContainerTypePluginPluginConstants.java
 */
public class DockerContainerConstants {

    public final static QName DOCKER_CONTAINER_NODETYPE = new QName("http://opentosca.org/nodetype", "DockerContainer");
    public final static QName DOCKER_CONTAINER_NODETYPE2 = new QName("http://opentosca.org/nodetypes", "DockerContainer");
    public final static QName DOCKER_CONTAINER_ARTIFACTTYPE_OLD =
        new QName("http://opentosca.org/artefacttypes", "DockerContainerArtefact");
    public final static QName DOCKER_CONTAINER_ARTIFACTTYPE =
        new QName("http://opentosca.org/artifacttypes", "DockerContainerArtifact");

    public final static QName DOCKER_VOLUME_ARTIFACTTYPE =
        new QName("http://opentosca.org/artifacttypes", "DockerVolumeArtifact_1-w1-wip1");

    public final static QName OPENMTC_BACKEND_SERVICE_NODETYPE = new QName("http://opentosca.org/nodetypes", "OpenMTC");
    public final static QName OPENMTC_GATEWAY_DOCKER_CONTAINER_NODETYPE =
        new QName("http://opentosca.org/nodetypes", "OpenMTCDockerContainerGateway");
    public final static QName OPENMTC_PROTOCOL_ADAPTER_DOCKER_CONTAINER_NODETYPE =
        new QName("http://opentosca.org/nodetypes", "OpenMTCDockerContainerProtocolAdapter");

    public final static String PROPERTY_CONTAINER_PORT = "ContainerPort";
    public final static String PROPERTY_PORT = "Port";
    public final static String PROPERTY_SSHPORT = "SSHPort";
    public final static String PROPERTY_CONTAINER_IP = "ContainerIP";
    public final static String PROPERTY_CONTAINER_ID = "ContainerID";
    public final static String PROPERTY_DOCKER_ENGINE_URL = "DockerEngineURL";
    public final static String PROPERTY_IMAGE_ID = "ImageID";
    public static final String PROPERTY_CONTAINER_MOUNT_PATH = "ContainerMountPath";

}
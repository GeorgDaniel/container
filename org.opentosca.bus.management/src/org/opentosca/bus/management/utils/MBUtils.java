package org.opentosca.bus.management.utils;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.xml.namespace.QName;

import org.opentosca.bus.management.servicehandler.ServiceHandler;
import org.opentosca.container.core.model.csar.id.CSARID;
import org.opentosca.container.core.next.model.NodeTemplateInstance;
import org.opentosca.container.core.next.model.RelationshipTemplateInstance;
import org.opentosca.container.core.next.model.ServiceTemplateInstance;
import org.opentosca.container.core.next.repository.NodeTemplateInstanceRepository;
import org.opentosca.container.core.next.repository.ServiceTemplateInstanceRepository;
import org.opentosca.container.core.tosca.convention.Interfaces;
import org.opentosca.container.core.tosca.convention.Types;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.traversal.DocumentTraversal;
import org.w3c.dom.traversal.NodeFilter;
import org.w3c.dom.traversal.NodeIterator;

public class MBUtils {

    final private static Logger LOG = LoggerFactory.getLogger(MBUtils.class);

    // repository to access NodeTemplateInstance data
    final private static NodeTemplateInstanceRepository nodeTemplateInstanceRepository =
        new NodeTemplateInstanceRepository();

    // repository to access ServiceTemplateInstance data
    final private static ServiceTemplateInstanceRepository serviceTemplateInstanceRepository =
        new ServiceTemplateInstanceRepository();


    /**
     *
     * Returns the OperatingSystem NodeTemplate.
     *
     * @param csarID
     * @param serviceTemplateID
     * @param nodeTemplateID
     *
     * @return name of the OperatingSystem NodeTemplate.
     */
    public static String getOperatingSystemNodeTemplateID(final CSARID csarID, final QName serviceTemplateID,
                                                          String nodeTemplateID) {

        MBUtils.LOG.debug("Searching the OperatingSystemNode of NodeTemplate: {}, ServiceTemplate: {} & CSAR: {} ...",
                          nodeTemplateID, serviceTemplateID, csarID);

        QName nodeType =
            ServiceHandler.toscaEngineService.getNodeTypeOfNodeTemplate(csarID, serviceTemplateID, nodeTemplateID);

        while (!isOperatingSystemNodeType(csarID, nodeType) && nodeTemplateID != null) {

            MBUtils.LOG.debug("{} isn't the OperatingSystemNode.", nodeTemplateID);
            MBUtils.LOG.debug("Getting the underneath Node for checking if it is the OperatingSystemNode...");

            // try different relationshiptypes with priority on hostedOn
            nodeTemplateID =
                ServiceHandler.toscaEngineService.getRelatedNodeTemplateID(csarID, serviceTemplateID, nodeTemplateID,
                                                                           Types.hostedOnRelationType);

            if (nodeTemplateID == null) {
                nodeTemplateID =
                    ServiceHandler.toscaEngineService.getRelatedNodeTemplateID(csarID, serviceTemplateID,
                                                                               nodeTemplateID,
                                                                               Types.deployedOnRelationType);

                if (nodeTemplateID == null) {
                    nodeTemplateID =
                        ServiceHandler.toscaEngineService.getRelatedNodeTemplateID(csarID, serviceTemplateID,
                                                                                   nodeTemplateID,
                                                                                   Types.dependsOnRelationType);
                }

            }

            if (nodeTemplateID != null) {
                MBUtils.LOG.debug("Checking if the underneath Node: {} is the OperatingSystemNode.", nodeTemplateID);
                nodeType = ServiceHandler.toscaEngineService.getNodeTypeOfNodeTemplate(csarID, serviceTemplateID,
                                                                                       nodeTemplateID);

            } else {
                MBUtils.LOG.debug("No underneath Node found.");
            }
        }

        if (nodeTemplateID != null) {
            MBUtils.LOG.debug("OperatingSystemNode found: {}", nodeTemplateID);
        }

        return nodeTemplateID;
    }

    /**
     *
     * Checks if the specified NodeType is the OperatingSystem NodeType.
     *
     * @param csarID
     * @param nodeType
     * @return true if the specified NodeType is the OperatingSystem NodeType. Otherwise false.
     */
    private static boolean isOperatingSystemNodeType(final CSARID csarID, final QName nodeType) {
        if (ServiceHandler.toscaEngineService.doesInterfaceOfNodeTypeContainOperation(csarID, nodeType,
                                                                                      Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_OPERATINGSYSTEM,
                                                                                      Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_OPERATINGSYSTEM_RUNSCRIPT)
            && ServiceHandler.toscaEngineService.doesInterfaceOfNodeTypeContainOperation(csarID, nodeType,
                                                                                         Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_OPERATINGSYSTEM,
                                                                                         Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_OPERATINGSYSTEM_TRANSFERFILE)
            && ServiceHandler.toscaEngineService.doesInterfaceOfNodeTypeContainOperation(csarID, nodeType,
                                                                                         Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_OPERATINGSYSTEM,
                                                                                         Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_OPERATINGSYSTEM_INSTALLPACKAGE)) {
            return true;
        } else if (ServiceHandler.toscaEngineService.doesInterfaceOfNodeTypeContainOperation(csarID, nodeType,
                                                                                             Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_DOCKERCONTAINER,
                                                                                             Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_DOCKERCONTAINER_RUNSCRIPT)
            && ServiceHandler.toscaEngineService.doesInterfaceOfNodeTypeContainOperation(csarID, nodeType,
                                                                                         Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_DOCKERCONTAINER,
                                                                                         Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_DOCKERCONTAINER_TRANSFERFILE)) {
            return true;
        }
        return false;
    }

    /**
     * Returns the OS interface of the given OS Node Type
     *
     * @param csarID the CSAR Id where the referenced Node Type is declared
     * @param nodeType a QName of the Node Type to check
     * @return a String containing the name of the OS interface, or if the given Node Type is not an
     *         OS Node Type null
     */
    public static String getInterfaceForOperatingSystemNodeType(final CSARID csarID, final QName nodeType) {
        if (ServiceHandler.toscaEngineService.doesInterfaceOfNodeTypeContainOperation(csarID, nodeType,
                                                                                      Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_OPERATINGSYSTEM,
                                                                                      Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_OPERATINGSYSTEM_RUNSCRIPT)
            && ServiceHandler.toscaEngineService.doesInterfaceOfNodeTypeContainOperation(csarID, nodeType,
                                                                                         Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_OPERATINGSYSTEM,
                                                                                         Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_OPERATINGSYSTEM_TRANSFERFILE)
            && ServiceHandler.toscaEngineService.doesInterfaceOfNodeTypeContainOperation(csarID, nodeType,
                                                                                         Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_OPERATINGSYSTEM,
                                                                                         Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_OPERATINGSYSTEM_INSTALLPACKAGE)) {
            return Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_OPERATINGSYSTEM;
        } else if (ServiceHandler.toscaEngineService.doesInterfaceOfNodeTypeContainOperation(csarID, nodeType,
                                                                                             Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_DOCKERCONTAINER,
                                                                                             Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_DOCKERCONTAINER_RUNSCRIPT)
            && ServiceHandler.toscaEngineService.doesInterfaceOfNodeTypeContainOperation(csarID, nodeType,
                                                                                         Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_DOCKERCONTAINER,
                                                                                         Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_DOCKERCONTAINER_TRANSFERFILE)) {
            return Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_DOCKERCONTAINER;
        }
        return null;
    }

    /**
     *
     * Returns the name of the OperatingSystem ImplementationArtifact.
     *
     * @param csarID
     * @param serviceTemplateID
     * @param osNodeTemplateID
     *
     *
     * @return name of the OperatingSystem ImplementationArtifact.
     */
    public static String getOperatingSystemIA(final CSARID csarID, final QName serviceTemplateID,
                                              final String osNodeTemplateID) {

        MBUtils.LOG.debug("Searching the OperatingSystem-IA of NodeTemplate: {}, ServiceTemplate: {} & CSAR: {} ...",
                          osNodeTemplateID, serviceTemplateID, csarID);

        final QName osNodeType =
            ServiceHandler.toscaEngineService.getNodeTypeOfNodeTemplate(csarID, serviceTemplateID, osNodeTemplateID);

        final List<QName> osNodeTypeImpls =
            ServiceHandler.toscaEngineService.getNodeTypeImplementationsOfNodeType(csarID, osNodeType);

        for (final QName osNodeTypeImpl : osNodeTypeImpls) {

            MBUtils.LOG.debug("NodeTypeImpl: {} ", osNodeTypeImpl);

            final List<String> osIANames =
                ServiceHandler.toscaEngineService.getImplementationArtifactNamesOfNodeTypeImplementation(csarID,
                                                                                                         osNodeTypeImpl);

            for (final String osIAName : osIANames) {

                MBUtils.LOG.debug("IA: {} ", osIAName);

                final String osIAInterface =
                    ServiceHandler.toscaEngineService.getInterfaceOfAImplementationArtifactOfANodeTypeImplementation(csarID,
                                                                                                                     osNodeTypeImpl, osIAName);

                MBUtils.LOG.debug("Interface: {} ", osIAInterface);

                if (osIAInterface == null
                    || osIAInterface.equals(Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_OPERATINGSYSTEM)
                    || osIAInterface.equals(Interfaces.OPENTOSCA_DECLARATIVE_INTERFACE_DOCKERCONTAINER)) {
                    return osIAName;

                }
            }
        }

        return null;
    }

    /**
     * Traverses the topology and searches for the specified property. If found, the value from the
     * instance data is returned.
     *
     * @param nodeTemplateInstanceID the ID of the NodeTemplateInstance where the search should be
     *        started in downwards direction
     * @param property the name of the property that is searched
     * @return instance data value of searched property if found, <tt>null</tt> otherwise.
     */
    public static String searchProperty(NodeTemplateInstance nodeTemplateInstance, final String property) {

        MBUtils.LOG.debug("Searching the Property: {} in or under the NodeTemplateInstance ID: {} ...", property,
                          nodeTemplateInstance.getId());

        // check if property is already defined at this NodeTemplateInstance
        String propertyValue = getInstanceDataPropertyValue(nodeTemplateInstance, property);

        // search until property is found or no new NodeTemplateInstance is found
        boolean moreNodeTemplateInstances = true;
        while (propertyValue == null && moreNodeTemplateInstances) {
            MBUtils.LOG.debug("Property not found at NodeTemplate: {}", nodeTemplateInstance.getTemplateId());
            moreNodeTemplateInstances = false;

            // perform search in downwards direction in the topology
            final Collection<RelationshipTemplateInstance> outgoingRelations =
                nodeTemplateInstance.getOutgoingRelations();

            for (final RelationshipTemplateInstance relation : outgoingRelations) {
                final QName relationType = relation.getTemplateType();
                MBUtils.LOG.debug("Found outgoing relation of Type: {}", relationType);

                // only follow relations of kind hostedOn, deployedOn and dependsOn
                if (relationType.equals(Types.hostedOnRelationType) || relationType.equals(Types.deployedOnRelationType)
                    || relationType.equals(Types.dependsOnRelationType)) {

                    nodeTemplateInstance = relation.getTarget();
                    moreNodeTemplateInstances = true;

                    MBUtils.LOG.debug("Found new NodeTemplate: {}. Continue property search.",
                                      nodeTemplateInstance.getTemplateId());

                    // check if new NodeTemplateInstance contains property
                    propertyValue = getInstanceDataPropertyValue(nodeTemplateInstance, property);
                    break;
                } else {
                    MBUtils.LOG.debug("RelationshipType is not valid for property search (e.g. hostedOn).");
                }
            }
        }

        if (propertyValue != null) {
            MBUtils.LOG.debug("Searched property: {} with value: {} found in NodeTemplate: {}.", property,
                              propertyValue, nodeTemplateInstance.getTemplateId());
        } else {
            MBUtils.LOG.debug("Searched property: {} not found!", property);
        }

        return propertyValue;

    }

    /**
     * Returns the value of a certain property of a certain NodeTemplateInstance.
     *
     * @param nodeTemplateInstance the NodeTemplateInstance
     * @param property the name of the property
     * @return the value of the property if found, <tt>null</tt> otherwise.
     */
    public static String getInstanceDataPropertyValue(final NodeTemplateInstance nodeTemplateInstance,
                                                      final String property) {
        final Map<String, String> propertiesMap = nodeTemplateInstance.getPropertiesAsMap();

        if (propertiesMap != null) {
            return propertiesMap.get(property);
        } else {
            return null;
        }
    }

    /**
     * Returns the NodeTemplateInstance object for a certain ID.
     *
     * @param nodeTemplateInstanceID the ID of the NodeTemplateInstance
     * @return the NodeTemplateInstance if found, <tt>null</tt> otherwise
     */
    public static NodeTemplateInstance getNodeTemplateInstance(final Long nodeTemplateInstanceID) {
        MBUtils.LOG.debug("Trying to retrieve NodeTemplateInstance with ID: {}", nodeTemplateInstanceID);

        // retrieve ServiceTemplateInstance object from database
        if (nodeTemplateInstanceID != null) {
            final Optional<NodeTemplateInstance> instanceOptional =
                nodeTemplateInstanceRepository.find(nodeTemplateInstanceID);
            if (instanceOptional.isPresent()) {
                return instanceOptional.get();
            } else {
                MBUtils.LOG.debug("Unable to find NodeTemplateInstance with ID: {}", nodeTemplateInstanceID);
            }
        } else {
            MBUtils.LOG.debug("Given NodeTemplateInstance ID is null. Unable to retrieve NodeTemplateInstance.");
        }

        return null;
    }

    /**
     * Retrieve the NodeTemplateInstance which is contained in a certain ServiceTemplateInstance and
     * has a certain template ID.
     *
     * @param serviceTemplateInstanceID this ID identifies the ServiceTemplateInstance
     * @param nodeTemplateID the template ID to identify the correct instance
     * @return the found NodeTemplateInstance or <tt>null</tt> if no instance was found that matches
     *         the parameters
     */
    public static NodeTemplateInstance getNodeTemplateInstance(final Long serviceTemplateInstanceID,
                                                               final String nodeTemplateID) {
        MBUtils.LOG.debug("Trying to retrieve NodeTemplateInstance for ServiceTemplateInstance ID {} and NodeTemplate ID {} ...",
                          serviceTemplateInstanceID, nodeTemplateID);

        // retrieve ServiceTemplateInstance object from database
        if (serviceTemplateInstanceID != null) {
            final Optional<ServiceTemplateInstance> instanceOptional =
                serviceTemplateInstanceRepository.find(serviceTemplateInstanceID);
            if (instanceOptional.isPresent()) {
                MBUtils.LOG.debug("Corresponding ServiceTemplateInstance object found...");
                final ServiceTemplateInstance serviceTemplateInstance = instanceOptional.get();

                final Collection<NodeTemplateInstance> nodeTemplateInstances =
                    serviceTemplateInstance.getNodeTemplateInstances();

                // search for the NodeTemplateInstance with matching ID
                for (final NodeTemplateInstance nodeTemplateInstance : nodeTemplateInstances) {
                    if (nodeTemplateInstance.getName().equals(nodeTemplateID)) {
                        MBUtils.LOG.debug("NodeTemplateInstance has ID: {}", nodeTemplateInstance.getId());
                        return nodeTemplateInstance;
                    }
                }

                MBUtils.LOG.debug("No NodeTemplateInstance with this NodeTemplate ID contained in the ServiceTemplateInstance!");
            } else {
                MBUtils.LOG.debug("Unable to find ServiceTemplateInstance with ID: {}", serviceTemplateInstanceID);
            }
        } else {
            MBUtils.LOG.debug("Given ServiceTemplateInstance ID is null. Unable to search NodeTemplateInstance.");
        }

        return null;
    }

    /**
     * Transfers the properties document to a map.
     *
     * @param propertiesDocument to be transfered to a map.
     * @return transfered map.
     */
    public static HashMap<String, String> docToMap(final Document propertiesDocument, final boolean allowEmptyEntries) {
        final HashMap<String, String> reponseMap = new HashMap<>();

        final DocumentTraversal traversal = (DocumentTraversal) propertiesDocument;
        final NodeIterator iterator =
            traversal.createNodeIterator(propertiesDocument.getDocumentElement(), NodeFilter.SHOW_ELEMENT, null, true);

        for (Node node = iterator.nextNode(); node != null; node = iterator.nextNode()) {

            final String name = ((Element) node).getLocalName();
            final StringBuilder content = new StringBuilder();
            final NodeList children = node.getChildNodes();
            for (int i = 0; i < children.getLength(); i++) {
                final Node child = children.item(i);
                if (child.getNodeType() == Node.TEXT_NODE) {
                    content.append(child.getTextContent());
                }
            }

            if (allowEmptyEntries) {
                reponseMap.put(name, content.toString());
            } else {
                if (!content.toString().trim().isEmpty()) {
                    reponseMap.put(name, content.toString());
                }
            }

        }

        return reponseMap;
    }
}

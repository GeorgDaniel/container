package org.opentosca.container.core.engine.next;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.winery.model.ids.definitions.ArtifactTemplateId;
import org.eclipse.winery.model.tosca.TArtifactReference;
import org.eclipse.winery.model.tosca.TArtifactTemplate;
import org.eclipse.winery.model.tosca.TDeploymentArtifact;
import org.eclipse.winery.model.tosca.TNodeTemplate;
import org.eclipse.winery.model.tosca.TNodeTypeImplementation;
import org.eclipse.winery.model.tosca.TRelationshipType;

import org.opentosca.container.core.engine.ResolvedArtifacts;
import org.opentosca.container.core.model.csar.Csar;
import org.opentosca.container.core.next.model.NodeTemplateInstance;
import org.opentosca.container.core.next.model.RelationshipTemplateInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Implements Tosca-Engine-like operations for the model classes available under {@link
 * org.opentosca.container.core.next.model}
 */
@Component
public final class ContainerEngine {

    private static final Logger LOG = LoggerFactory.getLogger(ContainerEngine.class);

    //private final IXMLSerializerService xmlSerializerService;


    public static NodeTemplateInstance resolveRelationshipOperationTarget(RelationshipTemplateInstance relationshipInstance,
                                                                          TRelationshipType relationshipType,
                                                                          String interfaceName, String operationName) {
        boolean operationIsAttachedToSource = Optional.ofNullable(relationshipType.getSourceInterfaces())
            .orElse(Collections.emptyList()).stream()
            .filter(iface -> interfaceName == null || iface.getName().equals(interfaceName))
            .flatMap(iface -> iface.getOperations().stream())
            .anyMatch(op -> op.getName().equals(operationName));
        if (operationIsAttachedToSource) {
            return relationshipInstance.getSource();
        } else {
            return relationshipInstance.getTarget();
        }
    }

    public ResolvedArtifacts resolvedDeploymentArtifacts(Csar context, TNodeTemplate nodeTemplate) {
        final ResolvedArtifacts result = new ResolvedArtifacts();
        result.setDeploymentArtifacts(resolvedDeploymentArtifactsForNodeTemplate(context, nodeTemplate));
        return result;
    }

    public ResolvedArtifacts resolvedDeploymentArtifactsOfNodeTypeImpl(Csar context, TNodeTypeImplementation nodeTemplate) {
        LOG.debug("Trying to fetch DAs of NodeTypeImplementation {}", nodeTemplate.getName());
        final ResolvedArtifacts result = new ResolvedArtifacts();

        List<ResolvedArtifacts.ResolvedDeploymentArtifact> collect = nodeTemplate.getDeploymentArtifacts().stream()
            .map(da -> resolveDA(context, da))
            .collect(Collectors.toList());

        result.setDeploymentArtifacts(collect);
        return result;
    }

    public List<ResolvedArtifacts.ResolvedDeploymentArtifact> resolvedDeploymentArtifactsForNodeTemplate(Csar context, TNodeTemplate nodeTemplate) {
        LOG.debug("Trying to fetch DAs of NodeTemplate {}", nodeTemplate.getName());
        if (nodeTemplate.getDeploymentArtifacts() == null
            || nodeTemplate.getDeploymentArtifacts().isEmpty()) {
            LOG.info("NodeTemplate {} has no deployment artifacts", nodeTemplate.getName());
            return Collections.emptyList();
        }

        return nodeTemplate.getDeploymentArtifacts().stream()
            .map(da -> resolveDA(context, da))
            .collect(Collectors.toList());
    }

    private ResolvedArtifacts.ResolvedDeploymentArtifact resolveDA(Csar context, TDeploymentArtifact da) {

        final ResolvedArtifacts.ResolvedDeploymentArtifact result = new ResolvedArtifacts.ResolvedDeploymentArtifact();
        result.setName(da.getName());
        result.setType(da.getArtifactType());
        // assumption: there is artifactSpecificContent OR an artifactTemplateRef
        if (Objects.isNull(da.getArtifactRef())) {
            result.setArtifactSpecificContent(readArtifactSpecificContent(da));
            result.setReferences(Collections.emptyList());
            return result;
        }

        TArtifactTemplate template = (TArtifactTemplate) context.queryRepository(new ArtifactTemplateId(da.getArtifactRef()));
        final List<String> references = new ArrayList<>();
        for (final TArtifactReference artifactReference : Optional.ofNullable(template.getArtifactReferences()).orElse(Collections.emptyList())) {
            // if there is no include patterns, just add the reference
            if (artifactReference.getIncludeOrExclude().isEmpty()) {
                references.add(artifactReference.getReference());
                continue;
            }
            artifactReference.getIncludeOrExclude().stream()
                .filter(o -> o instanceof TArtifactReference.Include)
                .map(TArtifactReference.Include.class::cast)
                .forEach(includePattern -> references.add(artifactReference.getReference() + "/" + includePattern.getPattern()));
        }
        result.setReferences(references);

        return result;
    }

    private Document readArtifactSpecificContent(TDeploymentArtifact artifact) {
        final List<Element> listOfAnyElements = new ArrayList<>();
        for (final Object obj : artifact.getAny()) {
            if (obj instanceof Element) {
                listOfAnyElements.add((Element) obj);
            } else {
                LOG.error("There is content inside of the DeploymentArtifact [{}] which is not a processable DOM Element.", artifact.getName());
                return null;
            }
        }
        try {
            return this.elementsIntoDocument(listOfAnyElements, "DeploymentArtifactSpecificContent");
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Document elementsIntoDocument(final List<Element> elements, final String rootElementName) throws ParserConfigurationException {

                    DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        documentBuilderFactory.setNamespaceAware(true);
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        final Document returnDoc = documentBuilder.newDocument();

                final Element root = returnDoc.createElement(rootElementName);
                returnDoc.appendChild(root);

                for (final Element element : elements) {
                    final Node node = returnDoc.importNode(element, true);
                    if (node == null) {
                        // return null for easier checking of an error.
                        // if the return is not null, an empty or incomplete but valid
                        // document
                        // without
                        // content would be returned.
                        return null;
                    }
                    root.appendChild(node);
                }

                return returnDoc;
            }


}

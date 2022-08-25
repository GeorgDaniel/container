package org.opentosca.container.api.dto;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;

@XmlRootElement(name = "NodeTemplateInstanceResources")
public class NodeTemplateInstanceListDTO extends ResourceSupport {

    @JsonProperty
    @XmlElement(name = "NodeTemplateInstance")
    @XmlElementWrapper(name = "NodeTemplateInstances")
    private final List<NodeTemplateInstanceDTO> nodeTemplateInstances = new ArrayList<>();

    @ApiModelProperty(name = "node_template_instances")
    public List<NodeTemplateInstanceDTO> getNodeTemplateInstances() {
        return this.nodeTemplateInstances;
    }

    public void add(final NodeTemplateInstanceDTO... nodeTemplateInstances) {
        this.nodeTemplateInstances.addAll(Arrays.asList(nodeTemplateInstances));
    }
}

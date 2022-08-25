package org.opentosca.container.api.dto;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;

@XmlRootElement(name = "ServiceTemplateInstanceResources")
public class ServiceTemplateInstanceListDTO extends ResourceSupport {

    @JsonProperty
    @XmlElement(name = "ServiceTemplateInstance")
    @XmlElementWrapper(name = "ServiceTemplateInstances")
    private final List<ServiceTemplateInstanceDTO> serviceTemplateInstances = new ArrayList<>();

    @ApiModelProperty(name = "service_template_instances")
    public List<ServiceTemplateInstanceDTO> getServiceTemplateInstances() {
        return this.serviceTemplateInstances;
    }

    public void add(final ServiceTemplateInstanceDTO... serviceTemplateInstances) {
        this.serviceTemplateInstances.addAll(Arrays.asList(serviceTemplateInstances));
    }
}

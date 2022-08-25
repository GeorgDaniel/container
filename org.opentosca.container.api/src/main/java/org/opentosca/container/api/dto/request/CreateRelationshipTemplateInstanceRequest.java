package org.opentosca.container.api.dto.request;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@XmlRootElement(name = "CreateRelationshipTemplateInstanceRequest")
@XmlAccessorType(XmlAccessType.FIELD)
@JsonIgnoreProperties(ignoreUnknown = true)
public class CreateRelationshipTemplateInstanceRequest {

    @XmlAttribute(name = "service-instance-id")
    private Long serviceInstanceId;

    @XmlAttribute(name = "source-instance-id")
    private Long sourceNodeTemplateInstanceId;

    @XmlAttribute(name = "target-instance-id")
    private Long targetNodeTemplateInstanceId;

    public Long getServiceInstanceId() {
        return serviceInstanceId;
    }

    public void setServiceInstanceId(Long serviceInstanceId) {
        this.serviceInstanceId = serviceInstanceId;
    }

    public Long getSourceNodeTemplateInstanceId() {
        return this.sourceNodeTemplateInstanceId;
    }

    public void setSourceNodeTemplateInstanceId(final Long sourceNodeTemplateInstanceId) {
        this.sourceNodeTemplateInstanceId = sourceNodeTemplateInstanceId;
    }

    public Long getTargetNodeTemplateInstanceId() {
        return this.targetNodeTemplateInstanceId;
    }

    public void setTargetNodeTemplateInstanceId(final Long targetNodeTemplateInstanceId) {
        this.targetNodeTemplateInstanceId = targetNodeTemplateInstanceId;
    }
}

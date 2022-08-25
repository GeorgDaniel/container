package org.opentosca.container.api.dto.situations;

import java.util.Map;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.common.collect.Maps;
import org.opentosca.container.api.dto.ResourceSupport;
import org.opentosca.container.core.next.model.SituationTriggerInstance;

@XmlRootElement(name = "SituationTriggerInstance")
@JsonIgnoreProperties(ignoreUnknown = true)
public class SituationTriggerInstanceDTO extends ResourceSupport {

    @XmlAttribute(name = "id")
    private Long id;

    private Long situationTriggerId;

    private boolean isActive;

    private Map<String, String> outputParams;

    public Long getId() {
        return this.id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    @XmlElement(name = "SituationTriggerId")
    public Long getSituationTriggerId() {
        return this.situationTriggerId;
    }

    public void setSituationTriggerId(final Long situationTriggerId) {
        this.situationTriggerId = situationTriggerId;
    }

    @XmlElement(name = "Active")
    public boolean isActive() {
        return this.isActive;
    }

    public void setActive(final boolean isActive) {
        this.isActive = isActive;
    }

    @XmlElement(name = "Value")
    @XmlElementWrapper(name = "Output")
    public Map<String, String> getOutputParams() {
        return this.outputParams;
    }

    public void setOutputParams(final Map<String, String> outputParams) {
        this.outputParams = outputParams;
    }

    public static final class Converter {
        public static SituationTriggerInstanceDTO convert(final SituationTriggerInstance object) {
            final SituationTriggerInstanceDTO dto = new SituationTriggerInstanceDTO();

            dto.setId(object.getId());
            dto.setActive(object.isFinished());
            dto.setSituationTriggerId(object.getSituationTrigger().getId());
            final Map<String, String> outputParams = Maps.newHashMap();
            object.getOutputs().forEach(x -> outputParams.put(x.getName(), x.getValue()));
            dto.setOutputParams(outputParams);

            return dto;
        }
    }
}

package org.opentosca.container.api.dto.situations;

import java.util.Arrays;
import java.util.List;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Lists;
import org.opentosca.container.api.dto.ResourceSupport;

@XmlRootElement(name = "SituationTriggerResources")
public class SituationTriggerListDTO extends ResourceSupport {

    @JsonProperty
    @XmlElement(name = "SituationTrigger")
    @XmlElementWrapper(name = "SituationTriggers")
    private final List<SituationTriggerDTO> situationTriggers = Lists.newArrayList();

    public List<SituationTriggerDTO> getSituationTriggers() {
        return this.situationTriggers;
    }

    public void add(final SituationTriggerDTO... situations) {
        this.situationTriggers.addAll(Arrays.asList(situations));
    }
}

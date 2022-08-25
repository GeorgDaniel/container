package org.opentosca.container.api.dto.situations;

import java.util.Arrays;
import java.util.List;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Lists;
import org.opentosca.container.api.dto.ResourceSupport;

@XmlRootElement(name = "SituationsMonitors")
public class SituationsMonitorListDTO extends ResourceSupport {

    @JsonProperty
    @XmlElement(name = "SituationsMonitor")
    @XmlElementWrapper(name = "SituationsMonitors")
    private final List<SituationsMonitorDTO> situationsMonitors = Lists.newArrayList();

    public List<SituationsMonitorDTO> getSituations() {
        return this.situationsMonitors;
    }

    public void add(final SituationsMonitorDTO... situations) {
        this.situationsMonitors.addAll(Arrays.asList(situations));
    }
}

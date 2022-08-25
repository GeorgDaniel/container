package org.opentosca.container.api.dto.boundarydefinitions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.opentosca.container.api.dto.ResourceSupport;

@XmlRootElement(name = "InterfaceResources")
public class InterfaceListDTO extends ResourceSupport {

    @JsonProperty
    @XmlElement(name = "Interface")
    @XmlElementWrapper(name = "Interfaces")
    private final List<InterfaceDTO> interfaces = new ArrayList<>();

    public List<InterfaceDTO> getInterfaces() {
        return this.interfaces;
    }

    public void add(final InterfaceDTO... interfaces) {
        this.interfaces.addAll(Arrays.asList(interfaces));
    }
}

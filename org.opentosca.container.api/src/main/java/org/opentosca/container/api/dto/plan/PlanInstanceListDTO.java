package org.opentosca.container.api.dto.plan;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import org.opentosca.container.api.dto.ResourceSupport;

@XmlRootElement(name = "PlanInstanceResources")
public class PlanInstanceListDTO extends ResourceSupport {

    @JsonProperty
    @XmlElement(name = "PlanInstance")
    @XmlElementWrapper(name = "PlanInstances")
    private final List<PlanInstanceDTO> planInstances = new ArrayList<>();

    @ApiModelProperty(name = "plan_instances")
    public List<PlanInstanceDTO> getPlanInstances() {
        return this.planInstances;
    }

    public void add(final PlanInstanceDTO... planInstances) {
        this.planInstances.addAll(Arrays.asList(planInstances));
    }

    public void add(final Collection<PlanInstanceDTO> planInstances) {
        this.planInstances.addAll(planInstances);
    }
}

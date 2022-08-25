package org.opentosca.container.api.dto.plan;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;

import org.opentosca.container.api.dto.PropertyDTO;
import org.opentosca.container.core.next.model.PlanInstanceInput;

@XmlRootElement(name = "PlanInstanceInput")
@XmlAccessorType(XmlAccessType.FIELD)
public class PlanInstanceInputDTO extends PropertyDTO {
    public static class Converter {
        public static PlanInstanceInputDTO convert(final PlanInstanceInput object) {
            final PlanInstanceInputDTO result = new PlanInstanceInputDTO();
            PropertyDTO.Converter.fillValues(object, result);
            return result;
        }
    }
}

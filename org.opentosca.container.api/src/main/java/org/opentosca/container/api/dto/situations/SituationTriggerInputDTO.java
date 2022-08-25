package org.opentosca.container.api.dto.situations;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;

import org.opentosca.container.api.dto.PropertyDTO;
import org.opentosca.container.core.next.model.Property;

@XmlRootElement(name = "InputParameter")
@XmlAccessorType(XmlAccessType.FIELD)
public class SituationTriggerInputDTO extends PropertyDTO {
    public static class Converter {
        public static SituationTriggerInputDTO convert(final Property object) {
            final SituationTriggerInputDTO result = new SituationTriggerInputDTO();
            PropertyDTO.Converter.fillValues(object, result);

            return result;
        }
    }
}

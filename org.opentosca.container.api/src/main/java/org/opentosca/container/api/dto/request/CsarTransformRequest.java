package org.opentosca.container.api.dto.request;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@XmlRootElement(name = "CsarTransformRequest")
@XmlAccessorType(XmlAccessType.FIELD)
@JsonIgnoreProperties(ignoreUnknown = true)
public class CsarTransformRequest {

    @XmlElement(name = "SourceCsarName")
    private String sourceCsarName;

    @XmlElement(name = "TargetCsarName")
    private String targetCsarName;

    public String getSourceCsarName() {
        return this.sourceCsarName;
    }

    public void setSourceCsarName(final String sourceCsarName) {
        this.sourceCsarName = sourceCsarName;
    }

    public String getTargetCsarName() {
        return this.targetCsarName;
    }

    public void setTargetCsarName(final String targetCsarName) {
        this.targetCsarName = targetCsarName;
    }
}

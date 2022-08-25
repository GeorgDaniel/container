package org.opentosca.container.api.dto.request;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlValue;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@XmlRootElement(name = "log")
@XmlAccessorType(XmlAccessType.FIELD)
@JsonIgnoreProperties(ignoreUnknown = true)
public class CreatePlanInstanceLogEntryRequest {

    @XmlValue
    private String logEntry;

    public String getLogEntry() {
        return this.logEntry;
    }

    public void setLogEntry(final String logEntry) {
        this.logEntry = logEntry;
    }
}

// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference
// Implementation, v2.2.4-2
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a>
// Any modifications to this file will be lost upon recompilation of the source schema.
// Generated on: 2018.07.05 at 09:07:58 PM CEST

package org.opentosca.bus.management.service.impl.collaboration.model;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;

/**
 * <p>
 * The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="InstanceDataMatchingRequest">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="NodeType" type="{http://www.w3.org/2001/XMLSchema}QName"/>
 *         &lt;element name="Properties" type="{http://collaboration.org/schema}KeyValueMap"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "InstanceDataMatchingRequest", propOrder = {"nodeType", "properties"})
public class InstanceDataMatchingRequest {

    @XmlElement(name = "NodeType", required = true)
    protected QName nodeType;
    @XmlElement(name = "Properties", required = true)
    protected KeyValueMap properties;

    public InstanceDataMatchingRequest() {
    }

    public InstanceDataMatchingRequest(final QName nodeType, final KeyValueMap properties) {
        this.nodeType = nodeType;
        this.properties = properties;
    }

    /**
     * Gets the value of the nodeType property.
     *
     * @return possible object is {@link QName }
     */
    public QName getNodeType() {
        return this.nodeType;
    }

    /**
     * Sets the value of the nodeType property.
     *
     * @param value allowed object is {@link QName }
     */
    public void setNodeType(final QName value) {
        this.nodeType = value;
    }

    /**
     * Gets the value of the properties property.
     *
     * @return possible object is {@link KeyValueMap }
     */
    public KeyValueMap getProperties() {
        return this.properties;
    }

    /**
     * Sets the value of the properties property.
     *
     * @param value allowed object is {@link KeyValueMap }
     */
    public void setProperties(final KeyValueMap value) {
        this.properties = value;
    }
}

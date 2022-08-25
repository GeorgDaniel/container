//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference
// Implementation, v2.2.4-2
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a>
// Any modifications to this file will be lost upon recompilation of the source schema.
// Generated on: 2018.07.05 at 09:07:58 PM CEST
//

package org.opentosca.bus.management.service.impl.collaboration.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;

/**
 * <p>
 * The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="KeyValueMap">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="KeyValuePair" type="{http://collaboration.org/schema}KeyValueType" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "KeyValueMap", propOrder = {"keyValuePair"})
public class KeyValueMap {

    @XmlElement(name = "KeyValuePair", required = true)
    protected List<KeyValueType> keyValuePair;

    /**
     * Gets the value of the KeyValuePair property.
     *
     * <p>
     * This accessor method returns a reference to the live list, not a snapshot. Therefore any modification you make to
     * the returned list will be present inside the JAXB object. This is why there is not a <CODE>set</CODE> method for
     * the keyValuePair property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     *
     * <pre>
     * getKeyValuePair().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list {@link KeyValueType }
     */
    public List<KeyValueType> getKeyValuePair() {
        if (this.keyValuePair == null) {
            this.keyValuePair = new ArrayList<>();
        }
        return this.keyValuePair;
    }
}

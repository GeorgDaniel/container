//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference
// Implementation, vJAXB 2.1.10 in JDK 6
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a>
// Any modifications to this file will be lost upon recompilation of the source schema.
// Generated on: 2012.05.16 at 05:29:02 PM MESZ
//

package org.apache.ode.schemas.dd._2007._03;

import java.util.ArrayList;
import java.util.List;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java class for tSchedule complex type.
 *
 * <p>
 * The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="tSchedule">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="cleanup" type="{http://www.apache.org/ode/schemas/dd/2007/03}tCleanup" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="when" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tSchedule", propOrder = {"cleanup"})
public class TSchedule {

    protected List<TCleanup> cleanup;
    @XmlAttribute(required = true)
    protected String when;

    /**
     * Gets the value of the cleanup property.
     *
     * <p>
     * This accessor method returns a reference to the live list, not a snapshot. Therefore any modification you make to
     * the returned list will be present inside the JAXB object. This is why there is not a <CODE>set</CODE> method for
     * the cleanup property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     *
     * <pre>
     * getCleanup().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list {@link TCleanup }
     */
    public List<TCleanup> getCleanup() {
        if (this.cleanup == null) {
            this.cleanup = new ArrayList<>();
        }
        return this.cleanup;
    }

    /**
     * Gets the value of the when property.
     *
     * @return possible object is {@link String }
     */
    public String getWhen() {
        return this.when;
    }

    /**
     * Sets the value of the when property.
     *
     * @param value allowed object is {@link String }
     */
    public void setWhen(final String value) {
        this.when = value;
    }
}

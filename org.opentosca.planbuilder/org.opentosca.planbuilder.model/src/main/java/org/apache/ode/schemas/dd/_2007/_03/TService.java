//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference
// Implementation, vJAXB 2.1.10 in JDK 6
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a>
// Any modifications to this file will be lost upon recompilation of the source schema.
// Generated on: 2013.05.27 at 03:40:55 PM CEST
//

package org.apache.ode.schemas.dd._2007._03;

import java.util.ArrayList;
import java.util.List;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAnyElement;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;
import jakarta.xml.bind.annotation.adapters.CollapsedStringAdapter;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.namespace.QName;

import org.w3c.dom.Element;

/**
 * <p>
 * Java class for tService complex type.
 *
 * <p>
 * The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="tService">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;any processContents='lax' namespace='##other' maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="name" use="required" type="{http://www.w3.org/2001/XMLSchema}QName" />
 *       &lt;attribute name="port" use="required" type="{http://www.w3.org/2001/XMLSchema}NCName" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tService", propOrder = {"any"})
public class TService {

    @XmlAnyElement(lax = true)
    protected List<Object> any;
    @XmlAttribute(required = true)
    protected QName name;
    @XmlAttribute(required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "NCName")
    protected String port;

    /**
     * Gets the value of the any property.
     *
     * <p>
     * This accessor method returns a reference to the live list, not a snapshot. Therefore any modification you make to
     * the returned list will be present inside the JAXB object. This is why there is not a <CODE>set</CODE> method for
     * the any property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     *
     * <pre>
     * getAny().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list {@link Object } {@link Element }
     */
    public List<Object> getAny() {
        if (this.any == null) {
            this.any = new ArrayList<>();
        }
        return this.any;
    }

    /**
     * Gets the value of the name property.
     *
     * @return possible object is {@link QName }
     */
    public QName getName() {
        return this.name;
    }

    /**
     * Sets the value of the name property.
     *
     * @param value allowed object is {@link QName }
     */
    public void setName(final QName value) {
        this.name = value;
    }

    /**
     * Gets the value of the port property.
     *
     * @return possible object is {@link String }
     */
    public String getPort() {
        return this.port;
    }

    /**
     * Sets the value of the port property.
     *
     * @param value allowed object is {@link String }
     */
    public void setPort(final String value) {
        this.port = value;
    }
}

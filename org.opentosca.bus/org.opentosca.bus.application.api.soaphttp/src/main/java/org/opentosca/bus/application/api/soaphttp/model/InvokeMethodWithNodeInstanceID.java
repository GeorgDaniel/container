//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference
// Implementation, v2.2.4-2
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a>
// Any modifications to this file will be lost upon recompilation of the source schema.
// Generated on: 2016.02.25 at 04:54:56 PM CET
//

package org.opentosca.bus.application.api.soaphttp.model;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java class for invokeMethodWithNodeInstanceID complex type.
 *
 * <p>
 * The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="invokeMethodWithNodeInstanceID">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="interface" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="operation" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="nodeInstanceID" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="Params" type="{http://opentosca.org/appinvoker/}ParamsMap" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "invokeMethodWithNodeInstanceID", propOrder = {"_interface", "operation", "nodeInstanceID", "params"})
public class InvokeMethodWithNodeInstanceID {

    @XmlElement(name = "interface", required = true)
    protected String _interface;
    @XmlElement(required = true)
    protected String operation;
    protected int nodeInstanceID;
    @XmlElement(name = "Params")
    protected ParamsMap params;

    /**
     * Gets the value of the interface property.
     *
     * @return possible object is {@link String }
     */
    public String getInterface() {
        return this._interface;
    }

    /**
     * Sets the value of the interface property.
     *
     * @param value allowed object is {@link String }
     */
    public void setInterface(final String value) {
        this._interface = value;
    }

    /**
     * Gets the value of the operation property.
     *
     * @return possible object is {@link String }
     */
    public String getOperation() {
        return this.operation;
    }

    /**
     * Sets the value of the operation property.
     *
     * @param value allowed object is {@link String }
     */
    public void setOperation(final String value) {
        this.operation = value;
    }

    /**
     * Gets the value of the nodeInstanceID property.
     */
    public int getNodeInstanceID() {
        return this.nodeInstanceID;
    }

    /**
     * Sets the value of the nodeInstanceID property.
     */
    public void setNodeInstanceID(final int value) {
        this.nodeInstanceID = value;
    }

    /**
     * Gets the value of the params property.
     *
     * @return possible object is {@link ParamsMap }
     */
    public ParamsMap getParams() {
        return this.params;
    }

    /**
     * Sets the value of the params property.
     *
     * @param value allowed object is {@link ParamsMap }
     */
    public void setParams(final ParamsMap value) {
        this.params = value;
    }
}

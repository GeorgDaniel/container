//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference
// Implementation, vJAXB 2.1.10 in JDK 6
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a>
// Any modifications to this file will be lost upon recompilation of the source schema.
// Generated on: 2014.11.11 at 08:37:59 AM CET
//

package org.eclipse.winery.model.selfservice;

import javax.xml.bind.annotation.XmlRegistry;

/**
 * This object contains factory methods for each Java content interface and Java element interface generated in the
 * org.eclipse.winery.model.selfservice package.
 * <p>
 * An ObjectFactory allows you to programatically construct new instances of the Java representation for XML content.
 * The Java representation of XML content can consist of schema derived interfaces and classes representing the binding
 * of schema type definitions, element declarations and model groups. Factory methods for each of these are provided in
 * this class.
 */
@XmlRegistry
public class ObjectFactory {

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package:
     * org.eclipse.winery.model.selfservice
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link Application }
     */
    public Application createApplication() {
        return new Application();
    }

    /**
     * Create an instance of {@link ApplicationOption }
     */
    public ApplicationOption createApplicationOption() {
        return new ApplicationOption();
    }

    /**
     * Create an instance of {@link Application.Options }
     */
    public Application.Options createApplicationOptions() {
        return new Application.Options();
    }
}
//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.7-b41 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2015.02.10 at 09:37:12 PM CET 
//


package com.enonic.wem.export.internal.xml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for indexConfig complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="indexConfig">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="decideByType" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="enabled" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="nGram" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="fulltext" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="includeInAllText" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "indexConfig", propOrder = {
    "decideByType",
    "enabled",
    "nGram",
    "fulltext",
    "includeInAllText"
})
public class XmlIndexConfig {

    protected boolean decideByType;
    protected boolean enabled;
    protected boolean nGram;
    protected boolean fulltext;
    protected boolean includeInAllText;

    /**
     * Gets the value of the decideByType property.
     * 
     */
    public boolean isDecideByType() {
        return decideByType;
    }

    /**
     * Sets the value of the decideByType property.
     * 
     */
    public void setDecideByType(boolean value) {
        this.decideByType = value;
    }

    /**
     * Gets the value of the enabled property.
     * 
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Sets the value of the enabled property.
     * 
     */
    public void setEnabled(boolean value) {
        this.enabled = value;
    }

    /**
     * Gets the value of the nGram property.
     * 
     */
    public boolean isNGram() {
        return nGram;
    }

    /**
     * Sets the value of the nGram property.
     * 
     */
    public void setNGram(boolean value) {
        this.nGram = value;
    }

    /**
     * Gets the value of the fulltext property.
     * 
     */
    public boolean isFulltext() {
        return fulltext;
    }

    /**
     * Sets the value of the fulltext property.
     * 
     */
    public void setFulltext(boolean value) {
        this.fulltext = value;
    }

    /**
     * Gets the value of the includeInAllText property.
     * 
     */
    public boolean isIncludeInAllText() {
        return includeInAllText;
    }

    /**
     * Sets the value of the includeInAllText property.
     * 
     */
    public void setIncludeInAllText(boolean value) {
        this.includeInAllText = value;
    }

}
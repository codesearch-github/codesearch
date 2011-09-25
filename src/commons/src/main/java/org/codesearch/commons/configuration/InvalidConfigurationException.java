/**
 * 
 */
package org.codesearch.commons.configuration;

/**
 * Exception that is thrown by the configuration reader if the configuration is invalid.
 * @author Samuel Kogler
 */
public class InvalidConfigurationException extends Exception {

    /** . */
    private static final long serialVersionUID = 9167741349667856037L;

    /**
     * 
     */
    public InvalidConfigurationException() {
        super();
    }

    /**
     * @param message
     */
    public InvalidConfigurationException(String message) {
        super(message);
    }

    

}

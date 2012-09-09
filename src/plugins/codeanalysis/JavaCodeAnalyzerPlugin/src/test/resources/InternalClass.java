/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author david
 */
public class InternalClass {
    public InternalClass(){
        InnerClass innerClass = new InternalClass.InnerClass();
    }
    
    class InnerClass{
        
    }
}

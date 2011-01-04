/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.codesearch.indexer.tasks;

/**
 *
 * @author David Froehlich
 */
@Deprecated
public class Test {
    private int i;

    public void bar(){
        int test = 3;
        foo(test);
    }

    public void foo(String asdf){
        if(true){
            {
                int i = 0;
                i = 2;
            }
            i = 3;
        }
    }

    public void foo(int asdf){
        
    }
}

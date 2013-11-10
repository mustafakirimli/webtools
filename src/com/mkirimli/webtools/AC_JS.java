package com.mkirimli.webtools;

import android.util.Log;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 *
 * @author mustafa
 */
final public class AC_JS {
    // Active auto complete steps
    private ArrayList<AC_BASE> stack;
    
    private HashMap<String, AC_BASE> ac_base;

    public void setAcObjects(){
        ac_base.put("var", new AC_BASE("var <x> = <y>;<>"));
        ac_base.put("new", new AC_BASE("new <Cls>(<>);<>"));
        ac_base.put("if", new AC_BASE("if(<state>){\n  <>\n}<>"));
        ac_base.put("func", new AC_BASE("function <fncName>(<args>){\n  <>\n}<>"));
        ac_base.put("for", new AC_BASE("for(var <x> in <y>){\n  <>\n}<>"));
        ac_base.put("while", new AC_BASE("while(<cond>){\n  <>\n}<>"));
        ac_base.put("try", new AC_BASE("try{\n  <>\n}catch(<e>){\n  <>\n}<>"));
        ac_base.put("paran", new AC_BASE("(<>)<>"));
        ac_base.put("brack", new AC_BASE("[<>]<>"));
        ac_base.put("curly", new AC_BASE("{<>}<>"));
        ac_base.put("jshort", new AC_BASE("$<>"));
        ac_base.put("squota", new AC_BASE("'<>'<>"));
        ac_base.put("dquota", new AC_BASE("\"<>\"<>"));
        ac_base.put("equals", new AC_BASE(" == <>"));
    }
    
    public AC_JS() {
        stack = new ArrayList<AC_BASE>();
        
        ac_base = new HashMap<String, AC_BASE>();
        
        // Set all ac object to the stack
        setAcObjects();
        
        // TODO: remove
        //debugPatterns();
    }
    
    public void addStack(String ac){
        if(ac_base.containsKey(ac)){
            stack.add(ac_base.get(ac));
        }
    }
    
    public AC_BASE getAcObject(String ac){
        return ac_base.get(ac);
    }
    
    public void clearAC(){
        for (Iterator<AC_BASE> it = stack.iterator(); it.hasNext();) {
            AC_BASE ac_base1 = it.next();
            ac_base1.reset();
            
        }
        stack.clear();
    }
    
    public String enterEvent(){
        AC_BASE ref;
        if(stack.size() > 0){ // if any ac objects have in stack
            // Get active auto complete object
            ref = stack.get(stack.size() - 1);
            
            // Play to auto complete objects step
            String ret = ref.play();
            
            if (false == ref.hasNext()) {
                //If auto complete object steps is last step, remove from stack
                ref.reset();
                stack.remove(stack.size() - 1);
            }
            return ret;
        }else{ // there are not any stack or steps, prevent default event
            return null;
        }
    }
    
    public void debugPatterns(){
        Log.v("START", "================DEBUG PATTERN================");
        for (Map.Entry<String, AC_BASE> entry : ac_base.entrySet()) {
            String key = entry.getKey();
            AC_BASE value = entry.getValue();
            Log.v(key, ":::::pattern::::::\n" + value.pattern);
            Log.v(key, ":::::pattern tex::\n" + value.pattern_text);
            
            for (Iterator<String> it = value.steps.iterator(); it.hasNext();) {
                String steps = it.next();
                Log.v("steps", steps);
            }
        }
        Log.v("END", "================DEBUG PATTERN================");
    }
}
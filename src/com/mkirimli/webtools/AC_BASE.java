package com.mkirimli.webtools;

import java.util.ArrayList;

/**
 *
 * @author mkirimli
 */
final public class AC_BASE {

    // Auto complete pattern. Ex: "function <fncName>(<args>){\n  <>\n"
    protected String pattern;
    
    // Auto compete pattern string
    protected String pattern_text;
    
    // Auto complete steps <start, end>
    ArrayList<String> steps;
    
    // Auto complete current step
    protected int step = 0;

    public AC_BASE(String pattern) {
        this.pattern = pattern;
        
        // initialize steps
        steps = new ArrayList<String>();
        
        // Calculate pattern
        calculate();
        
        // Recalculate for auto comlete
        reCalculate();
        
        // Replace pattern tags
        replaceDelim();
    }
    
    /**
     * 
     * SetSelection example:
     * 12345, setSelection(3,3) 123|45
     * 12345, setSelection(3,4) 123|4|5
     */
    protected void calculate() {
        step = 0;
        int start = 0, end = 0;
        byte[] bs = pattern.getBytes();
        for (int i = 0, phol = 0; i < bs.length; i++) {
            byte b = bs[i];
            if (b == 10) {
                //phol++;
            }
            if (b == 60) { //'<'
                phol++;
                start = (i + 1) - phol;
            } else if (b == 62) { // '62'
                phol++;
                end = (i + 1) - phol;
                // Append to pattern
                steps.add(start + "," + end);
            }
        }
        // Ad end flat to steps
       // steps.add( (end + 1) + "," + (end + 1));
    }
    
    public void reCalculate(){
        for (int i = steps.size() - 1; i > 0; i--) {
            String prev[] = steps.get(i - 1).split(",");
            String curr[] = steps.get(i).split(",");

            steps.set(i, (Integer.parseInt(curr[0]) - Integer.parseInt(prev[1])) 
                        +"," 
                        +(Integer.parseInt(curr[1]) - Integer.parseInt(prev[1]))
                     );
        }
    }
    
    public void reset(){
        step = 0;
    }

    public void replaceDelim(){
        pattern_text = pattern.replace("<>", "")
                              .replace(">",  "")
                              .replace("<",  "");
    } 
    
    public String play() {
        if (step <= steps.size() - 1) {
            String ret = (String) steps.get(step);
            step++;
            return ret;
        }else{
            return null;
        }
    }

    public boolean hasNext() {
        return step < steps.size();
    }
}

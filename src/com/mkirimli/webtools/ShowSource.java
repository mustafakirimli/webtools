package com.mkirimli.webtools;

import android.widget.EditText;
import android.widget.TextView;

/**
 *
 *  Source tabının içeriği
 * 
 * @author mustafa
 */
public class ShowSource {
    // PWT nesnesine erişir
    private PWT pwt;
    
    // Source tabinde bulunan edittext alani
    private EditText htmlsource;
    
    // Tabin daha önce görünüp görünmediğini tutar
    private boolean isShow = false;
    
    // Tab 'in hazır olup olmadığı (data bekliyor olabilir)
    private boolean isReady = false;

    /**
     * Consructor
     * @param pwt PWT nesnesi 
     */
    public ShowSource(PWT pwt) {
        this.pwt = pwt;
    }

    /**
     * 
     * @return 
     */
    public EditText getHtmlsource() {
        return htmlsource;
    }

    /**
     * 
     * @param htmlsource 
     */
    public void setHtmlsource(EditText htmlsource) {
        this.htmlsource = (EditText) htmlsource;
    }
    
    /**
     * Task lerden gelen veriyi set eder
     * @param result 
     */
    public void ready(String result){
        pwt.setSource(result);
        isReady = true;
        if(isShow){ // tab e daha önce girildiyse task bitince veriyi güncelle
            display();
        }
    }

    /**
     * Tab 'e içeriğin (datanın) yüklenmesini sağlar
     */
    public void display() {
        htmlsource.setText((CharSequence) pwt.getSource(), TextView.BufferType.EDITABLE);
    }
    
    /**
     * Tab 'de bulunan dataları temizler
     */
    public void clear(){
        htmlsource.setText((CharSequence) "", TextView.BufferType.EDITABLE);
    }
    
    /**
     * Tab değiştiğinde çalışır
     * @param tab id
     */
    public void tabChanged(String tabId){
        // içerik yüklenmemişse yükle
        if("source".equals(tabId) && isShow == false){
            if( isReady){
                display();
            }
            isShow = true;
        }else if(false == tabId.equals("source")){
            // Kaynak kodunun değişip değişmediğine bak
            String source = htmlsource.getText().toString();
            if (source.length() > 0 && 
                                  pwt.getSource().length() != source.length()) {
                pwt.getShowMeta().ready(source);
                pwt.getShowWeb().ready();
                this.ready(source);
            }
        }
    }
}
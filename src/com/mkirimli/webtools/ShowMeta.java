package com.mkirimli.webtools;

import android.widget.ArrayAdapter;
import android.widget.ListView;
import java.util.Map;

/**
 * 
 * Meta tabının içeriği
 * 
 * @author mkirimli
 */
public class ShowMeta {

    // PWT nesnesine erişir
    private PWT pwt;
    
    // Meta tabinde bulunan listview alani
    private ListView metaList;
    
    // Event based XML parser
    private CustomSAXParser customSAXParser;
    
    // ListView adapter
    ArrayAdapter<String> metaAdapter;
    
    // Tabin daha önce görünüp görünmediğini tutar
    private boolean isShow = false;
    
    // Tab 'in hazır olup olmadığı (data bekliyor olabilir)
    private boolean isReady = false;

    /**
     * Consructor
     * @param pwt PWT nesnesi 
     */
    public ShowMeta(PWT pwt) {
        this.pwt = pwt;
        this.customSAXParser = new CustomSAXParser(pwt);
    }

    public ListView getMetaDetails() {
        return metaList;
    }

    public void setMetaDetails(ListView metaList) {
        this.metaList = metaList;
        metaAdapter = new ArrayAdapter<String>(pwt.getApplicationContext(), R.layout.show_meta_row);
        this.metaList.setAdapter(metaAdapter);
    }

    /**
     * Task lerden gelen veriyi set eder
     * @param result 
     */
    public void ready(String result){
        customSAXParser.parseDocument(result);
        isReady = true;
        if(isShow){ // tab e daha önce girildiyse task bitince veriyi güncelle
            display();
        }
    }

    public void display() {
        if( false == pwt.getSource().equals("") ){
            String title = pwt.getMetas().containsKey("title") ? 
                           pwt.getMetas().get("title") : " - ";
            String desc =  pwt.getMetas().containsKey("description") ? 
                           pwt.getMetas().get("description") : " - ";
            String keyw =  pwt.getMetas().containsKey("keywords") ? 
                           pwt.getMetas().get("keywords") : " - ";
            metaAdapter.add("Title : " + title);
            metaAdapter.add("Description : " + desc);
            metaAdapter.add("Keywords : " + keyw);
        }
        for (Map.Entry<String, String> item : pwt.getMetas().entrySet()) {
            String key = item.getKey();
            String value = item.getValue();
            if( key != null && 
                false == (key.equalsIgnoreCase("title") || 
                          key.equalsIgnoreCase("description") || 
                          key.equalsIgnoreCase("keywords")) ){
                metaAdapter.add(key + " : " + value);
            }
        }
    }
    
    public void clear(){
        metaAdapter.clear();
        metaAdapter.notifyDataSetChanged();
    }
    
    /**
     * Tab değiştiğinde çalışır
     * @param tab id
     */
    public void tabChanged(String tabId){
        // içerik yüklenmemişse yükle
        if(tabId.equals("meta") && isShow == false && isReady){
            display();
            isShow = true;
        }
    }
}

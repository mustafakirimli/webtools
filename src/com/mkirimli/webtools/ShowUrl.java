package com.mkirimli.webtools;

import android.widget.ArrayAdapter;
import android.widget.ListView;
import java.util.Map;

/**
 *
 * URL tabının içeriği
 * 
 * @author mustafa
 */
public class ShowUrl {

    // PWT nesnesine erişir
    private PWT pwt;
    
    // URL tabi içinde bulunan listview
    ListView urlList;
    
    // ListView adapter
    ArrayAdapter<String> urlAdapter;
    
    // Tabin daha önce görünüp görünmediğini tutar
    private boolean isShow = false;
    
    // Tab 'in hazır olup olmadığı (data bekliyor olabilir)
    private boolean isReady = false;

    /**
     * Consructor
     * @param pwt PWT nesnesi 
     */
    public ShowUrl(PWT pwt) {
        this.pwt = pwt;
    }

    /**
     * 
     * @return 
     */
    public ListView getUrlDetails() {
        return urlList;
    }

    /**
     * 
     * @param urlList 
     */
    public void setUrlDetails(ListView urlList) {
        this.urlList = urlList;
        urlAdapter = new ArrayAdapter<String>(pwt.getApplicationContext(), R.layout.show_url_row);
        this.urlList.setAdapter(urlAdapter);
    }

    /**
     * Task lerden gelen veriyi set eder
     * @param result 
     */
    public void ready(String result){
        isReady = true;
        if(isShow){ // tab e daha önce girildiyse task bitince veriyi güncelle
            display();
        }
    }
    
    public void display() {
        for (Map.Entry<String, Integer> item : pwt.getUrlRedirections().entrySet()) {
            String key = item.getKey();
            Integer value = item.getValue();
            urlAdapter.add(key + "(" + value + ")");
        }
    }
    
    public void clear(){
        urlAdapter.clear();
        urlAdapter.notifyDataSetChanged();
    }
    
    /**
     * Tab değiştiğinde çalışır
     * @param tab id
     */
    public void tabChanged(String tabId){
        // içerik yüklenmemişse yükle
        if(tabId.equals("url") && isShow == false && isReady){
            display();
            isShow = true;
        }
    }
}

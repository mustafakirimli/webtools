package com.mkirimli.webtools;

import android.widget.ArrayAdapter;
import android.widget.ListView;
import java.util.Map;

/**
 *
 * Header tabının içeriği
 * 
 * @author mkirimli
 */
public class ShowHeader{

    // PWT nesnesine erişir
    private PWT pwt;
    
    // Header tabinde bulunan listview alani
    private ListView headerList;
    
    // ListView Adapter
    ArrayAdapter<String> headerAdapter;

    // Tabin daha önce görünüp görünmediğini tutar
    private boolean isShow = false;
    
    // Tab 'in hazır olup olmadığı (data bekliyor olabilir)
    private boolean isReady = false;
    
    /**
     * Consructor
     * @param pwt PWT nesnesi 
     */
    public ShowHeader(PWT pwt) {
        this.pwt = pwt;
    }

    /**
     * 
     * @return 
     */
    public ListView getHeaderDetails() {
        return headerList;
    }

    /**
     * 
     * @param headerList 
     */
    public void setHeaderDetails(ListView headerList) {
        this.headerList = headerList;
        headerAdapter = new ArrayAdapter<String>(pwt.getApplicationContext(), R.layout.show_header_row);
        this.headerList.setAdapter(headerAdapter);
    }
    
    /**
     * 
     * @param headerName
     * @param headerValue 
     */
    public void ready(String headerName, String headerValue){
        isReady = true;
        if (headerName.equals("Content-Type")) {
            if (headerValue.contains(";")) {
                pwt.addContentType("header", headerValue.split(";")[0]);
                if (headerValue.contains("=")) {
                    pwt.addEncoding("header", headerValue.split(";")[1].split("=")[1]);
                }
            }
        }
        pwt.addResponseHeader(headerName, headerValue);
        if(isShow){ // tab e daha önce girildiyse task bitince veriyi güncelle
            display();
        }
    }
    
    public void display() {
        headerAdapter.clear();
        for (Map.Entry<String, String> item : pwt.getResponseHeaders().entrySet()) {
            String key = item.getKey();
            String value = item.getValue();
            headerAdapter.add(key + " : " + value);
        }
    }
     
    public void clear(){
        this.headerAdapter.clear();
        this.headerAdapter.notifyDataSetChanged();
    }
    
    /**
     * Tab değiştiğinde çalışır
     * @param tab id
     */
    public void tabChanged(String tabId){
        // içerik yüklenmemişse yükle
        if(tabId.equals("header") && isShow == false && isReady){
            display();
            isShow = true;
        }
    }
}
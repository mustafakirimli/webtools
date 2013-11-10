package com.mkirimli.webtools;

import android.util.Log;
import android.view.View.OnClickListener;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ListView;

/**
 *
 *  Console tabının içeriği
 * 
 * @author mustafa
 */
public class ShowConsole{
    // PWT nesnesine erişir
    private PWT pwt;
    
    // Console tabinde bulunan listview alani
    private ListView commandList;
    
    // ListView adapter
    ArrayAdapter<String> consoleAdapter;
    
    // Console command clear button
    private Button clearConsole;
    
    // Console command (js) auto complete
    private AutoCompleteTextView command;
    
    // Tabin daha önce görünüp görünmediğini tutar
    private boolean isShow = false;
    
    // Tab 'in hazır olup olmadığı (data bekliyor olabilir)
    private boolean isReady = false;

    // JS Auto Complete Object
    private AC_JS ac_js;
    
    /**
     * Consructor
     * @param pwt PWT nesnesi 
     */
    public ShowConsole(PWT pwt) {
        this.pwt = pwt;
        ac_js = new AC_JS();
    }
    
    public boolean enterEvent(String ac){
        String pos = ac_js.enterEvent();
        int curPos   = command.getSelectionEnd();
        if (pos != null) {
            if(ac != null){
                    command.getText().replace(command.getSelectionStart(), 
                                              curPos,
                                              "");
                    curPos   = command.getSelectionEnd();
                    command.getText().insert(curPos, 
                                     ac_js.getAcObject(ac).pattern_text);
            }
            String poss[] = pos.split(",");
            command.setSelection(Integer.parseInt(poss[0]) + curPos, 
                                 Integer.parseInt(poss[1]) + curPos);
            return true;
        }
        return false;
    }
    
    public void runCommand(){
        String commandText = command.getText().toString();
        showConsole("> " + commandText);
        pwt.getShowWeb().consoleCommand(commandText);
        command.setText("");
        ac_js.clearAC();
    }
    
    public void setObjects(ListView commandList, 
                           Button ClearLine,
                           Button runCommand,
                           Button clearConsole,
                           final AutoCompleteTextView command,
                           Button defvar,
                           Button newobj,
                           Button ifstate,
                           Button fnc,
                           Button forloop,
                           Button whileloop,
                           Button trycatch,
                           Button parantez,
                           Button suslu,
                           Button koseli,
                           Button jshort,
                           Button cifttirnak,
                           Button tektirnak,
                           Button esittir,
                           Button newline) {
        this.commandList = commandList;
        this.clearConsole = clearConsole;
        this.command = command;
        
        newline.setOnClickListener(new OnClickListener() {

            public void onClick(View arg0) {
                command.getText().insert(command.getSelectionEnd(), "\n");
            }
        });
        
        ClearLine.setOnClickListener(new OnClickListener() {

            public void onClick(View arg0) {
                command.setText("");
                ac_js.clearAC();
            }
        });
        
        runCommand.setOnClickListener(new OnClickListener() {

            public void onClick(View arg0) {
                // Perform action on key press
                runCommand();
            }
        });
        
        defvar.setOnClickListener(new OnClickListener() {

            public void onClick(View arg0) {
                ac_js.addStack("var");
                enterEvent("var");
            }
        });
        
        newobj.setOnClickListener(new OnClickListener() {

            public void onClick(View arg0) {
                ac_js.addStack("new");
                enterEvent("new");
            }
        });
        
        ifstate.setOnClickListener(new OnClickListener() {

            public void onClick(View arg0) {
                ac_js.addStack("if");
                enterEvent("if");
            }
        });
        
        // Kısayol butonları
        parantez.setOnClickListener(new OnClickListener() {

            public void onClick(View arg0) {
                ac_js.addStack("paran");
                enterEvent("paran");
            }
        });
        suslu.setOnClickListener(new OnClickListener() {

            public void onClick(View arg0) {
                ac_js.addStack("curly");
                enterEvent("curly");
            }
        });
        koseli.setOnClickListener(new OnClickListener() {

            public void onClick(View arg0) {
                ac_js.addStack("brack");
                enterEvent("brack");
            }
        });
        fnc.setOnClickListener(new OnClickListener() {

            public void onClick(View arg0) {
                ac_js.addStack("func");
                enterEvent("func");
            }
        });
        forloop.setOnClickListener(new OnClickListener() {

            public void onClick(View arg0) {
                ac_js.addStack("for");
                enterEvent("for");
            }
        });
        whileloop.setOnClickListener(new OnClickListener() {

            public void onClick(View arg0) {
                ac_js.addStack("while");
                enterEvent("while");
            }
        });
        jshort.setOnClickListener(new OnClickListener() {

            public void onClick(View arg0) {
                ac_js.addStack("jshort");
                enterEvent("jshort");
            }
        });
        cifttirnak.setOnClickListener(new OnClickListener() {

            public void onClick(View arg0) {
                ac_js.addStack("dquota");
                enterEvent("dquota");
            }
        });
        tektirnak.setOnClickListener(new OnClickListener() {

            public void onClick(View arg0) {
                ac_js.addStack("squota");
                enterEvent("squota");
            }
        });
        esittir.setOnClickListener(new OnClickListener() {

            public void onClick(View arg0) {
                ac_js.addStack("equals");
                enterEvent("equals");
            }
        });
        trycatch.setOnClickListener(new OnClickListener() {

            public void onClick(View arg0) {
                ac_js.addStack("try");
                enterEvent("try");
            }
        });
        
        commandList.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        consoleAdapter = new ArrayAdapter<String>(pwt.getApplicationContext(), R.layout.show_console_row);
        commandList.setAdapter(consoleAdapter);
        
        // Console loglarına tıklandığında komutu komut satırına yaz
        commandList.setClickable(true);
        commandList.setOnItemLongClickListener(new OnItemLongClickListener() {

            public boolean onItemLongClick(AdapterView<?> parent, View v, int pos, long id) {
                String cmd = consoleAdapter.getItem(pos);
                cmd = cmd.replace("> ", "");
                command.setText(cmd);
                return true;
            }
        });
        
        clearConsole.setOnClickListener(new OnClickListener() {

            public void onClick(View arg0) {
                consoleAdapter.clear();
            }
        });
        
        command.setOnClickListener(new OnClickListener() {

            public void onClick(View arg0) {
                ac_js.clearAC();
            }
        });
        
        command.setOnKeyListener(new View.OnKeyListener() {

            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if ( event.getAction() == KeyEvent.ACTION_DOWN && 
                            keyCode == KeyEvent.KEYCODE_ENTER) {
                    // get ac action
                    if( false == enterEvent(null) ){
                        runCommand();
                    }
                    return true;
                }
                //ac_js.clearAC();
                return false;
            }
        });
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(pwt.getApplicationContext(), R.layout.jscommand_array);
        String[] protocols = pwt.getApplicationContext().getResources().getStringArray(R.array.jscommand_array);
        for (String string : protocols) {
            adapter.add(string);
        }
        command.setAdapter(adapter);
    }
    
    /**
     * Task lerden gelen veriyi set eder
     */
    public void ready(){
        isReady = true;
        if(isShow){ // tab e daha önce girildiyse task bitince veriyi güncelle
            display();
        }
    }
    
    public void showConsole(String message) {
        consoleAdapter.add(message);
        command.requestFocus();
    }
    
    public void display(){
        
    }
     
    public void clear(){
        consoleAdapter.clear();
    }
    
    /**
     * Tab değiştiğinde çalışır
     * @param tab id
     */
    public void tabChanged(String tabId){
        // içerik yüklenmemişse yükle
        if(tabId.equals("console") && isShow == false && isReady){
            display();
            pwt.getShowWeb().display();
            isShow = true;
        }
    }
}

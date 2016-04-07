package com.meizu.alimemtest.Utils;

import org.apache.commons.lang.StringUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class utils {
    public static List<String> readFileByLines(String fileName) {
        List<String> strlist = new LinkedList<String>();
        File file = new File(fileName);
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            String tempString = null;
            int line = 1;
            while ((tempString = reader.readLine()) != null) {
                String[] ress = StringUtils.split(tempString);
                if(ress.length<3){
                    strlist.add(tempString);
                }
                else{
                    if(!ress[1].equals("0")){
                        strlist.add(ress[1]);
                    }
                }
                line++;
            }
            reader.close();
            return strlist;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                }
            }
        }
        return strlist;
    }
}

package com.example.andrey.applicationb.util;

public class StringUtil {

    public static String getFileNameFromUrl(String url){
        StringBuilder stringBuilder = new StringBuilder();

        for(int i = url.length() - 1; i > 0; i--){
            if(url.charAt(i) != '/')
                stringBuilder.append(url.charAt(i));
            else
                return checkExtension(stringBuilder.reverse().toString());
        }
        return "any_file.jpg";//it should be called never
    }

    public static String checkExtension(String filename){
        String lastSymbolsOfExtension = filename.substring(filename.length() - 4, filename.length());
        if(lastSymbolsOfExtension.equals(".jpg") ||
                lastSymbolsOfExtension.equals(".png") ||
                lastSymbolsOfExtension.equals(".gif") ||
                lastSymbolsOfExtension.equals(".tif"))
            return filename;
        else return filename + ".jpg";

    }


}

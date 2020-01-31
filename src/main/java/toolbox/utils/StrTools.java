package toolbox.utils;

import java.util.Scanner;

/*
GNU GENERAL PUBLIC LICENSE
Version 3, 29 June 2007

Copyright (C) 2007 Free Software Foundation, Inc. <https://fsf.org/>

See full version of LICENSE in <https://fsf.org/>

*/

public class StrTools {
	

	public static String Capitalize(String str) {
		String result="";
		Scanner lineScan = new Scanner(str); 
        while(lineScan.hasNext()) {
            String word = lineScan.next(); 
            if (result.length()>0) { result+=" "; }
            result += Character.toUpperCase(word.charAt(0)) + word.substring(1);; 
        }
        lineScan.close();
        return result;
	}
	
}

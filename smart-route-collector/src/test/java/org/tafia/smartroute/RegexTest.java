package org.tafia.smartroute;

import org.testng.annotations.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexTest {

    @Test
    public void testRegex() {
        String regex = "flyItemClick\\('(.*)','(.*)'\\)";
        Matcher matcher = Pattern.compile(regex).matcher("temp.push(\"<li \"+(i % 2 == 0?\"\":\"class='bg2'\")+\" onclick=\\\"flyItemClick('CA4193','2018-05-09')\\\">");
        while(matcher.find()) {
            for (int i = 0; i < matcher.groupCount() + 1; i++) {
                System.out.println(matcher.group(i));
            }
            System.out.println();
        }
    }
}

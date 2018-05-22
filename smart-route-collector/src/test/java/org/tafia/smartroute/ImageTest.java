package org.tafia.smartroute;

import org.tafia.smartroute.spider.umetrip.ImageParser;
import org.testng.annotations.Test;

/**
 * Created by Dason on 2018/5/20.
 */
public class ImageTest {

    @Test
    public void parseImage() throws Exception{
        System.out.println(ImageParser.parse("1scasDQrTrlBeS29rfEBNg=="));
    }
}

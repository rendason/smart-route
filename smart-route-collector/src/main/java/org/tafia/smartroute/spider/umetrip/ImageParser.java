package org.tafia.smartroute.spider.umetrip;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Base64;
import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Dason on 2018/5/20.
 */
public class ImageParser {

    private static final Map<String, String> CHAR_MAP = new HashMap<>();
    static {
        CHAR_MAP.put("Af//f9913dd3WbO1WVMdcbURSg0nzRFlQZEGUdBAJTAYARNBQRkRCRUkUQIhAcUUgZCBQRQiIQMEURAIGUAQDUIhExDECYGUFTBNARMxaUBRCRAOARUkkQEGQYABEA==", "0");
        CHAR_MAP.put("/d91Wds1x5FFcRMNHVOlQ7WRSRcnjVFpWZkHU9NFLTU5RZtBBRNViREkcdABCVQQQxCAARQgARAAARAAAkAACAAAEAAEAAAEAAIAAAEAAQBAAIAAABAAQAAABAAg", "1");
        CHAR_MAP.put("Af9/W/01R5JUVRGRWdIsUREVWR0jxRFnWYkEU9MFrTQ4RRphQRsVCT0kUcIhA0UUwpCBABQgARCAARAAAkQACUAgEBAECAEEAQIJABEAYQBAAYAGABAA0AAABYAwABABhBIAQSFACRIRAQwEAQwQARJAASAAAUAAAIA=", "2");
        CHAR_MAP.put("gf8/018FSNJVERGlWdKNQTEVGQcHjRFrQZEGcdBEhbAZRRNhQREVCAkgUIAAAwAUQJABABQgIAMAURAIGUAQDQAhExBECYGUERJFARMxaUBRDRAOCRUkkQEGYYARUZAABUAAAQ==", "3");
        CHAR_MAP.put("AQB+HlUVXZN1WTOdVVOtQ7WRSRcFDVFoUBEHMZFEjZAQRQNhQxIVCRkEVYAhE0AUwJEhQRUiIQcFWRAYWWAQTQKlERDFQIMUFTJNARMxaUBRDZAGGRUg0AEERYMwEBABBBIBQQBACAAQAAgEAAQAAAIAAQAAAUAAAIAAEAAAQAAEAAAg", "4");
        CHAR_MAP.put("+f//39913dNVETGlSZKBEZERUQQhhVFCGQAFQRNAAZAJRAEAAwEFARkgUIAgA0AUQJABABQgIAMAURAAGUAQDUAhExBECYGUETBFARMxa0BRDRAOCRUkkQEGYYAREZAABUA=", "5");
        CHAR_MAP.put("Af7/f99139d3WbO1WVMZcbUVSw8nxREMSRAFYYNAIZAJAAEBAwEFABkgUAAgA0QUAJCBQBQgIAMAURAAGUAADQAgEwBEAYCUARJFAQMxY0BRDRAOGRUk0QEGZYERUZQAhUIAASEAAQ==", "6");
        CHAR_MAP.put("/f//X9d1XZM1GREREUAIARAECQQCAQEhQAEEEJAEgZA5RZthRxtViT0kddIBGVQSQxCIARUgARSAARAAAg==", "7");
        CHAR_MAP.put("gf93/9913dd3WbO9XVOdcbUVWw8njRFjQZEGUdAEpbAZRRNhQxkVCB0kUQAgAwQUQJABABQgIAMAURAIGUAQDQAhExBECYGUERJFARMxaUBRDRAOCRUkkQEGYYARUZAAhUIAASA=", "8");
        CHAR_MAP.put("gf9/X9813ZNXGTGlWdKMUTEVGQUjjRFjQZEEUdIABTQIBRpAQRkRCSUkVcIhE8EUwZABQRUiIQcEURAIGUAQDUAhExBECYGUERJNARMxa0BRDRAOCRUkkQEGYYABEZAABA==", "9");
        CHAR_MAP.put("AfBDVf802RFXXTEMTZIlABEREREhhBBCSBAEQRBAIBAI", ":");
        CHAR_MAP.put("AQAAAH4AAABVVQAASZIkABEREREhhBBCSBAEQRBAIBAI", ".");
        CHAR_MAP.put("8f91Xdd1Xdd1WbO1WROVUaERQQslDBFMWRkHMRNFgZAhRAFBBAIRATAgRcAAEcUQw5DJQRUiLQeFWRMYQQAAAAAAAAAAAAAAAAAAAQMxSEBRCRAKGRUEUQAAIQMQQBQBAJIBQEAUCAAxAwAEUQABCEsAQBEBgQIhQQAQRQABgRACABEEAAQCARAASFABQBAYAAMABSEAEQAlQACBQBCAAIEUQAABQTQAAQIhAwEEBFEAABAIGQAAQBAF", "%");
        CHAR_MAP.put("AQCABwBAFQAgSQAQEQEIIQQEQRACgUABAYEBAUIEASQQARhAARgAASQABUAAEA==", "-");
    }

    public static String parse(String str) {
        try {
            URL url = new URL("http://www.umetrip.com/mskyweb/graphic.do?str="+str+"&width=200&height=40&front=0,0,0&back=255,255,255&size=60&xpos=0&ypos=40");
            BufferedImage image = ImageIO.read(url);
            int left = 0;
            int width = image.getWidth();
            StringBuilder stringBuilder = new StringBuilder();
            while (true) {
                while (isBlankColumn(image, left) && left < width - 1) left++;
                if (left == width - 1) break;
                int right = left + 1;
                while (!isBlankColumn(image, right) && right < width - 1) right++;
                stringBuilder.append(CHAR_MAP.getOrDefault(toString(image, left, right - left + 1), "?"));
                if (right == width - 1) break;
                left = right;
            }
            return stringBuilder.toString();
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }

    }

    private static boolean isBlankColumn(BufferedImage image, int column) {
        for (int i = 0; i < image.getHeight(); i++) {
            if (image.getRGB(column, i) != -1)
                return false;
        }
        return true;
    }

    private static String toString(BufferedImage image, int x, int width) {
        BitSet bitSet = new BitSet(width + image.getHeight());
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < image.getHeight(); j++) {
                if (image.getRGB(x + i, j) != -1)
                    bitSet.set(i * j);
            }
        }
        return Base64.getEncoder().encodeToString(bitSet.toByteArray());
    }
}

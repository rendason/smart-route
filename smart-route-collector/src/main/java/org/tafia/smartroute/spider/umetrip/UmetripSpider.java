package org.tafia.smartroute.spider.umetrip;

import com.alibaba.fastjson.JSON;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class UmetripSpider implements PageProcessor {

    private Site site = Site.me();

    @Override
    public void process(Page page) {
        String url = page.getUrl().toString();
        if (url.endsWith("citiesData.js")) {
            String json = page.getRawText().substring(12);
            List<String> airports = JSON.parseArray(json).toJavaList(UmetripAirport.class).stream()
                    .filter(e -> "".equals(e.getEnglish()))
                    .map(UmetripAirport::getTcode)
                    .collect(Collectors.toList());

            List<String> dates = new ArrayList<>();
            LocalDate date = LocalDate.now();
            LocalDate start = LocalDate.of(2017, 11, 7);
            while (date.isAfter(start)) {
                dates.add(date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
                date = date.minusDays(1);
            }
            for (int i = 0; i < airports.size(); i++) {
                for (int j = 0; j < airports.size(); j++) {
                    if (i == j) continue;
                    for (String d : dates) {
                        String target = "http://www.umetrip.com/mskyweb/fs/fa.do?dep=" + airports.get(i) + "&arr=" + airports.get(j) + "&date=" + d + "&channel=";
                        page.addTargetRequest(target);
                    }
                }
            }
            return;
        }
        if (url.startsWith("http://www.umetrip.com/mskyweb/fs/fa.do")) {
            String text = page.getRawText();
            int start = 0;
            while ((start = text.indexOf("flyItemClick(", start + 13)) != -1) {
                int end = text.indexOf(")", start);
                String[] strings = text.substring(start + 13, end).split("'");
                String target = "http://www.umetrip.com/mskyweb/fs/fc.do?flightNo=" + strings[1] + "&date=" + strings[3] + "&channel=";
                page.addTargetRequest(target);
            }
            return;
        }
        if (url.startsWith("http://www.umetrip.com/mskyweb/fs/fc.do")) {
            System.out.println(url);
        }
    }

    @Override
    public Site getSite() {
        return site;
    }

    public static void main(String[] args) {
        Spider.create(new UmetripSpider())
                //.addUrl("http://www.umetrip.com/mskyweb/js/citiesData.js")
                .addUrl("http://www.umetrip.com/mskyweb/fs/fa.do?dep=PEK&arr=LHW&date=2018-05-09&channel=")
                .run();
    }
}

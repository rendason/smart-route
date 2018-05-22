package org.tafia.smartroute.spider.umetrip;

import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tafia.smartroute.spider.common.MultiplePageProcessor;
import org.tafia.smartroute.spider.common.StoragePipeline;
import org.tafia.smartroute.spider.common.annotation.PageUrlStartsWith;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.scheduler.PriorityScheduler;
import us.codecraft.webmagic.selector.Selectable;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class DailyFlightSpider {

    private LocalDate flightDate;

    public DailyFlightSpider(int year, int month, int day) {
        flightDate = LocalDate.of(year, month, day);
    }

    private Logger logger = LoggerFactory.getLogger(getClass());

    @PageUrlStartsWith("http://www.umetrip.com/mskyweb/js/citiesData.js")
    private void processCitiesData(Page page) {
        String json = page.getRawText().substring(12);
        List<String> airports = JSON.parseArray(json).toJavaList(UmetripAirport.class).stream()
                .filter(e -> "".equals(e.getEnglish()))
                .map(UmetripAirport::getTcode)
                .collect(Collectors.toList());

        String dateStr = flightDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        int airportsSize = airports.size();
        List<Request> requests = IntStream.range(0, airportsSize * airportsSize)
                .filter(n -> n / airportsSize != n % airportsSize)
                .parallel()
                .mapToObj(n -> String.format("http://www.umetrip.com/mskyweb/fs/fa.do?dep=%s&arr=%s&date=%s&channel=",
                        airports.get(n / airportsSize), airports.get(n % airportsSize), dateStr))
                .peek(u -> logger.info("add url: {}", u))
                .map(Request::new)
                .collect(Collectors.toList());
        page.getTargetRequests().addAll(requests);
        logger.info("add {} airport to airport urls that date is {}", page.getTargetRequests().size(), dateStr);
    }

    @PageUrlStartsWith("http://www.umetrip.com/mskyweb/fs/fa.do")
    private void processFlightList(Page page) {
        String text = page.getRawText();
        int start = 0;
        int count = 0;
        while ((start = text.indexOf("flyItemClick(", start + 13)) != -1) {
            int end = text.indexOf(")", start);
            String[] strings = text.substring(start + 13, end).split("'");
            String target = "http://www.umetrip.com/mskyweb/fs/fc.do?flightNo=" + strings[1] + "&date=" + strings[3] + "&channel=";
            page.addTargetRequest(new Request(target).setPriority(1));
            logger.info("add url: {}", target);
            count++;
        }
        logger.info("add {} flight urls", count);
    }

    @PageUrlStartsWith("http://www.umetrip.com/mskyweb/fs/fc.do")
    private void processFlightDetail(Page page) {
        Selectable flightDetail = page.getHtml().xpath("/html/body/div[@class='main']/div[@class='flydetail']");
        DailyFlight flight = new DailyFlight();
        flight.setFlightNo(flightDetail.xpath("div[@id='flySearch']//input[@id='byNumInput']/@value").toString());
        flight.setFlightDate(flightDetail.xpath("//div[@class='f_tit']/span/text()").toString().trim().split(" ")[1]);
        flight.setStatus(flightDetail.xpath("//div[@class='state']/div/text()").toString());
        flight.setMileage(toInt(flightDetail.xpath("//li[@class='mileage']/span/text()").toString().replace("公里", "")));
        flight.setDuration(convertToMinute(flightDetail.xpath("//li[@class='time']/span/text()").toString()));
        String[] modelAge = flightDetail.xpath("//li[@class='age']/span/text()").toString().split("[/年]");
        flight.setAircraftModel(modelAge[0]);
        flight.setAircraftAge(toFloat(modelAge[1]));
        flight.setPunctualityRate(Float.valueOf(parseImage(flightDetail.xpath("//li[@class='per']/span/img/@src").toString())
                .replace("%", "")));
        List<Selectable> nodes = flightDetail.xpath("div[@class='del_com']/div").nodes();
        String[] fromInfo = extractTerminalDetail(nodes.get(5));
        flight.setFromCity(fromInfo[0]);
        flight.setFromAirport(fromInfo[1]);
        flight.setFromAirportCode(fromInfo[2]);
        flight.setFromWeather(fromInfo[3]);
        flight.setFromVisibility(toInt(fromInfo[4]));
        flight.setFromFlow(fromInfo[5]);
        flight.setPlannedDeparture(fromInfo[6]);
        flight.setActualDeparture(fromInfo[7]);
        String[] toInfo = extractTerminalDetail(nodes.get(6));
        flight.setToCity(toInfo[0]);
        flight.setToAirport(toInfo[1]);
        flight.setToAirportCode(toInfo[2]);
        flight.setToWeather(toInfo[3]);
        flight.setToVisibility(toInt(toInfo[4]));
        flight.setToFlow(toInfo[5]);
        flight.setPlannedInbound(toInfo[6]);
        flight.setActualInbound(toInfo[7]);
        page.putField("flight", flight);
    }

    private int convertToMinute(String str) {
        if (!str.contains("小时"))
            return Integer.valueOf(str.replace("分", ""));
        if (!str.contains("分"))
            return Integer.valueOf(str.replace("小时", "")) * 60;
        String[] hourMinute = str.split("小时|分");
        return Integer.valueOf(hourMinute[0]) * 60 + Integer.valueOf(hourMinute[1]);
    }

    private String[] extractTerminalDetail(Selectable selectable) {
        String[] result = new String[8];
        String fromCityAirport[] = selectable.xpath("div[@class='f_tit']/h2/text()").toString().trim().split(" ");
        result[0] = fromCityAirport[0];  //出发城市
        result[1] = fromCityAirport[1];  //出发机场名称
        result[2] = fromCityAirport[3];  //出发机场代码
        result[3] = selectable.xpath("div[@class='f_com']/div[@class='f_r']/p[1]/text()").toString().trim()  //温度
                + selectable.xpath("div[@class='f_com']/div[@class='f_r']/p[1]/b/text()").toString().trim(); //天气
        result[4] = selectable.xpath("div[@class='f_com']/div[@class='f_r']/p[2]/text()").toString()
                .trim().split(" ")[1]; //能见度
        result[5] = selectable.xpath("div[@class='f_com']/div[@class='f_r']/p[3]/text()").toString()
                .trim().split(" ")[1]; //流量
        List<String> images = selectable.xpath("div[@class='f_com']/div[@class='f_m']/div[@class='time']//img/@src").all();
        result[6] = parseImage(images.get(0)); //计划起飞
        result[7] = parseImage(images.get(1)); //实际起飞
        return result;
    }

    private String parseImage(String src) {
        int strIndex = src.indexOf("?str=") + 5;
        String imageStr = src.substring(strIndex, src.indexOf("&", strIndex));
        return ImageParser.parse(imageStr);
    }

    private int toInt(String s) {
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private float toFloat(String s) {
        try {
            return Float.parseFloat(s);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    public static void main(String[] args) {
        //LocalDate date = LocalDate.now().minusDays(182);
        LocalDate date = LocalDate.of(2018, 5, 1);
        while (date.isBefore(LocalDate.now())) {
            DailyFlightSpider spider = new DailyFlightSpider(date.getYear(), date.getMonthValue(), date.getDayOfMonth());
            date = date.plusDays(1);
            MultiplePageProcessor processor = new MultiplePageProcessor(Site.me().setCharset("UTF-8").setRetryTimes(3));
            processor.addProcessMethod(spider);
            Spider.create(processor)
                    .setScheduler(new PriorityScheduler())
                    .addUrl("http://www.umetrip.com/mskyweb/js/citiesData.js")
                    .addPipeline(new StoragePipeline())
                    .thread(4)
                    .run();
        }
    }
}

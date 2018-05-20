package org.tafia.smartroute;

import com.alibaba.fastjson.JSON;
import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import org.tafia.smartroute.spider.umetrip.UmetripAirport;
import org.testng.annotations.Test;

public class SmartDaoTest {

    @Test
    public void testInsert() {
        MysqlDataSource dataSource = new MysqlDataSource();
        dataSource.setUrl("jdbc:mysql://tafia.org:3306/smart_route?useUnicode=true&characterEncoding=utf-8");
        dataSource.setUser("root");
        dataSource.setPassword("tafia$123456");
        UmetripAirport airport = new UmetripAirport();
        airport.setName("aaa");
        airport.setCity("bbb");
        airport.setMatch("ccc");
        airport.setPinyin("ddd");
        airport.setTcode("eee");
        SmartDao.of(dataSource).saveOne(airport);
    }

    @Test
    public void testJson() {

        JSON.parseObject("{\"object\":[\"1\", \"2\"]}").toJavaObject(Demo.class);
        JSON.parseObject("{\"object\":[{\"a\":\"1\", \"b\":\"2\"}]}").toJavaObject(Demo.class);
        JSON.parseObject("{\"object\":\"a\"}").toJavaObject(Demo.class);
    }

    public static class Demo {
        public Demo() {
        }

        public void setObject(Object o) {
            System.out.println(o.getClass() + ": " + o);
        }
    }

}

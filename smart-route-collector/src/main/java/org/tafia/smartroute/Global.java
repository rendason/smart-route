package org.tafia.smartroute;

import com.alibaba.druid.pool.DruidDataSource;

public class Global {

    private static SmartDao smartDao;
    static {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:mysql://tafia.org:3306/smart_route?useUnicode=true&characterEncoding=utf-8");
        dataSource.setUsername("root");
        dataSource.setPassword("tafia$123456");
        smartDao = SmartDao.of(dataSource);
    }

    private Global(){}

    public static SmartDao smartDao() {
        return smartDao;
    }
}

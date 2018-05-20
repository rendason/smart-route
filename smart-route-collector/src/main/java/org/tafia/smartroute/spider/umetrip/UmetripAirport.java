package org.tafia.smartroute.spider.umetrip;

import com.alibaba.fastjson.annotation.JSONField;
import org.tafia.smartroute.SmartEntity;

public class UmetripAirport extends SmartEntity {

    private String name;

    private String city;

    private String match;

    private String english;

    private String pinyin;

    private String tcode;

    public String getName() {
        return name;
    }

    @JSONField(alternateNames = "airport")
    public void setName(String name) {
        this.name = name;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getMatch() {
        return match;
    }

    public void setMatch(String match) {
        this.match = match;
    }

    public String getEnglish() {
        return english;
    }

    @JSONField(alternateNames = "enAirport")
    public void setEnglish(String english) {
        this.english = english;
    }

    public String getPinyin() {
        return pinyin;
    }

    public void setPinyin(String pinyin) {
        this.pinyin = pinyin;
    }

    public String getTcode() {
        return tcode;
    }

    public void setTcode(String tcode) {
        this.tcode = tcode;
    }
}

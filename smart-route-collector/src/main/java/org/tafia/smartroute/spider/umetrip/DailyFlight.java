package org.tafia.smartroute.spider.umetrip;

import org.tafia.smartroute.SmartEntity;

/**
 * Created by Dason on 2018/5/20.
 */
public class DailyFlight extends SmartEntity {

    private String flightNo;
    private String flightDate;
    private String fromCity;
    private String toCity;
    private String fromAirport;
    private String toAirport;
    private String fromAirportCode;
    private String toAirportCode;
    private Integer mileage;
    private Integer duration;
    private String aircraftModel;
    private Float aircraftAge;
    private Float punctualityRate;
    private String fromWeather;
    private String toWeather;
    private Integer fromVisibility;
    private Integer toVisibility;
    private String fromFlow;
    private String toFlow;
    private String plannedDeparture;
    private String actualDeparture;
    private String plannedInbound;
    private String actualInbound;

    public String getFlightNo() {
        return flightNo;
    }

    public void setFlightNo(String flightNo) {
        this.flightNo = flightNo;
    }

    public String getFlightDate() {
        return flightDate;
    }

    public void setFlightDate(String flightDate) {
        this.flightDate = flightDate;
    }

    public Integer getMileage() {
        return mileage;
    }

    public void setMileage(Integer mileage) {
        this.mileage = mileage;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public String getAircraftModel() {
        return aircraftModel;
    }

    public void setAircraftModel(String aircraftModel) {
        this.aircraftModel = aircraftModel;
    }

    public Float getAircraftAge() {
        return aircraftAge;
    }

    public void setAircraftAge(Float aircraftAge) {
        this.aircraftAge = aircraftAge;
    }

    public Float getPunctualityRate() {
        return punctualityRate;
    }

    public void setPunctualityRate(Float punctualityRate) {
        this.punctualityRate = punctualityRate;
    }

    public String getFromCity() {
        return fromCity;
    }

    public void setFromCity(String fromCity) {
        this.fromCity = fromCity;
    }

    public String getToCity() {
        return toCity;
    }

    public void setToCity(String toCity) {
        this.toCity = toCity;
    }

    public String getFromAirport() {
        return fromAirport;
    }

    public void setFromAirport(String fromAirport) {
        this.fromAirport = fromAirport;
    }

    public String getToAirport() {
        return toAirport;
    }

    public void setToAirport(String toAirport) {
        this.toAirport = toAirport;
    }

    public String getFromAirportCode() {
        return fromAirportCode;
    }

    public void setFromAirportCode(String fromAirportCode) {
        this.fromAirportCode = fromAirportCode;
    }

    public String getToAirportCode() {
        return toAirportCode;
    }

    public void setToAirportCode(String toAirportCode) {
        this.toAirportCode = toAirportCode;
    }

    public String getFromWeather() {
        return fromWeather;
    }

    public void setFromWeather(String fromWeather) {
        this.fromWeather = fromWeather;
    }

    public String getToWeather() {
        return toWeather;
    }

    public void setToWeather(String toWeather) {
        this.toWeather = toWeather;
    }

    public Integer getFromVisibility() {
        return fromVisibility;
    }

    public void setFromVisibility(Integer fromVisibility) {
        this.fromVisibility = fromVisibility;
    }

    public Integer getToVisibility() {
        return toVisibility;
    }

    public void setToVisibility(Integer toVisibility) {
        this.toVisibility = toVisibility;
    }

    public String getFromFlow() {
        return fromFlow;
    }

    public void setFromFlow(String fromFlow) {
        this.fromFlow = fromFlow;
    }

    public String getToFlow() {
        return toFlow;
    }

    public void setToFlow(String toFlow) {
        this.toFlow = toFlow;
    }

    public String getPlannedDeparture() {
        return plannedDeparture;
    }

    public void setPlannedDeparture(String plannedDeparture) {
        this.plannedDeparture = plannedDeparture;
    }

    public String getActualDeparture() {
        return actualDeparture;
    }

    public void setActualDeparture(String actualDeparture) {
        this.actualDeparture = actualDeparture;
    }

    public String getPlannedInbound() {
        return plannedInbound;
    }

    public void setPlannedInbound(String plannedInbound) {
        this.plannedInbound = plannedInbound;
    }

    public String getActualInbound() {
        return actualInbound;
    }

    public void setActualInbound(String actualInbound) {
        this.actualInbound = actualInbound;
    }
}

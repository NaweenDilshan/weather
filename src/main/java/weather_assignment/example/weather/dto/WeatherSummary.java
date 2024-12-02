package weather_assignment.example.weather.dto;

public class WeatherSummary {
    private String city;
    private double averageTemperature;
    private String coldestDay;  // Date of the coldest day
    private String hottestDay;  // Date of the hottest day

    public WeatherSummary(String city, double averageTemperature, String coldestDay, String hottestDay) {
        this.city = city;
        this.averageTemperature = averageTemperature;
        this.coldestDay = coldestDay;
        this.hottestDay = hottestDay;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public double getAverageTemperature() {
        return averageTemperature;
    }

    public void setAverageTemperature(double averageTemperature) {
        this.averageTemperature = averageTemperature;
    }

    public String getColdestDay() {
        return coldestDay;
    }

    public void setColdestDay(String coldestDay) {
        this.coldestDay = coldestDay;
    }

    public String getHottestDay() {
        return hottestDay;
    }

    public void setHottestDay(String hottestDay) {
        this.hottestDay = hottestDay;
    }
}

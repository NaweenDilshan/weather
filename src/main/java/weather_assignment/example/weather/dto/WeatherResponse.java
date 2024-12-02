package weather_assignment.example.weather.dto;

public class WeatherResponse {
    private String city;
    private double temperature;
    private double tempMin;
    private double tempMax;

    public WeatherResponse(String city, double temperature, double tempMin, double tempMax) {
        this.city = city;
        this.temperature = temperature;
        this.tempMin = tempMin;
        this.tempMax = tempMax;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public double getTemperature() {
        return temperature;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public double getTempMin() {
        return tempMin;
    }

    public void setTempMin(double tempMin) {
        this.tempMin = tempMin;
    }

    public double getTempMax() {
        return tempMax;
    }

    public void setTempMax(double tempMax) {
        this.tempMax = tempMax;
    }
}


package weather_assignment.example.weather.service;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import weather_assignment.example.weather.dto.WeatherSummary;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Service
public class WeatherService {

    private final RestTemplate restTemplate;
    private final String apiUrl;
    private final String apiKey;

    public WeatherService(@Value("${openweathermap.api.url}") String apiUrl,
                          @Value("${openweathermap.api.key}") String apiKey) {
        this.restTemplate = new RestTemplate();
        this.apiUrl = apiUrl;
        this.apiKey = apiKey;
    }

    @Async
    @Cacheable(value = "weatherCache", key = "#cityName", unless = "#result == null")
    public CompletableFuture<WeatherSummary> fetchWeatherDataByCity(String cityName) {
        String url = String.format("%s?q=%s&appid=%s&units=metric", apiUrl, cityName, apiKey);
        ResponseEntity<Map> responseEntity = restTemplate.getForEntity(url, Map.class);

        if (responseEntity.getStatusCode() == HttpStatus.OK && responseEntity.getBody() != null) {
            return CompletableFuture.completedFuture(parseWeatherResponse(responseEntity.getBody()));
        } else {
            throw new RuntimeException("Failed to fetch weather data");
        }
    }


    private WeatherSummary parseWeatherResponse(Map<String, Object> body) {
        Map<String, Object> cityInfo = (Map<String, Object>) body.get("city");
        List<Map<String, Object>> forecasts = (List<Map<String, Object>>) body.get("list");

        if (cityInfo == null || forecasts == null || forecasts.isEmpty()) {
            throw new RuntimeException("Invalid data in API response.");
        }

        String cityName = (String) cityInfo.get("name");
        double totalTemp = 0;
        double tempMin = Double.MAX_VALUE;
        double tempMax = Double.MIN_VALUE;
        String coldestDay = "";
        String hottestDay = "";

        for (Map<String, Object> forecast : forecasts) {
            Map<String, Object> main = (Map<String, Object>) forecast.get("main");

            if (main == null) {
                continue; // Skip invalid entries
            }

            double temp = getAsDouble(main.get("temp"));
            double minTemp = getAsDouble(main.get("temp_min"));
            double maxTemp = getAsDouble(main.get("temp_max"));
            String date = (String) forecast.get("dt_txt");

            // Calculate total temp for average
            totalTemp += temp;

            // Track coldest and hottest day
            if (minTemp < tempMin) {
                tempMin = minTemp;
                coldestDay = date;
            }
            if (maxTemp > tempMax) {
                tempMax = maxTemp;
                hottestDay = date;
            }
        }

        double averageTemp = totalTemp / forecasts.size();

        return new WeatherSummary(cityName, averageTemp, coldestDay, hottestDay);
    }

    // Helper method to safely convert Object to double
    private double getAsDouble(Object value) {
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        } else {
            throw new RuntimeException("Invalid value type for temperature: " + value);
        }
    }
}

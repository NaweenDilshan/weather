package weather_assignment.example.weather.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.http.HttpStatus;
import reactor.core.publisher.Mono;
import org.springframework.scheduling.annotation.Async;
import weather_assignment.example.weather.dto.WeatherSummary;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Service
public class WeatherService {

    private final WebClient webClient;
    private final String apiKey;

    public WeatherService(@Value("${openweathermap.api.url}") String apiUrl,
                          @Value("${openweathermap.api.key}") String apiKey) {
        this.webClient = WebClient.builder()
                .baseUrl(apiUrl)
                .defaultHeader("Content-Type", "application/json")
                .build();
        this.apiKey = apiKey;
    }

    @Async
    @Cacheable(value = "weatherCache", key = "#cityName", unless = "#result == null")
    public CompletableFuture<WeatherSummary> fetchWeatherDataByCity(String cityName) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("q", cityName)
                        .queryParam("units", "metric")
                        .queryParam("appid", apiKey)
                        .build())
                .retrieve()
                .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(), response -> {

                    return Mono.error(new RuntimeException("Error response from API: " + response.statusCode()));
                })
                .bodyToMono(Map.class)
                .map(this::parseWeatherResponse)
                .toFuture();
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
                continue; // Skip
            }

            double temp = getAsDouble(main.get("temp"));
            double minTemp = getAsDouble(main.get("temp_min"));
            double maxTemp = getAsDouble(main.get("temp_max"));
            String date = (String) forecast.get("dt_txt");

            // Calculate temp average
            totalTemp += temp;

            // coldest and hottest day
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

    private double getAsDouble(Object value) {
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        } else {
            throw new RuntimeException("Invalid value type for temperature: " + value);
        }
    }
}

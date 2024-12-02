//package weather_assignment.example.weather;

package weather_assignment.example.weather;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import weather_assignment.example.weather.dto.WeatherSummary;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.cache.CacheManager;
import org.springframework.cache.Cache;
import org.springframework.beans.factory.annotation.Value;
import weather_assignment.example.weather.service.WeatherService;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.Map;
import java.util.HashMap;

public class WeatherServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private CacheManager cacheManager;

    private WeatherService weatherService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        weatherService = new WeatherService("https://api.openweathermap.org/data/2.5/forecast", "a2f05adf9bb9544d6acef2f67b1f3660");
    }

    @Test
    public void testFetchWeatherDataByCity() throws ExecutionException, InterruptedException {
        // Mock the response from RestTemplate
        String cityName = "London";
        Map<String, Object> mockResponse = new HashMap<>();
        mockResponse.put("city", Map.of("name", cityName));

        // Prepare mock response
        ResponseEntity<Map> responseEntity = new ResponseEntity<>(mockResponse, HttpStatus.OK);
        when(restTemplate.getForEntity(anyString(), eq(Map.class))).thenReturn(responseEntity);

        // Fetch weather data
        CompletableFuture<WeatherSummary> weatherData = weatherService.fetchWeatherDataByCity(cityName);
        WeatherSummary result = weatherData.get();  // Blocking call to get result

        // Verify the results
        assertEquals("London", result.getCity());
    }

    @Test
    public void testCachingBehavior() throws ExecutionException, InterruptedException {
        // First request: the cache will not have the data, so it will fetch from the API.
        String cityName = "London";
        Map<String, Object> mockResponse = new HashMap<>();
        mockResponse.put("city", Map.of("name", cityName));

        ResponseEntity<Map> responseEntity = new ResponseEntity<>(mockResponse, HttpStatus.OK);
        when(restTemplate.getForEntity(anyString(), eq(Map.class))).thenReturn(responseEntity);

        CompletableFuture<WeatherSummary> weatherData1 = weatherService.fetchWeatherDataByCity(cityName);
        WeatherSummary result1 = weatherData1.get();

        assertEquals("London", result1.getCity());

        // Second request: the cache will return the cached data, not calling the API.
        CompletableFuture<WeatherSummary> weatherData2 = weatherService.fetchWeatherDataByCity(cityName);
        WeatherSummary result2 = weatherData2.get();

        assertEquals("London", result2.getCity());
        verify(restTemplate, times(1)).getForEntity(anyString(), eq(Map.class));  // Verify API is only called once
    }
}


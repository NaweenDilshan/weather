package weather_assignment.example.weather.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import weather_assignment.example.weather.dto.WeatherResponse;
import weather_assignment.example.weather.dto.WeatherSummary;
import weather_assignment.example.weather.service.WeatherService;

import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/weather")
public class WeatherController {
    private final WeatherService weatherService;

    public WeatherController(WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    @GetMapping
    public ResponseEntity<WeatherSummary> getWeatherByCity(
            @RequestParam("city") String cityName) {
        try {
            // Wait for the CompletableFuture to complete and get the result
            WeatherSummary response = weatherService.fetchWeatherDataByCity(cityName).get();
            return ResponseEntity.ok(response);
        } catch (ExecutionException e) {
            // Handle the case where the asynchronous task fails
            e.printStackTrace();  // Log the stack trace for debugging
            return ResponseEntity.status(500).body(null);
        } catch (InterruptedException e) {
            // Handle interruption (e.g., thread was interrupted)
            Thread.currentThread().interrupt(); // Restore the interrupted status
            e.printStackTrace();  // Log the stack trace for debugging
            return ResponseEntity.status(500).body(null);
        } catch (Exception e) {
            // Catch any other exceptions that might occur
            e.printStackTrace();  // Log the stack trace for debugging
            return ResponseEntity.status(500).body(null);
        }
    }
}




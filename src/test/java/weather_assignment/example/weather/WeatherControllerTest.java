package weather_assignment.example.weather;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import weather_assignment.example.weather.controller.WeatherController;
import weather_assignment.example.weather.service.WeatherService;
import weather_assignment.example.weather.dto.WeatherSummary;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class WeatherControllerTest {

    @Mock
    private WeatherService weatherService;

    private WeatherController weatherController;
    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        weatherController = new WeatherController(weatherService);
        mockMvc = MockMvcBuilders.standaloneSetup(weatherController).build();
    }

    @Test
    public void testGetWeatherByCity() throws Exception {
        String cityName = "London";
        WeatherSummary mockWeatherSummary = new WeatherSummary(cityName, 15.5, "2024-12-01", "2024-12-03");
        CompletableFuture<WeatherSummary> mockFuture = CompletableFuture.completedFuture(mockWeatherSummary);

        when(weatherService.fetchWeatherDataByCity(cityName)).thenReturn(mockFuture);

        // Perform a GET request and verify the response
        mockMvc.perform(MockMvcRequestBuilders.get("/weather?city=" + cityName))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.city").value(cityName))
                .andExpect(jsonPath("$.averageTemperature").value(15.5));

        verify(weatherService, times(1)).fetchWeatherDataByCity(cityName);
    }
}

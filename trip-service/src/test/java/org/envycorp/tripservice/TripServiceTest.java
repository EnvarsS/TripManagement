package org.envycorp.tripservice;

import org.envycorp.tripservice.entity.Trip;
import org.envycorp.tripservice.repository.TripRepository;
import org.envycorp.tripservice.service.TripService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TripServiceTest {
    @Mock private TripRepository repository;
    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;
    @InjectMocks
    private TripService tripService;

    @Test
    void testGetByIds() {
        List<Long> ids = List.of(1L, 2L);
        when(repository.findAllByIdIn(ids)).thenReturn(List.of(new Trip(1L, "Paris"), new Trip(2L, "London")));

        List<Trip> result = tripService.getByIds(ids);

        assertEquals(2, result.size());
        verify(repository).findAllByIdIn(ids);
    }
}

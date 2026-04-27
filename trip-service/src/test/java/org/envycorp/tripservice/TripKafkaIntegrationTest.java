package org.envycorp.tripservice;

import org.envycorp.tripservice.client.UserClient;
import org.envycorp.tripservice.model.TripInput;
import org.envycorp.tripservice.service.TripService;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;

import java.time.Duration;
import java.util.Collections;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
// 💡 Активуємо вбудовану Kafka для тестів у фоновому режимі
@EmbeddedKafka(partitions = 1, topics = { "trip-events" })
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TripKafkaIntegrationTest {

    @Autowired
    private TripService tripService;

    @Autowired
    private EmbeddedKafkaBroker embeddedKafkaBroker; // 💡 Допоміжний бін Spring Kafka

    @MockBean
    private UserClient userClient; // 💡 Мокаємо Feign-клієнт, щоб тест не йшов у User-сервіс

    private Consumer<String, String> consumer;

    @BeforeAll
    void setUp() {
        // Заглушка (Mock) для OpenFeign: повертаємо true при перевірці користувача
        Mockito.when(userClient.checkUserExists(1L)).thenReturn(true);

        // Налаштовуємо властивості для тестового споживача (Consumer)
        Map<String, Object> consumerProps = KafkaTestUtils.consumerProps("testGroup", "true", embeddedKafkaBroker);
        consumerProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        DefaultKafkaConsumerFactory<String, String> cf = new DefaultKafkaConsumerFactory<>(consumerProps);
        consumer = cf.createConsumer();

        // Підписуємося на топік, куди TripService буде слати повідомлення
        consumer.subscribe(Collections.singleton("trip-events"));
    }

    @AfterAll
    void tearDown() {
        if (consumer != null) {
            consumer.close(); // Звільняємо ресурси після тестів
        }
    }

    @Test
    void testKafkaEventPublishedOnTripCreation() {
        // 1. Готуємо вхідні дані для створення поїздки
        TripInput input = new TripInput();
        input.setUserId(1L);
        input.setOrigin("Харків");
        input.setDestination("Одеса");
        input.setDepartureTime("2026-07-20T14:30:00");

        // 2. Викликаємо метод сервісу. Всередині нього спрацює kafkaTemplate.send(...)
        var savedTrip = tripService.createTrip(input);

        // 3. Спроба зчитати 1 єдиний запис з нашої вбудованої Kafka з таймаутом очікування 5 секунд
        //ConsumerRecord<String, String> receivedRecord = KafkaTestUtils.getSingleRecord(consumer, "trip-events", 5000);
        ConsumerRecord<String, String> receivedRecord = KafkaTestUtils.getSingleRecord(consumer, "topic-name", Duration.ofMillis(1000));

        // 4. Робимо перевірки за допомогою AssertJ
        assertThat(receivedRecord).isNotNull();

        // Очікуваний шаблон повідомлення, який прописаний в сервісі
        String expectedMessage = "TRIP_CREATED:" + savedTrip.getId();

        assertThat(receivedRecord.value()).isEqualTo(expectedMessage);
    }
}

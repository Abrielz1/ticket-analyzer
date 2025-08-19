package ru.abriel.ticket_analyzer.shared.util;

import ru.abriel.ticket_analyzer.domain.model.flight.AirportInfo;
import ru.abriel.ticket_analyzer.domain.model.flight.FlightSegment;
import ru.abriel.ticket_analyzer.domain.model.geography.GeoPoint;
import ru.abriel.ticket_analyzer.domain.model.geography.Latitude;
import ru.abriel.ticket_analyzer.domain.model.geography.Longitude;
import ru.abriel.ticket_analyzer.domain.model.money.Price;
import ru.abriel.ticket_analyzer.domain.model.ticket.Ticket;
import ru.abriel.ticket_analyzer.dto.TicketInputDto;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;

/**
 * A utility class for mapping "dirty" DTOs to "clean" Domain Models.
 * This is the Anti-Corruption Layer for our application.
 */
public class TicketMapper {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yy H:mm");
    private static final ZoneId VVO_ZONE = ZoneId.of("Asia/Vladivostok");
    private static final ZoneId TLV_ZONE = ZoneId.of("Asia/Tel_Aviv");
    private TicketMapper() {
        throw new RuntimeException("Utility Class!");
    }

    public static Ticket fromDto(TicketInputDto dto) {
        try {
            Price price = new Price(dto.price(), "RUB");

            // Use NaN for missing coordinates instead of null.
            GeoPoint originLocation = new GeoPoint(new Longitude(Double.NaN), new Latitude(Double.NaN));
            AirportInfo origin = new AirportInfo(dto.origin(), dto.originName(), VVO_ZONE, originLocation);

            GeoPoint destLocation = new GeoPoint(new Longitude(Double.NaN), new Latitude(Double.NaN));
            AirportInfo destination = new AirportInfo(dto.destination(), dto.destinationName(), TLV_ZONE, destLocation);

            LocalDateTime departureLdt = LocalDateTime.parse(dto.departureDate() + " " + dto.departureTime(), FORMATTER);
            ZonedDateTime departureZdt = ZonedDateTime.of(departureLdt, VVO_ZONE);

            LocalDateTime arrivalLdt = LocalDateTime.parse(dto.arrivalDate() + " " + dto.arrivalTime(), FORMATTER);
            ZonedDateTime arrivalZdt = ZonedDateTime.of(arrivalLdt, TLV_ZONE);

            FlightSegment segment = new FlightSegment(origin, departureZdt, destination, arrivalZdt);

            return new Ticket(price, dto.carrier(), "UNKNOWN", Collections.singletonList(segment));

        } catch (Exception e) {
            System.err.println("Failed to map ticket due to parsing error: " + e.getMessage());
            return null;
        }
    }
}

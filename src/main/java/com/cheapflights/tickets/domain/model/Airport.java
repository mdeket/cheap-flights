package com.cheapflights.tickets.domain.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 * Used as connection between City in relational database and Airport in graph database.
 * External id field represents externalAirportId in graph Airport entity, acts as "fake" foreign key.
 */
@Data
@NoArgsConstructor
@Table(name = "airport")
@Entity(name = "airport")
public class Airport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Id from the importing file. It will also be used as a way to map this airport to the one from graph db.
     */
    @NotNull
    @Column(name = "external_id")
    private Long externalId;

    @NotNull
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "city", nullable = false)
    private City city;

    Airport(Long id, @NotNull Long externalId, @NotNull String name, City city) {
        this.id = id;
        this.externalId = externalId;
        this.name = name;
        this.city = city;
    }

    public static AirportBuilder builder() {
        return new AirportBuilder();
    }

    public static class AirportBuilder {
        private Long id;
        private @NotNull Long externalId;
        private @NotNull String name;
        private City city;

        AirportBuilder() {
        }

        public AirportBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public AirportBuilder externalId(@NotNull Long externalId) {
            this.externalId = externalId;
            return this;
        }

        public AirportBuilder name(@NotNull String name) {
            this.name = name;
            return this;
        }

        public AirportBuilder city(City city) {
            this.city = city;
            return this;
        }

        public Airport build() {
            return new Airport(id, externalId, name, city);
        }

        public String toString() {
            return "Airport.AirportBuilder(id=" + this.id + ", externalId=" + this.externalId + ", name=" + this.name + ", city=" + this.city + ")";
        }
    }
}

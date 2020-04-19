package com.cheapflights.tickets.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = "comments")
@Entity
@Table(name = "city")
public class City {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Size(min = 1, max = 100, message = "Name must be between 1 and 100 chars.")
    private String name;

    @NotNull(message = "Country must not be null.")
    @Size(min = 1, max = 50, message = "Country must be between 1 and 50 chars.")
    private String country;

    @NotNull
    @Size(min = 1, max = 2000, message = "Description must be between 0 and 2000 chars.")
    private String description;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "city", cascade = CascadeType.REMOVE)
    @JsonIgnore
    private List<Comment> comments;
}

package com.cheapflights.tickets.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "comment")
@Entity(name = "comment")
public class Comment {

    // TODO add constraints
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author", nullable = false)
    private User author;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "city", nullable = false)
    private City city;

    private String text;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    public static CommentBuilder builder() {
        return new CommentBuilder();
    }

    @PrePersist
    private void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.modifiedAt = this.createdAt;
    }

    @PreUpdate
    private void preUpdate() {
        this.modifiedAt = LocalDateTime.now();
    }

    public static class CommentBuilder {
        private Long id;
        private User author;
        private City city;
        private String text;
        private LocalDateTime createdAt;
        private LocalDateTime modifiedAt;

        CommentBuilder() {
        }

        public CommentBuilder id(Long id) {
            this.id = id;
            return this;
        }

        public CommentBuilder author(User author) {
            this.author = author;
            return this;
        }

        public CommentBuilder city(City city) {
            this.city = city;
            return this;
        }

        public CommentBuilder text(String text) {
            this.text = text;
            return this;
        }

        public CommentBuilder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public CommentBuilder modifiedAt(LocalDateTime modifiedAt) {
            this.modifiedAt = modifiedAt;
            return this;
        }

        public Comment build() {
            return new Comment(id, author, city, text, createdAt, modifiedAt);
        }

        public String toString() {
            return "Comment.CommentBuilder(id=" + this.id + ", author=" + this.author + ", city=" + this.city + ", text=" + this.text + ", createdAt=" + this.createdAt + ", modifiedAt=" + this.modifiedAt + ")";
        }
    }
}

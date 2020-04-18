package com.cheapflights.tickets.domain.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Table(name = "comment")
@Entity(name = "comment")
public class Comment {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private User author;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="city", nullable=false)
    private City city;
    private String text;
    private LocalDateTime timestamp;

    public Comment() {

    }

    public static CommentBuilder builder() {
        return new CommentBuilder();
    }

    public static class CommentBuilder {
        private Long id;
        private User author;
        private City city;
        private String text;
        private LocalDateTime timestamp;

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

        public CommentBuilder timestamp(LocalDateTime timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public Comment build() {
            return new Comment(id, author, city, text, timestamp);
        }

        public String toString() {
            return "Comment.CommentBuilder(id=" + this.id + ", author=" + this.author + ", city=" + this.city + ", text=" + this.text + ", timestamp=" + this.timestamp + ")";
        }
    }
}

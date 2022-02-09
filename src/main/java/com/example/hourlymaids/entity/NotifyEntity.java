package com.example.hourlymaids.entity;

import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "NOTIFY")
@Where(clause = "is_deleted = 0")
public class NotifyEntity extends BaseEntity{
    @Id
    @Column(name = "ID")
    @SequenceGenerator(name = "NOTIFY_SEQ", sequenceName = "NOTIFY_SEQ", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "NOTIFY_SEQ")
    private Long id;
    @Column(name = "CONTENT")
    private String content;
    @Column(name = "TITLE")
    private String title;
    @Column(name = "TOPIC")
    private Integer topic;
    @Column(name = "STATUS")
    private Integer status;
    @Column(name = "PUBLISH_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date publishDate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getTopic() {
        return topic;
    }

    public void setTopic(Integer topic) {
        this.topic = topic;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Date getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(Date publishDate) {
        this.publishDate = publishDate;
    }
}

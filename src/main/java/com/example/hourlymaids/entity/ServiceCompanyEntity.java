package com.example.hourlymaids.entity;

import org.hibernate.annotations.Where;

import javax.persistence.*;

@Entity
@Table(name = "SERVICE_COMPANY")
@Where(clause = "is_deleted = 0")
public class ServiceCompanyEntity extends BaseEntity {
    @Id
    @Column(name = "ID")
    @SequenceGenerator(name = "SERVICE_COMPANY_SEQ", sequenceName = "SERVICE_COMPANY_SEQ", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "SERVICE_COMPANY_SEQ")
    private Long id;

    @Column(name = "SERVICE_NAME")
    private String serviceName;
    @Column(name = "PRICE")
    private Long price;
    @Column(name = "NOTE")
    private String note;
    @Column(name = "INTRODUCE")
    private String introduce;
    @Column(name = "ADVANTAGE")
    private String advantages;
    @Column(name = "BANNER")
    private String banner;
    @Column(name = "STATUS")
    private Integer status;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public Long getPrice() {
        return price;
    }

    public void setPrice(Long price) {
        this.price = price;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getBanner() {
        return banner;
    }

    public void setBanner(String banner) {
        this.banner = banner;
    }

    public String getIntroduce() {
        return introduce;
    }

    public void setIntroduce(String introduce) {
        this.introduce = introduce;
    }

    public String getAdvantages() {
        return advantages;
    }

    public void setAdvantages(String advantages) {
        this.advantages = advantages;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}

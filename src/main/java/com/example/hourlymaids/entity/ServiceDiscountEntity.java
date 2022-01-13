package com.example.hourlymaids.entity;

import org.hibernate.annotations.Where;

import javax.persistence.*;

@Entity
@Table(name = "SERVICE_DISCOUNT")
@Where(clause = "is_deleted = 0")
public class ServiceDiscountEntity extends BaseEntity {
    @Id
    @Column(name = "ID")
    @SequenceGenerator(name = "SERVICE_DISCOUNT_SEQ", sequenceName = "SERVICE_DISCOUNT_SEQ", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "SERVICE_DISCOUNT_SEQ")
    private Long id;
    @Column(name = "SERVICE_ID")
    private Long serviceId;
    @Column(name = "DISCOUNT_ID")
    private Long discountId;
    @Column(name = "SALE_PERCENTAGE")
    private Long salePercentage;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getServiceId() {
        return serviceId;
    }

    public void setServiceId(Long serviceId) {
        this.serviceId = serviceId;
    }

    public Long getDiscountId() {
        return discountId;
    }

    public void setDiscountId(Long discountId) {
        this.discountId = discountId;
    }

    public Long getSalePercentage() {
        return salePercentage;
    }

    public void setSalePercentage(Long salePercentage) {
        this.salePercentage = salePercentage;
    }
}

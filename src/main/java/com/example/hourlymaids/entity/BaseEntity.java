/**
 *
 */
package com.example.hourlymaids.entity;

/**
 * @author mba0051
 *
 */
import org.springframework.context.i18n.LocaleContextHolder;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.Locale;

@MappedSuperclass
public abstract class BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Column(name = "CREATED_BY")
    protected Long createdBy;

    @Column(name = "CREATED_DATE")
    protected Date createdDate;

    @Column(name = "UPDATED_BY")
    protected Long updatedBy;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "UPDATED_DATE")
    protected Date updatedDate;

    @Column(name = "IS_DELETED", columnDefinition = "Numeric(2,0) default '0'")
    protected Integer isDeleted;

    public BaseEntity(Long createdBy, Date createdDate, Long updatedBy, Date updatedDate, Integer isDeleted) {
        super();
        this.createdBy = createdBy;
        this.createdDate = createdDate;
        this.updatedBy = updatedBy;
        this.updatedDate = updatedDate;
        this.isDeleted = isDeleted;
    }

    public BaseEntity() {
        super();
    }

    @PrePersist
    protected void onCreate() {
        this.createdDate = new Date();
        this.updatedDate = new Date();
        this.isDeleted = 0;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedDate = new Date();
    }

    /**
     * @return the createdBy
     */
    public Long getCreatedBy() {
        return createdBy;
    }

    /**
     * @param createdBy the createdBy to set
     */
    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }

    /**
     * @return the createdDate
     */
    public Date getCreatedDate() {
        return createdDate;
    }

    /**
     * @param createdDate the createdDate to set
     */
    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    /**
     * @return the updatedBy
     */
    public Long getUpdatedBy() {
        return updatedBy;
    }

    /**
     * @param updatedBy the updatedBy to set
     */
    public void setUpdatedBy(Long updatedBy) {
        this.updatedBy = updatedBy;
    }

    /**
     * @return the updatedDate
     */
    public Date getUpdatedDate() {
        return updatedDate;
    }

    /**
     * @param updatedDate the updatedDate to set
     */
    public void setUpdatedDate(Date updatedDate) {
        this.updatedDate = updatedDate;
    }

    /**
     * @return the isDeleted
     */
    public Integer getIsDeleted() {
        return isDeleted;
    }

    /**
     * @param isDeleted the isDeleted to set
     */
    public void setIsDeleted(Integer isDeleted) {
        this.isDeleted = isDeleted;
    }

    /**
     * @return the serialversionuid
     */
    public static long getSerialversionuid() {
        return serialVersionUID;
    }

    /**
     * Gets lang.
     *
     * @return the current locale
     */
    protected static String getLang() {
        Locale currentLocale = LocaleContextHolder.getLocale();
        String localeText = currentLocale.getLanguage();
        return localeText;
    }

}
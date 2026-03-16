package com.ycyw.userservice.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(
        name = "vehicles",
        indexes = {
                @Index(name = "idx_vehicle_agency", columnList = "agency_id"),
                @Index(name = "idx_vehicle_acriss", columnList = "acriss_code")
        }
)
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "acriss_code", nullable = false, length = 20)
    private String acrissCode;

    @Column(nullable = false)
    private String brand;

    @Column(nullable = false)
    private String model;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VehicleStatus status;

    @Column(name = "price_per_day", nullable = false)
    private double pricePerDay;

    @ManyToOne(optional = false)
    @JoinColumn(name = "agency_id", nullable = false)
    private Agency agency;

    public Vehicle() {}

    public Long getId() {
        return id;
    }

    public String getAcrissCode() {
        return acrissCode;
    }

    public void setAcrissCode(String acrissCode) {
        this.acrissCode = acrissCode;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public VehicleStatus getStatus() {
        return status;
    }

    public void setStatus(VehicleStatus status) {
        this.status = status;
    }

    public double getPricePerDay() {
        return pricePerDay;
    }

    public void setPricePerDay(double pricePerDay) {
        this.pricePerDay = pricePerDay;
    }

    public Agency getAgency() {
        return agency;
    }

    public void setAgency(Agency agency) {
        this.agency = agency;
    }
}


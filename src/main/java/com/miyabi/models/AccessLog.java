package com.miyabi.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "access_log")
public class AccessLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "access_id")
    private Integer idAccess;

    @Column(name = "access_date", insertable = false, updatable = false)
    private LocalDateTime accessDate;

    @Column(name = "access_ip", length = 50)
    private String ipAccess;

    @Column(name = "user_type", length = 20)
    private String userType;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "guest_id")
    private Guest guest;

    public AccessLog() {}

    // ==========================================
    // GETTERS Y SETTERS
    // ==========================================

    public Integer getIdAccess() {
        return idAccess;
    }

    public void setIdAccess(Integer idAccess) {
        this.idAccess = idAccess;
    }

    public LocalDateTime getAccessDate() {
        return accessDate;
    }

    public void setAccessDate(LocalDateTime accessDate) {
        this.accessDate = accessDate;
    }

    public String getIpAccess() {
        return ipAccess;
    }

    public void setIpAccess(String ipAccess) {
        this.ipAccess = ipAccess;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Guest getGuest() {
        return guest;
    }

    public void setGuest(Guest guest) {
        this.guest = guest;
    }
}
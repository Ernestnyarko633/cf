package com.completefarmer.neobank.accountservice.account;

import jakarta.persistence.*;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AccountEntityListener {
    @PrePersist
    public void prePersist(Object o) {
        log.info("Creating account: {}", o);
    }

    @PreUpdate
    public void preUpdate(Object o) {

    }

    @PreRemove
    public void preRemove(Object o) {

    }

    @PostLoad
    public void postLoad(Object o) {

    }

    @PostRemove
    public void postRemove(Object o) {

    }

    @PostUpdate
    public void postUpdate(Object o) {

    }

    @PostPersist
    public void postPersist(Object o) {

    }
}

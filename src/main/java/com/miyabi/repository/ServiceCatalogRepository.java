package com.miyabi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.miyabi.models.ServiceCatalog;
import java.util.List;

public interface ServiceCatalogRepository extends JpaRepository<ServiceCatalog, Integer> {
    List<ServiceCatalog> findByAvailable(Integer available);
}
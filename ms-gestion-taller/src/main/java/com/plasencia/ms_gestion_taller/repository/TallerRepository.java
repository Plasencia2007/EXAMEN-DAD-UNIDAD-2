package com.plasencia.ms_gestion_taller.repository;

import com.plasencia.ms_gestion_taller.entity.Taller;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TallerRepository extends JpaRepository<Taller, Long> {
}

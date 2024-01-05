package com.korea.project2_team4.Repository;

import com.korea.project2_team4.Model.Entity.DmPage;
import org.hibernate.type.descriptor.converter.spi.JpaAttributeConverter;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DmPageRepository extends JpaRepository<DmPage, Long> {
}

package com.korea.project2_team4.Repository;

import com.korea.project2_team4.Model.Entity.RecentSearch;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RecentSearchRepository extends JpaRepository<RecentSearch, Long> {
    List<RecentSearch> findTop5ByOrderBySearchDateDesc();
}

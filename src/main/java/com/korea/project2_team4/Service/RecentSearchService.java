package com.korea.project2_team4.Service;

import com.korea.project2_team4.Model.Entity.RecentSearch;
import com.korea.project2_team4.Repository.RecentSearchRepository;
import lombok.Builder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Builder
public class RecentSearchService {
    private final RecentSearchRepository recentSearchRepository;

    public List<String> getRecentSearchKeywords() {
        List<RecentSearch> recentSearchList = recentSearchRepository.findTop5ByOrderBySearchDateDesc();
        return recentSearchList.stream().map(RecentSearch::getRecentKeyword).collect(Collectors.toList());
    }

    public void saveRecentSearch(String recentKeyword) {
        RecentSearch recentSearch = new RecentSearch();
        recentSearch.setRecentKeyword(recentKeyword);
        recentSearch.setSearchDate(LocalDateTime.now());

        recentSearchRepository.save(recentSearch);
    }

}

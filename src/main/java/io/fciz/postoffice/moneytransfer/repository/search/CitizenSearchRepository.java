package io.fciz.postoffice.moneytransfer.repository.search;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import io.fciz.postoffice.moneytransfer.domain.Citizen;
import io.fciz.postoffice.moneytransfer.repository.CitizenRepository;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.scheduling.annotation.Async;

/**
 * Spring Data Elasticsearch repository for the {@link Citizen} entity.
 */
public interface CitizenSearchRepository extends ElasticsearchRepository<Citizen, Long>, CitizenSearchRepositoryInternal {}

interface CitizenSearchRepositoryInternal {
    Page<Citizen> search(String query, Pageable pageable);

    Page<Citizen> search(Query query);

    @Async
    void index(Citizen entity);

    @Async
    void deleteFromIndexById(Long id);
}

class CitizenSearchRepositoryInternalImpl implements CitizenSearchRepositoryInternal {

    private final ElasticsearchTemplate elasticsearchTemplate;
    private final CitizenRepository repository;

    CitizenSearchRepositoryInternalImpl(ElasticsearchTemplate elasticsearchTemplate, CitizenRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Page<Citizen> search(String query, Pageable pageable) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        return search(nativeQuery.setPageable(pageable));
    }

    @Override
    public Page<Citizen> search(Query query) {
        SearchHits<Citizen> searchHits = elasticsearchTemplate.search(query, Citizen.class);
        List<Citizen> hits = searchHits.map(SearchHit::getContent).stream().toList();
        return new PageImpl<>(hits, query.getPageable(), searchHits.getTotalHits());
    }

    @Override
    public void index(Citizen entity) {
        repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }

    @Override
    public void deleteFromIndexById(Long id) {
        elasticsearchTemplate.delete(String.valueOf(id), Citizen.class);
    }
}

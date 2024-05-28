package io.fciz.postoffice.moneytransfer.repository.search;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import io.fciz.postoffice.moneytransfer.domain.PostOffice;
import io.fciz.postoffice.moneytransfer.repository.PostOfficeRepository;
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
 * Spring Data Elasticsearch repository for the {@link PostOffice} entity.
 */
public interface PostOfficeSearchRepository extends ElasticsearchRepository<PostOffice, Long>, PostOfficeSearchRepositoryInternal {}

interface PostOfficeSearchRepositoryInternal {
    Page<PostOffice> search(String query, Pageable pageable);

    Page<PostOffice> search(Query query);

    @Async
    void index(PostOffice entity);

    @Async
    void deleteFromIndexById(Long id);
}

class PostOfficeSearchRepositoryInternalImpl implements PostOfficeSearchRepositoryInternal {

    private final ElasticsearchTemplate elasticsearchTemplate;
    private final PostOfficeRepository repository;

    PostOfficeSearchRepositoryInternalImpl(ElasticsearchTemplate elasticsearchTemplate, PostOfficeRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Page<PostOffice> search(String query, Pageable pageable) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        return search(nativeQuery.setPageable(pageable));
    }

    @Override
    public Page<PostOffice> search(Query query) {
        SearchHits<PostOffice> searchHits = elasticsearchTemplate.search(query, PostOffice.class);
        List<PostOffice> hits = searchHits.map(SearchHit::getContent).stream().toList();
        return new PageImpl<>(hits, query.getPageable(), searchHits.getTotalHits());
    }

    @Override
    public void index(PostOffice entity) {
        repository.findById(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }

    @Override
    public void deleteFromIndexById(Long id) {
        elasticsearchTemplate.delete(String.valueOf(id), PostOffice.class);
    }
}

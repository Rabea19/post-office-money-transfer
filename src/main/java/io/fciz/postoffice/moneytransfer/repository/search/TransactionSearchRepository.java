package io.fciz.postoffice.moneytransfer.repository.search;

import co.elastic.clients.elasticsearch._types.query_dsl.QueryStringQuery;
import io.fciz.postoffice.moneytransfer.domain.Transaction;
import io.fciz.postoffice.moneytransfer.repository.TransactionRepository;
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
 * Spring Data Elasticsearch repository for the {@link Transaction} entity.
 */
public interface TransactionSearchRepository extends ElasticsearchRepository<Transaction, Long>, TransactionSearchRepositoryInternal {}

interface TransactionSearchRepositoryInternal {
    Page<Transaction> search(String query, Pageable pageable);

    Page<Transaction> search(Query query);

    @Async
    void index(Transaction entity);

    @Async
    void deleteFromIndexById(Long id);
}

class TransactionSearchRepositoryInternalImpl implements TransactionSearchRepositoryInternal {

    private final ElasticsearchTemplate elasticsearchTemplate;
    private final TransactionRepository repository;

    TransactionSearchRepositoryInternalImpl(ElasticsearchTemplate elasticsearchTemplate, TransactionRepository repository) {
        this.elasticsearchTemplate = elasticsearchTemplate;
        this.repository = repository;
    }

    @Override
    public Page<Transaction> search(String query, Pageable pageable) {
        NativeQuery nativeQuery = new NativeQuery(QueryStringQuery.of(qs -> qs.query(query))._toQuery());
        return search(nativeQuery.setPageable(pageable));
    }

    @Override
    public Page<Transaction> search(Query query) {
        SearchHits<Transaction> searchHits = elasticsearchTemplate.search(query, Transaction.class);
        List<Transaction> hits = searchHits.map(SearchHit::getContent).stream().toList();
        return new PageImpl<>(hits, query.getPageable(), searchHits.getTotalHits());
    }

    @Override
    public void index(Transaction entity) {
        repository.findOneWithEagerRelationships(entity.getId()).ifPresent(elasticsearchTemplate::save);
    }

    @Override
    public void deleteFromIndexById(Long id) {
        elasticsearchTemplate.delete(String.valueOf(id), Transaction.class);
    }
}

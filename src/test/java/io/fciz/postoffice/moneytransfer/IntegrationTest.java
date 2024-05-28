package io.fciz.postoffice.moneytransfer;

import io.fciz.postoffice.moneytransfer.config.AsyncSyncConfiguration;
import io.fciz.postoffice.moneytransfer.config.EmbeddedElasticsearch;
import io.fciz.postoffice.moneytransfer.config.EmbeddedSQL;
import io.fciz.postoffice.moneytransfer.config.JacksonConfiguration;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Base composite annotation for integration tests.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@SpringBootTest(classes = { PostOfficeMoneyTransferApp.class, JacksonConfiguration.class, AsyncSyncConfiguration.class })
@EmbeddedElasticsearch
@EmbeddedSQL
public @interface IntegrationTest {
}

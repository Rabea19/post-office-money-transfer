package io.fciz.postoffice.moneytransfer.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class PostOfficeCriteriaTest {

    @Test
    void newPostOfficeCriteriaHasAllFiltersNullTest() {
        var postOfficeCriteria = new PostOfficeCriteria();
        assertThat(postOfficeCriteria).is(criteriaFiltersAre(filter -> filter == null));
    }

    @Test
    void postOfficeCriteriaFluentMethodsCreatesFiltersTest() {
        var postOfficeCriteria = new PostOfficeCriteria();

        setAllFilters(postOfficeCriteria);

        assertThat(postOfficeCriteria).is(criteriaFiltersAre(filter -> filter != null));
    }

    @Test
    void postOfficeCriteriaCopyCreatesNullFilterTest() {
        var postOfficeCriteria = new PostOfficeCriteria();
        var copy = postOfficeCriteria.copy();

        assertThat(postOfficeCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(filter -> filter == null)),
            criteria -> assertThat(criteria).isEqualTo(postOfficeCriteria)
        );
    }

    @Test
    void postOfficeCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var postOfficeCriteria = new PostOfficeCriteria();
        setAllFilters(postOfficeCriteria);

        var copy = postOfficeCriteria.copy();

        assertThat(postOfficeCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(filter -> filter != null)),
            criteria -> assertThat(criteria).isEqualTo(postOfficeCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var postOfficeCriteria = new PostOfficeCriteria();

        assertThat(postOfficeCriteria).hasToString("PostOfficeCriteria{}");
    }

    private static void setAllFilters(PostOfficeCriteria postOfficeCriteria) {
        postOfficeCriteria.id();
        postOfficeCriteria.name();
        postOfficeCriteria.streetAddress();
        postOfficeCriteria.city();
        postOfficeCriteria.state();
        postOfficeCriteria.postalCode();
        postOfficeCriteria.distinct();
    }

    private static Condition<PostOfficeCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getName()) &&
                condition.apply(criteria.getStreetAddress()) &&
                condition.apply(criteria.getCity()) &&
                condition.apply(criteria.getState()) &&
                condition.apply(criteria.getPostalCode()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<PostOfficeCriteria> copyFiltersAre(PostOfficeCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getName(), copy.getName()) &&
                condition.apply(criteria.getStreetAddress(), copy.getStreetAddress()) &&
                condition.apply(criteria.getCity(), copy.getCity()) &&
                condition.apply(criteria.getState(), copy.getState()) &&
                condition.apply(criteria.getPostalCode(), copy.getPostalCode()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}

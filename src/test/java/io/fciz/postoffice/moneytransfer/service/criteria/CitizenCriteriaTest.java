package io.fciz.postoffice.moneytransfer.service.criteria;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.function.BiFunction;
import java.util.function.Function;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;

class CitizenCriteriaTest {

    @Test
    void newCitizenCriteriaHasAllFiltersNullTest() {
        var citizenCriteria = new CitizenCriteria();
        assertThat(citizenCriteria).is(criteriaFiltersAre(filter -> filter == null));
    }

    @Test
    void citizenCriteriaFluentMethodsCreatesFiltersTest() {
        var citizenCriteria = new CitizenCriteria();

        setAllFilters(citizenCriteria);

        assertThat(citizenCriteria).is(criteriaFiltersAre(filter -> filter != null));
    }

    @Test
    void citizenCriteriaCopyCreatesNullFilterTest() {
        var citizenCriteria = new CitizenCriteria();
        var copy = citizenCriteria.copy();

        assertThat(citizenCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(filter -> filter == null)),
            criteria -> assertThat(criteria).isEqualTo(citizenCriteria)
        );
    }

    @Test
    void citizenCriteriaCopyDuplicatesEveryExistingFilterTest() {
        var citizenCriteria = new CitizenCriteria();
        setAllFilters(citizenCriteria);

        var copy = citizenCriteria.copy();

        assertThat(citizenCriteria).satisfies(
            criteria ->
                assertThat(criteria).is(
                    copyFiltersAre(copy, (a, b) -> (a == null || a instanceof Boolean) ? a == b : (a != b && a.equals(b)))
                ),
            criteria -> assertThat(criteria).isEqualTo(copy),
            criteria -> assertThat(criteria).hasSameHashCodeAs(copy)
        );

        assertThat(copy).satisfies(
            criteria -> assertThat(criteria).is(criteriaFiltersAre(filter -> filter != null)),
            criteria -> assertThat(criteria).isEqualTo(citizenCriteria)
        );
    }

    @Test
    void toStringVerifier() {
        var citizenCriteria = new CitizenCriteria();

        assertThat(citizenCriteria).hasToString("CitizenCriteria{}");
    }

    private static void setAllFilters(CitizenCriteria citizenCriteria) {
        citizenCriteria.id();
        citizenCriteria.nationalId();
        citizenCriteria.firstName();
        citizenCriteria.lastName();
        citizenCriteria.phoneNumber();
        citizenCriteria.distinct();
    }

    private static Condition<CitizenCriteria> criteriaFiltersAre(Function<Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId()) &&
                condition.apply(criteria.getNationalId()) &&
                condition.apply(criteria.getFirstName()) &&
                condition.apply(criteria.getLastName()) &&
                condition.apply(criteria.getPhoneNumber()) &&
                condition.apply(criteria.getDistinct()),
            "every filter matches"
        );
    }

    private static Condition<CitizenCriteria> copyFiltersAre(CitizenCriteria copy, BiFunction<Object, Object, Boolean> condition) {
        return new Condition<>(
            criteria ->
                condition.apply(criteria.getId(), copy.getId()) &&
                condition.apply(criteria.getNationalId(), copy.getNationalId()) &&
                condition.apply(criteria.getFirstName(), copy.getFirstName()) &&
                condition.apply(criteria.getLastName(), copy.getLastName()) &&
                condition.apply(criteria.getPhoneNumber(), copy.getPhoneNumber()) &&
                condition.apply(criteria.getDistinct(), copy.getDistinct()),
            "every filter matches"
        );
    }
}

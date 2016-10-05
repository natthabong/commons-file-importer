package gec.scf.file.converter;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import gec.scf.file.example.domain.SponsorDocument;
import gec.scf.file.validator.ConditionMatching;
import gec.scf.file.validator.ConditionMatchingFactory;
import gec.scf.file.validator.ConditionType;
import gec.scf.file.validator.DataCondition;
import gec.scf.file.validator.DataInvalidException;
import gec.scf.file.validator.DataValidator;
import gec.scf.file.validator.exception.ConditionMismatchException;

public class DataValidatorTest {

	@InjectMocks
	private DataValidator dataValidator;

	@Mock
	private ConditionMatchingFactory conditionMatchingFactory;

	private Date yesterday;

	private Date tomorrow;

	@Before
	public void setUp() {

		dataValidator = new DataValidator();
		dataValidator.setConditionMatchingFactory(conditionMatchingFactory);

		MockitoAnnotations.initMocks(this);

		Calendar yesterday = Calendar.getInstance(Locale.US);
		yesterday.set(2016, Calendar.AUGUST, 15, 0, 0, 0);
		this.yesterday = yesterday.getTime();

		Calendar tomorrow = Calendar.getInstance(Locale.US);
		tomorrow.set(2016, Calendar.AUGUST, 17, 0, 0, 0);
		this.tomorrow = tomorrow.getTime();
	}

	@Test
	public void given_a_condition_config_to_check_sponsor_payment_date_must_less_than_or_equal_current_day_when_validate_a_document_which_has_sponsor_payment_date_on_tomorrow_should_valid()
			throws DataInvalidException {

		// Arrange
		DataCondition sponsorPaymentDateLessThanOrEqualToday = new DataCondition();

		when(conditionMatchingFactory.create(sponsorPaymentDateLessThanOrEqualToday,
				SponsorDocument.class)).thenReturn(new DoNothingCondition());
		dataValidator.setConditions(Arrays
				.asList(new DataCondition[] { sponsorPaymentDateLessThanOrEqualToday }));

		SponsorDocument document = mock(SponsorDocument.class);
		when(document.getSponsorPaymentDate()).thenReturn(tomorrow);

		// Actual
		dataValidator.validate(document, SponsorDocument.class);

	}

	@Test(expected = DataInvalidException.class)
	public void given_a_condition_config_to_check_sponsor_payment_date_must_less_than_or_equal_current_day_when_validate_a_document_which_has_sponsor_payment_date_on_yesterday_should_throw_InvalidDataException()
			throws DataInvalidException {

		// Arrange
		DataCondition sponsorPaymentDateLessThanOrEqualToday = new DataCondition();

		dataValidator.setConditions(Arrays
				.asList(new DataCondition[] { sponsorPaymentDateLessThanOrEqualToday }));

		when(conditionMatchingFactory.create(sponsorPaymentDateLessThanOrEqualToday,
				SponsorDocument.class))
						.thenReturn(new ConditionMatching<SponsorDocument>() {

							@Override
							public void match(SponsorDocument object)
									throws ConditionMismatchException {
								throw new ConditionMismatchException();

							}

							@Override
							public ConditionType getConditionType() {
								return null;
							}
						});

		SponsorDocument document = mock(SponsorDocument.class);
		when(document.getSponsorPaymentDate()).thenReturn(yesterday);

		// Actual
		dataValidator.validate(document, SponsorDocument.class);

	}

	private final class DoNothingCondition implements ConditionMatching<SponsorDocument> {
		@Override
		public void match(SponsorDocument object) throws ConditionMismatchException {

		}

		@Override
		public ConditionType getConditionType() {
			return null;
		}
	}
}

package cz.fi.muni.pa165.currency;

import java.math.BigDecimal;
import java.util.Currency;
import org.junit.Test;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import static org.assertj.core.api.Assertions.*;


public class CurrencyConvertorImplTest {

    private static final Currency CZK = Currency.getInstance("CZK");
    private static final Currency EUR = Currency.getInstance("EUR");
    private CurrencyConvertor currencyConvertor;

    // Definuje namockovanie objektu oznaceneho anotaciou MOCK
    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    // Definuje mockovani objekt
    @Mock
    private ExchangeRateTable exchangeRateTable;

    @Before
    public void init() {
        currencyConvertor = new CurrencyConvertorImpl(exchangeRateTable);
    }

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void testConvert() throws ExternalServiceFailureException {
        // Definujem specificku hodnotu ktoru pozadujem od funkcie mockovaneho objektu
        when(exchangeRateTable.getExchangeRate(EUR, CZK))
                .thenReturn(new BigDecimal("0.1"));

        assertThat(currencyConvertor.convert(EUR, CZK, new BigDecimal("10.050")))
                .isEqualTo(new BigDecimal("1.00"));
        assertThat(currencyConvertor.convert(EUR, CZK, new BigDecimal("10.051")))
                .isEqualTo(new BigDecimal("1.01"));
        assertThat(currencyConvertor.convert(EUR, CZK, new BigDecimal("10.149")))
                .isEqualTo(new BigDecimal("1.01"));
        assertThat(currencyConvertor.convert(EUR, CZK, new BigDecimal("10.150")))
                .isEqualTo(new BigDecimal("1.02"));
    }

    @Test
    public void testConvertWithNullSourceCurrency() {
        expectedException.expect(IllegalArgumentException.class);
        currencyConvertor.convert(null, EUR, BigDecimal.ZERO);
    }

    @Test
    public void testConvertWithNullTargetCurrency() {
        expectedException.expect(IllegalArgumentException.class);
        currencyConvertor.convert(CZK, null, BigDecimal.ZERO);
    }

    @Test
    public void testConvertWithNullSourceAmount() {
        expectedException.expect(IllegalArgumentException.class);
        currencyConvertor.convert(EUR, CZK, null);
    }

    @Test
    public void testConvertWithUnknownCurrency() throws ExternalServiceFailureException {
        when(exchangeRateTable.getExchangeRate(CZK, EUR)).thenReturn(null);
        expectedException.expect(UnknownExchangeRateException.class);
        currencyConvertor.convert(CZK, EUR, BigDecimal.ONE);
    }

    @Test
    public void testConvertWithExternalServiceFailure() throws ExternalServiceFailureException {
        when(exchangeRateTable.getExchangeRate(EUR, CZK))
                .thenThrow(UnknownExchangeRateException.class);
        expectedException.expect(UnknownExchangeRateException.class);
        currencyConvertor.convert(EUR, CZK, BigDecimal.ONE);
    }
}

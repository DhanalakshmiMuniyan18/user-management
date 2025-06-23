package com.usermanagement.payment.factory;

import com.usermanagement.payment.strategy.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

/**
 * @author Saravanamuthukumar S
 */
@ExtendWith(MockitoExtension.class)
class PaymentStrategyFactoryTest {

    @Mock
    private CreditCardPaymentStrategy creditCardStrategy;

    @Mock
    private PayPalPaymentStrategy payPalStrategy;

    @Mock
    private BankTransferPaymentStrategy bankTransferStrategy;

    @Mock
    private CryptoPaymentStrategy cryptoStrategy;

    @Mock
    private DigitalWalletPaymentStrategy digitalWalletStrategy;

    private PaymentStrategyFactory factory;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        // Mock strategy names
        when(creditCardStrategy.getStrategyName()).thenReturn("CREDIT_CARD");
        when(payPalStrategy.getStrategyName()).thenReturn("PAYPAL");
        when(bankTransferStrategy.getStrategyName()).thenReturn("BANK_TRANSFER");
        when(cryptoStrategy.getStrategyName()).thenReturn("CRYPTO");
        when(digitalWalletStrategy.getStrategyName()).thenReturn("DIGITAL_WALLET");
        
        factory = new PaymentStrategyFactory(java.util.List.of(
            creditCardStrategy, payPalStrategy, bankTransferStrategy, cryptoStrategy, digitalWalletStrategy
        ));
    }

    @Test
    void testGetStrategy_CreditCard() {
        assertEquals(creditCardStrategy, factory.getStrategy("CREDIT_CARD"));
    }

    @Test
    void testGetStrategy_PayPal() {
        assertEquals(payPalStrategy, factory.getStrategy("PAYPAL"));
    }

    @Test
    void testGetStrategy_BankTransfer() {
        assertEquals(bankTransferStrategy, factory.getStrategy("BANK_TRANSFER"));
    }

    @Test
    void testGetStrategy_Crypto() {
        assertEquals(cryptoStrategy, factory.getStrategy("CRYPTO"));
    }

    @Test
    void testGetStrategy_DigitalWallet() {
        assertEquals(digitalWalletStrategy, factory.getStrategy("DIGITAL_WALLET"));
    }

    @Test
    void testGetStrategy_InvalidPaymentMethod() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> factory.getStrategy("INVALID_METHOD"));
        
        assertTrue(exception.getMessage().contains("Unsupported payment type"));
    }

    @Test
    void testGetStrategy_NullPaymentMethod() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> factory.getStrategy(null));
        
        assertTrue(exception.getMessage().contains("Unsupported payment type"));
    }
} 
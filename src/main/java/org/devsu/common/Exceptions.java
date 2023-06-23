package org.devsu.common;

public class Exceptions {

    public static class AccountNotFoundException extends RuntimeException {
        public AccountNotFoundException(String message) {
            super(message);
        }
    }

    public static class ExceedsDailyWithdrawalLimitException extends RuntimeException {
        public ExceedsDailyWithdrawalLimitException(String message) {
            super(message);
        }
    }

    public static class BalanceCalculationException extends RuntimeException {
        public BalanceCalculationException(String message) {
            super(message);
        }
    }

    public static class RecordNotFoundException extends RuntimeException {
        public RecordNotFoundException(String message) {
            super(message);
        }
    }

    public static class UnauthorizedException extends RuntimeException {
        public UnauthorizedException(String message) {
            super(message);
        }
    }

}

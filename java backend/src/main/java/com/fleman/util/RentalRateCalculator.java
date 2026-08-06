package com.fleman.util;

import com.fleman.entity.CarType;

// Shared by BookingServiceImpl (booking-time estimate) and
// StaffServiceImpl (the actual return invoice) so both price a rental the
// same way: pack whole months first, then whole weeks out of what's left,
// then bill the remaining days at the daily rate — rather than always
// multiplying every day by the daily rate regardless of length of stay.
// Mirrors the frontend's BookingContext.jsx totals calculation.
public final class RentalRateCalculator {

    private RentalRateCalculator() {}

    public static double calculateRentalAmount(CarType carType, int days) {
        double dailyRate = carType.getDailyRate() != null ? carType.getDailyRate() : 0.0;
        double weeklyRate = carType.getWeeklyRate() != null ? carType.getWeeklyRate() : 0.0;
        double monthlyRate = carType.getMonthlyRate() != null ? carType.getMonthlyRate() : 0.0;

        int remainingDays = days;
        double amount = 0.0;

        if (monthlyRate > 0 && remainingDays >= 30) {
            int months = remainingDays / 30;
            amount += months * monthlyRate;
            remainingDays %= 30;
        }
        if (weeklyRate > 0 && remainingDays >= 7) {
            int weeks = remainingDays / 7;
            amount += weeks * weeklyRate;
            remainingDays %= 7;
        }
        amount += remainingDays * dailyRate;

        return amount;
    }
}

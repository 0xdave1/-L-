package com.example.data.model

import java.text.NumberFormat
import java.util.Locale

data class CurrencyConfig(
    val code: String,
    val symbol: String,
    val name: String,
    val minorUnitsPerMajor: Long = 100L
) {
    fun formatAmount(minorUnits: Long?): String {
        if (minorUnits == null) return "${symbol}0"
        val major = minorUnits / minorUnitsPerMajor
        val minor = Math.abs(minorUnits % minorUnitsPerMajor)
        val formattedMajor = NumberFormat.getNumberInstance(Locale.US).format(major)
        return if (minor == 0L) {
            "$symbol$formattedMajor"
        } else {
            "$symbol$formattedMajor.${String.format(Locale.US, "%02d", minor)}"
        }
    }

    fun parseToMinorUnits(input: String): Long? {
        val clean = input.replace(symbol, "")
            .replace(",", "")
            .trim()
        if (clean.isBlank()) return null
        return try {
            val amount = clean.toDouble()
            if (amount < 0) null else (amount * minorUnitsPerMajor).toLong()
        } catch (e: Exception) {
            null
        }
    }
}

object Currencies {
    val NGN = CurrencyConfig(code = "NGN", symbol = "₦", name = "Nigerian Naira")
    val GHS = CurrencyConfig(code = "GHS", symbol = "GH₵", name = "Ghanaian Cedi")
    val KES = CurrencyConfig(code = "KES", symbol = "KSh", name = "Kenyan Shilling")
    val ZAR = CurrencyConfig(code = "ZAR", symbol = "R", name = "South African Rand")
    val USD = CurrencyConfig(code = "USD", symbol = "$", name = "US Dollar")
    val GBP = CurrencyConfig(code = "GBP", symbol = "£", name = "British Pound")
    val EUR = CurrencyConfig(code = "EUR", symbol = "€", name = "Euro")

    val supported = listOf(NGN, GHS, KES, ZAR, USD, GBP, EUR)

    fun get(code: String?): CurrencyConfig {
        return supported.find { it.code.equals(code, ignoreCase = true) } ?: NGN
    }
}

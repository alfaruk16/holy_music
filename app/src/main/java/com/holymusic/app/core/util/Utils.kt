package com.holymusic.app.core.util

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.chrono.HijrahDate
import java.time.format.DateTimeFormatter

object Utils {
    @RequiresApi(Build.VERSION_CODES.O)
    private fun arabicDate(diff: Int = 0): String {
        val now = LocalDateTime.now()
        val dt = LocalDate.of(now.year, now.monthValue, now.dayOfMonth).plusDays((diff - 1).toLong())
        val hijriDate = HijrahDate.from(dt)
        val formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy")
        return formatter.format(hijriDate)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun bengaliArabicDate(diff: Int = 0): String {
        val date = Regex("[^A-Za-z0-9 ]").replace(arabicDate(diff), "")
        val arr = date.split(" ")
        val day = arr[0]
        var month = ""
        for(i in 1..<arr.size - 1){
            month += arr[i]
        }
        val year = arr[arr.size - 1]
        return englishToBengaliArabicDate(day, month, year)
    }

    val nagad = buildAnnotatedString {
        withStyle(style = SpanStyle(Color(0xFFEA1D25))) {
            append("N")
        }
        append("agad")
    }

    private val arabicMonthName: HashMap<String, String> = hashMapOf(
        "Muharram" to "মুহররম", "Safar" to "সফর", "RabiI" to "রবিউল আউয়াল",
        "RabiII" to "রবিউছ ছানি", "JumadaI" to "জামাদিউল আউয়াল",
        "JumadaII" to "জামাদিউছ ছানি", "Rajab" to "রজব",
        "Shaban" to "শা’বান", "Ramadan" to "রামাজান", "Shawwal" to "শাওয়াল",
        "DhulQidah" to "জুল কাইদাহ", "DhulHijjah" to "জুল হিজ্জাহ"
    )

    private fun englishToBengaliArabicDate(day: String, month: String, year: String): String {
        return bengaliNumberString(day) + " " + arabicMonthName[month] + " " +
                bengaliNumberString(year)
    }

    private val bengaliDayName: HashMap<Int, String> = hashMapOf(
        6 to "শনিবার", 7 to "রবিবার", 1 to "সোমবার", 2 to "মঙ্গলবার", 3 to "বুধবার",
        4 to "বৃহস্পতিবার", 5 to "শুক্রবার"
    )

    private val bengaliMonthName: HashMap<Int, String> = hashMapOf(
        1 to "জানুয়ারি", 2 to "ফেব্রুয়ারি", 3 to "মার্চ", 4 to "এপ্রিল", 5 to "মে",
        6 to "জুন", 7 to "জুলাই", 8 to "আগস্ট", 9 to "সেপ্টেম্বর", 10 to "অক্টোবর",
        11 to "নভেম্বর", 12 to "ডিসেম্বর"
    )

    private val bengaliNumberChar: HashMap<Char, String> = hashMapOf(
        '1' to "১", '2' to "২", '3' to "৩", '4' to "৪", '5' to "৫",
        '6' to "৬", '7' to "৭", '8' to "৮", '9' to "৯", '0' to "০"
    )

    private fun bengaliNumberString(number: String): String {
        var bangla = ""
        for (ch in number) {
            bangla += bengaliNumberChar[ch]
        }
        return bangla
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getBengaliDate(diff: Int = 0): String {
        val now = LocalDateTime.now().plusDays(diff.toLong())
        return bengaliDayName[now.dayOfWeek.value] + ", " + bengaliMonthName[now.monthValue] + " " + bengaliNumberString(
            now.dayOfMonth.toString()
        ) + ", " + bengaliNumberString(now.year.toString())
    }

    val banglalink = buildAnnotatedString {
        withStyle(style = SpanStyle(Color(0xFFF16522))) {
            append("B")
        }
        append("anglalink")
    }
    val bKash = buildAnnotatedString {
        append("b")
        withStyle(style = SpanStyle(Color(0xFFE2136E))) {
            append("K")
        }
        append("ash")
    }
    val mfs = buildAnnotatedString {
        append("b")
        withStyle(style = SpanStyle(Color(0xFFE2136E))) {
            append("K")
        }
        append("ash/ ")
        withStyle(style = SpanStyle(Color(0xFFEA1D25))) {
            append("N")
        }
        append("agad/ ")
        withStyle(style = SpanStyle(Color(0xFF89288F))) {
            append("R")
        }
        append("ocket/ ")
        withStyle(style = SpanStyle(Color(0xFF295CAB))) {
            append("B")
        }
        append("ank")
    }
    val amarPay = buildAnnotatedString {
        append("b")
        withStyle(style = SpanStyle(Color(0xFFE2136E))) {
            append("K")
        }
        append("ash/ ")
        withStyle(style = SpanStyle(Color(0xFFEA1D25))) {
            append("N")
        }
        append("agad/ ")
        withStyle(style = SpanStyle(Color(0xFF89288F))) {
            append("R")
        }
        append("ocket/ ")
        withStyle(style = SpanStyle(Color(0xFFEB9200))) {
            append("B")
        }
        append("ank")
    }

    val robi = buildAnnotatedString {
        withStyle(style = SpanStyle(Color(0xFFE3352D))) {
            append("R")
        }
        append("obi/")
        withStyle(style = SpanStyle(Color(0xFFEC1C24))) {
            append("A")
        }
        append("irtel")
    }
}
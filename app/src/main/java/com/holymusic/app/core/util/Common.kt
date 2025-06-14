package com.holymusic.app.core.util

import androidx.compose.ui.graphics.Color
import com.holymusic.app.core.theme.BackGroundDark
import com.holymusic.app.core.theme.GradientColor1
import com.holymusic.app.core.theme.GradientColor2
import com.holymusic.app.core.theme.Primary

object Common {

    fun isValidMobile(mobile: String): Boolean {

        if (mobile.isNotEmpty() && mobile.length == 11 && !mobile.startsWith(
                "011"
            ) && !mobile.startsWith("012") && mobile.startsWith("01")
        ) {
            return true
        }

        return false
    }

    fun getTelcoProvider(mobile: String): String {
        if (mobile.startsWith("017") || mobile.startsWith("013")) {
            return "GP"
        } else if (mobile.startsWith("019") || mobile.startsWith("014")) {
            return "BL"
        } else if (mobile.startsWith("018") || mobile.startsWith("016")) {
            return "ROBI"
        } else if (mobile.startsWith("015")) {
            return "TT"
        }
        return ""
    }

    fun getImageColor(id: String?): Color {
        println(id)
        if (id != null) {
            val regex = "-?[0-9]+(\\.[0-9]+)?".toRegex()

            if (id.matches(regex)) {
                when (id.toInt() % 10) {
                    0 -> {
                        return Primary.copy(alpha = .5f)
                    }

                    1 -> {
                        return Color.DarkGray.copy(alpha = .5f)
                    }

                    2 -> {
                        return BackGroundDark.copy(alpha = .5f)
                    }

                    3 -> {
                        GradientColor1
                    }

                    4 -> {
                        return GradientColor2
                    }

                    5 -> {
                        return Primary.copy(alpha = .5f)
                    }

                    6 -> {
                        return Color.DarkGray.copy(alpha = .5f)
                    }

                    7 -> {
                        return BackGroundDark.copy(alpha = .5f)
                    }

                    8 -> {
                        return GradientColor1
                    }

                    9 -> {
                        return GradientColor2
                    }
                }
            }
        }

        return Primary.copy(alpha = .5f)
    }

    fun isBanglalink(mobile: String): Boolean {
        return (mobile.startsWith("019") || mobile.startsWith("014")) && mobile.length == 11
    }

    fun isRobi(mobile: String): Boolean {
        return (mobile.startsWith("018") || mobile.startsWith("016")) && mobile.length == 11
    }

    fun isNumeric(string: String): Boolean {
        return string.toDoubleOrNull() != null
    }
}
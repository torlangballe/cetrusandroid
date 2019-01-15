
//
//  ZWords.swift
//  capsule.fm
//
//  Created by Tor Langballe on /11/8/18.
//  Copyright © 2018 Capsule.fm. All rights reserved.
//
package com.github.torlangballe.cetrusandroid

data class ZWords (val _dummy: Int = 0) {
    companion object {

        fun Pluralize(word: String, count: Double, langCode: String? = null, pluralWord: String? = null) : String {
            var lang = ZLocale.GetDeviceLanguageCode()
            if (langCode != null) {
                lang = langCode!!
            }
            if (pluralWord != null) {
                if (count == 1.0) {
                    return word
                }
                return pluralWord!!
            }
            if (lang == "no") {
                return if ((count == 1.0)) word else word + "er"
            }
            if (lang == "d") {
                return if ((count == 1.0)) word else word + "e"
            }
            if (lang == "ja") {
                return word
            }
            // english
            if (count == 1.0) {
                return word
            }
            if (ZStr.Tail(word) == "s") {
                return word + "es"
            }
            return word + "s"
        }

        fun GetLogin() : String =
                ZTS("Log in")

        // generic name for login button etc
        fun GetLogout() : String =
                ZTS("Log out")

        // generic name for login button etc
        fun GetAnd() : String =
                ZTS("And")

        // generic name for and, i.e: cats and dogs
        fun GetHour(plural: Boolean = false) : String {
            if (plural) {
                return ZTS("hours")
                // generic name for hours plural
            }
            return ZTS("hour")
            // generic name for hour singular
        }

        fun GetToday() : String =
                ZTS("Today")

        // generic name for today
        fun GetYesterday() : String =
                ZTS("Yesterday")

        // generic name for yesterday
        fun GetTomorrow() : String =
                ZTS("Tomorrow")

        // generic name for tomorrow
        // these three functions insert day/month/year symbol after date in picker, only needed for ja so far.
        fun GetDateInsertDaySymbol() : String {
            if (ZLocale.GetDeviceLanguageCode() == "ja") {
                return "日"
            }
            return ""
        }

        fun GetDateInsertMonthSymbol() : String {
            if (ZLocale.GetDeviceLanguageCode() == "ja") {
                return "月"
            }
            return ""
        }

        fun GetDateInsertYearSymbol() : String {
            if (ZLocale.GetDeviceLanguageCode() == "ja") {
                return "年"
            }
            return ""
        }

        fun GetMinute(plural: Boolean = false) : String {
            if (plural) {
                return ZTS("minutes")
                // generic name for minutes plural
            }
            return ZTS("minute")
            // generic name for minute singular
        }

        fun GetMeter(plural: Boolean = false, langCode: String = "") : String {
            if (plural) {
                return ZTS("meters", langCode = langCode)
                // generic name for meters plural
            }
            return ZTS("meter", langCode = langCode)
            // generic name for meter singular
        }

        fun GetKiloMeter(plural: Boolean = false, langCode: String = "") : String {
            if (plural) {
                return ZTS("kilometers", langCode = langCode)
                // generic name for kilometers plural
            }
            return ZTS("kilometer", langCode = langCode)
            // generic name for kilometer singular
        }

        fun GetMile(plural: Boolean = false, langCode: String = "") : String {
            if (plural) {
                return ZTS("miles", langCode = langCode)
                // generic name for miles plural
            }
            return ZTS("mile", langCode = langCode)
            // generic name for mile singular
        }

        fun GetYard(plural: Boolean = false, langCode: String = "") : String {
            if (plural) {
                return ZTS("yards", langCode = langCode)
                // generic name for yards plural
            }
            return ZTS("yard", langCode = langCode)
            // generic name for yard singular
        }

        fun GetInch(plural: Boolean = false, langCode: String = "") : String {
            if (plural) {
                return ZTS("inches", langCode = langCode)
                // generic name for inch plural
            }
            return ZTS("inch", langCode = langCode)
            // generic name for inches singular
        }

        fun GetDayPeriod() : String =
                ZTS("am/pm")

        // generic name for am/pm part of day when used as a column title etc
        fun GetOk() : String =
                ZTS("OK")

        // generic name for OK in button etc
        fun GetSet() : String =
                ZTS("Set")

        // generic name for Set in button, i.e set value
        fun GetOff() : String =
                ZTS("Off")

        // generic name for Off in button, i.e value/switch is off. this is RMEOVED by VO in value
        fun GetOpen() : String =
                ZTS("Open")

        // generic name for button to open a window or something
        fun GetBack() : String =
                ZTS("Back")

        // generic name for back button in navigation bar
        fun GetCancel() : String =
                ZTS("Cancel")

        // generic name for Cancel in button etc
        fun GetClose() : String =
                ZTS("Close")

        // generic name for Close in button etc
        fun GetPlay() : String =
                ZTS("Play")

        // generic name for Play in button etc
        fun GetPost() : String =
                ZTS("Post")

        // generic name for Post in button etc, post a message to social media etc
        fun GetEdit() : String =
                ZTS("Edit")

        // generic name for Edit in button etc, to start an edit action
        fun GetReset() : String =
                ZTS("Reset")

        // generic name for Reset in button etc, to reset/restart something
        fun GetPause() : String =
                ZTS("Pause")

        // generic name for Pause in button etc
        fun GetSave() : String =
                ZTS("Save")

        // generic name for Save in button etc
        fun GetAdd() : String =
                ZTS("Add")

        // generic name for Add in button etc
        fun GetDelete() : String =
                ZTS("Delete")

        // generic name for Delete in button etc
        fun GetExit() : String =
                ZTS("Exit")

        // generic name for Exit in button etc. i.e: You have unsaved changes. [Save] [Exit]
        fun GetRetryQuestion() : String =
                ZTS("Retry?")

        // generic name for Retry? in button etc, must be formulated like a question
        fun GetFahrenheit() : String =
                ZTS("fahrenheit")

        // generic name for fahrenheit, used in buttons etc.
        fun GetCelsius() : String =
                ZTS("celsius")

        // generic name for celsius, used in buttons etc.
        fun GetSettings() : String =
                ZTS("settings")

        // generic name for settings, used in buttons / title etc
        fun GetDayOfMonth() : String =
                ZTS("Day")

        // generic name for the day of a month i.e 23rd of July
        fun GetMonth() : String =
                ZTS("Month")

        // generic name for month.
        fun GetYear() : String =
                ZTS("Year")

        // generic name for year.
        fun GetDay(plural: Boolean = false) : String {
            if (plural) {
                return ZTS("Days")
                // generic name for the plural of a number of days since/until etc
            }
            return ZTS("Day")
            // generic name for a days since/until etc
        }

        fun GetSelected(on: Boolean) : String {
            if (on) {
                return ZTS("Selected")
                // generic name for selected in button/title/switch, i.e something is selected/on
            } else {
                return ZTS("unselected")
                // generic name for unselected in button/title/switch, i.e something is unselected/off
            }
        }

        fun GetMonthFromNumber(m: Int, chars: Int = -1) : String {
            var str = ""
            when (m) {
                1 -> str = ZTS("January")
                // name of month
                2 -> str = ZTS("February")
                // name of month
                3 -> str = ZTS("March")
                // name of month
                4 -> str = ZTS("April")
                // name of month
                5 -> str = ZTS("May")
                // name of month
                6 -> str = ZTS("June")
                // name of month
                7 -> str = ZTS("July")
                // name of month
                8 -> str = ZTS("August")
                // name of month
                9 -> str = ZTS("September")
                // name of month
                10 -> str = ZTS("October")
                // name of month
                11 -> str = ZTS("November")
                // name of month
                12 -> str = ZTS("December")
                // name of month
            }
            if (chars != -1) {
                str = ZStr.Head(str, chars = chars)
            }
            return str
        }

        // generic name for year.
        fun GetNameOfLanguageCode(langCode: String, inLanguage: String = "en") : String {
            when (langCode.lowercased()) {
                "en" -> return ZTS("English")
                // name of english language
                "de" -> return ZTS("German")
                // name of german language
                "ja", "jp" -> return ZTS("Japanese")
                // name of english language
                "no", "nb", "nn" -> return ZTS("Norwegian")
                // name of norwegian language
                "us" -> return ZTS("American")
                // name of american language/person
                "ca" -> return ZTS("Canadian")
                // name of canadian language/person
                "nz" -> return ZTS("New Zealander")
                // name of canadian language/person
                "at" -> return ZTS("Austrian")
                // name of austrian language/person
                "ch" -> return ZTS("Swiss")
                // name of swiss language/person
                "in" -> return ZTS("Indian")
                // name of indian language/person
                "gb", "uk" -> return ZTS("British")
                // name of british language/person
                "za" -> return ZTS("South African")
                // name of south african language/person
                "ae" -> return ZTS("United Arab Emirati")
                // name of UAE language/person
                "id" -> return ZTS("Indonesian")
                // name of indonesian language/person
                "sa" -> return ZTS("Saudi Arabian")
                // name of saudi language/person
                "au" -> return ZTS("Australian")
                // name of australian language/person
                "ph" -> return ZTS("Filipino")
                // name of filipino language/person
                "sg" -> return ZTS("Singaporean")
                // name of singaporean language/person
                "ie" -> return ZTS("Irish")
                else -> // name of irish language/person
                    return ""
            }
        }

        fun GetDistance(meters: Double, metric: Boolean, langCode: String, round: Boolean) : String {
            val Meter = 1
            val Km = 2
            val Mile = 3
            val Yard = 4
            var type = Meter
            var d = meters
            var distance = ""
            var word = ""
            if (metric) {
                if (d >= 1000) {
                    type = Km
                    d /= 1000
                }
            } else {
                d /= 1.0936133
                if (d >= 1760) {
                    type = Mile
                    d /= 1760
                    distance = ZStr.Format("%.1lf", d)
                } else {
                    type = Yard
                    d = ZMath.Floor(d)
                    distance = "${d}"
                }
            }
            when ((type)) {
                Meter -> word = GetMeter(plural = true)
                Km -> word = GetKiloMeter(plural = true)
                Mile -> word = GetMile(plural = true)
                Yard -> word = GetYard(plural = true)
            }
            if (type == Meter || type == Yard && round) {
                d = ZMath.Ceil(((ZMath.Ceil(d) + 9) / 10) * 10)
                distance = ("${d.toInt()}")
            } else if (round && d > 50) {
                distance = ZStr.Format("%d", d.toInt())
            } else {
                distance = ZStr.Format("%.1lf", d)
            }
            return distance + " " + word
        }

        fun MemorySizeAsString(b: Long, langCode: String = "", maxSignificant: Int = 3, isBits: Boolean = false) : String {
            val kiloByte = 1024.0
            val megaByte = kiloByte * 1024
            val gigaByte = megaByte * 1024
            val terraByte = gigaByte * 1024
            var word = "T"
            var n = b.toDouble() / terraByte
            val d = b.toDouble()
            if (d < kiloByte) {
                word = ""
                n = b.toDouble()
            } else if (d < megaByte) {
                word = "K"
                n = b.toDouble() / kiloByte
            } else if (d < gigaByte) {
                word = "M"
                n = b.toDouble() / megaByte
            } else if (d < terraByte) {
                word = "G"
                n = b.toDouble() / gigaByte
            }
            word += (if (isBits) "b" else "B")
            val str = ZStr.NiceDouble(n, maxSig = maxSignificant) + " " + word
            return str
        }

        fun GetHemisphereDirectionsFromGeoAlignment(alignment: ZAlignment, separator: String, langCode: String) : String {
            var str = ""
            if (alignment and ZAlignment.Top) {
                str = ZTS("North", langCode = langCode)
                // General name for north as in north-east wind etc
            }
            if (alignment and ZAlignment.Bottom) {
                str = ZStr.ConcatNonEmpty(sep = separator, items = mutableListOf(str, ZTS("South", langCode = langCode)))
                // General name for south as in south-east wind etc
            }
            if (alignment and ZAlignment.Left) {
                str = ZStr.ConcatNonEmpty(sep = separator, items = mutableListOf(str, ZTS("West", langCode = langCode)))
                // General name for west as in north-west wind etc
            }
            if (alignment and ZAlignment.Right) {
                str = ZStr.ConcatNonEmpty(sep = separator, items = mutableListOf(str, ZTS("East", langCode = langCode)))
                // General name for north as in north-east wind etc
            }
            return str
        }
    }
}

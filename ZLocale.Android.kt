//
//  ZLocale.swift
//
//  Created by Tor Langballe on /13/08/2018.
//
//

package com.github.torlangballe.cetrusandroid

data class ZLocale(val _dummy:Int = 0) {
    companion object {

        fun GetDeviceLanguageCode(forceNo: Boolean = true) : String {
            val currentLocale = zMainActivityContext!!.resources.configuration.locales.get(0)
            return currentLocale.language
        }

        fun GetLangCodeAndCountryFromLocaleId(bcp: String, forceNo: Boolean = true) : Pair<String, String> {
            // lang, country-code
            var (lang, ccode) = ZStr.SplitInTwo(bcp, sep = "-")
            if (ccode.isEmpty()) {
                val (_, ccode) = ZStr.SplitInTwo(bcp, sep = "_")
                if (ccode.isEmpty()) {
                    val parts = ZStr.Split(bcp, sep = "-")
                    if (parts.size > 2) {
                        return Pair(parts.firstOrNull()!!, parts.lastOrNull()!!)
                    }
                    return Pair(bcp, "")
                }
            }
            if (lang == "nb") {
                lang = "no"
            }
            return Pair(lang, ccode)
        }

        private fun isImperialCountry() : Boolean {
            if (zMainActivityContext == null) {
                ZDebug.Print("No zMainActivityContext")
            }
            val currentLocale = zMainActivityContext!!.resources.configuration.locales.get(0)
            val country = currentLocale .getISO3Country().lowercased()
            if (country == "usa" || country == "mmr") {
                return true
            }
            return false
        }

        val UsesMetric: Boolean =
                !isImperialCountry()

        val UsesCelsius: Boolean =
                !isImperialCountry()

        val Uses24Hour: Boolean =
                !isImperialCountry()
    }
}

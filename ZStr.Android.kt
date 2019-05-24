//
//  ZStr.swift
//  Zed
//
//  Created by Tor Langballe on /23/9/14.
//  Copyright (c) 2014 Capsule.fm. All rights reserved.
//

package com.github.torlangballe.cetrusandroid

import java.io.File
import java.io.FileInputStream
import java.net.URLDecoder
import java.net.URLEncoder
import java.util.*
import java.util.regex.Pattern

enum class ZStringCompareOptions {
    caseInsensitive, literal, backwards, anchored, numeric, diacriticInsensitive, widthInsensitive, forcedOrdering, regularExpression
}

data class ZStr(val dummy:Int = 0) {
    companion object {
        fun Utf8(str:String) : ByteArray {
            return str.toByteArray()
        }

        fun Format(format: String, vararg args: Any?) : String {
            var f = ZStr.Replace(format, "%S", "%s")
            f = ZStr.Replace(f, "%lx", "%x")
            f = ZStr.Replace(f, "%ld", "%d")
            return f.format(*args)
        }

        fun SaveToFile(str: String, file: ZFileUrl) : Error? {
            val data = ZData(utfString = str)
            return data.SaveToFile(file)
        }

        fun LoadFromFile(file: ZFileUrl) : Pair<String, Error?> {
            val data = ZData()
            val err = data.LoadFromFile(file)
            if (err != null) {
                return Pair("", err)
            }
            return Pair(data.GetString(), null)
        }

        fun FindFirstOfChars(str: String, charset: String) : Int {
            ZNOTIMPLEMENTED()
            return -1
        }

        fun FindLastOfChars(str: String, charset: String) : Int {
            ZNOTIMPLEMENTED()
            return -1
        }

        fun Join(strs: List<String>, sep: String) : String =
                strs.joinToString(separator = sep)

        fun Split(str: String, sep: String) : List<String> {
            if (str.isEmpty()) {
                return listOf()
            }
            return str.split(sep)
        }

        fun SplitByChars(str: String, chars: String) : List<String> {
            ZNOTIMPLEMENTED()
            return listOf()
        }

        fun SplitN(str: String, sep: String, n: Int) : List<String> {
            var comps = Split(str, sep = sep)
            if (comps.size <= n) {
                return comps
            }
            var out = comps.subList(0, n).toMutableList()
            val c = comps.subList(n, comps.count())
            out.add(Join(c, sep = sep))
            return out
        }

        fun SplitInTwo(str: String, sep: String) : Pair<String, String> {
            val parts = SplitN(str, sep = sep, n = 2)
            if (parts.size == 2) {
                val first = parts[0]
                val rest = parts[1]
                return Pair(first, rest)
            }
            return Pair("", "")
        }

        fun SplitIntoLengths(str: String, length: Int) : List<String> {
            return str.chunked(length)
        }

        fun CountLines(str: String) : Int =
                str.lines().count()

        fun Head(str: String, chars: Int) : String {
            return str.take(chars)
        }

        fun Tail(str: String, chars: Int = 1) : String {
            return str.takeLast(chars)
        }

        fun Body(str: String, pos: Int, size: Int = -1) : String {
            if (pos >= str.length) {
                return ""
            }
            if (size == -1 || pos + size >= str.length) {
                return str.substring(startIndex = pos)
            }
            val e = pos + size
            return str.substring(startIndex = pos, endIndex = e)
        }

        fun HeadUntil(str: String, sep: String, options: ZStringCompareOptions = ZStringCompareOptions.literal) : String {
            val i = str.indexOf(sep)
            if (i == -1) {
                return str
            }
            return str.substring(i + 1)
        }

        fun HeadUntilWithRest(str: String, sep: String, options: ZStringCompareOptions = ZStringCompareOptions.literal) : Pair<String, String> {
            val s = HeadUntil(str, sep, options)
            val si = str.length
            val hi = s.length
            val rest = str.substring(si - hi)
            return Pair(s, rest)
        }

        fun TailUntil(str: String, sep: String, options: ZStringCompareOptions = ZStringCompareOptions.literal) : String {
            val i = str.lastIndexOf(sep)
            if (i == -1) {
                return ""
            }
            return str.substring(i + 1)
        }

        fun TailUntilWithRest(str: String, sep: String, options: ZStringCompareOptions = ZStringCompareOptions.literal) : Pair<String, String> {
            val i = str.lastIndexOf(sep)
            if (i == -1) {
                return Pair(str, "")
            }
            val s = str.substring(i + 1)
            val rest = str.take(i)
            return Pair(s, rest)
        }

        fun PopTailWord(str: String, sep: String = " ", options: ZStringCompareOptions = ZStringCompareOptions.literal) : Pair<String, String> {
            ZNOTIMPLEMENTED()
            return Pair("", "")
        }

        fun PopHeadWord(str:  String, sep: String = " ", options: ZStringCompareOptions = ZStringCompareOptions.literal) : Pair<String, String> {
            ZNOTIMPLEMENTED()
            return Pair("", "")
        }

        fun HasPrefix(str: String, prefix: String) : Boolean {
            if (str.startsWith(prefix)) {
                return true
            }
            return false
        }

        fun HasPrefixWithRest(str: String, prefix: String) : Pair<Boolean, String> {
            if (str.startsWith(prefix)) {
                var rest = Body(str, pos = prefix.length)
                return Pair(true, rest)
            }
            return Pair(false, "")
        }

        fun HasSuffix(str: String, suffix: String) : Boolean {
            if (str.endsWith(suffix)) {
                return true
            }
            return false
        }

        fun HasSuffixWithRest(str: String, suffix: String) : Pair<Boolean, String> {
            if (str.endsWith(suffix)) {
                val size = maxOf(str.length - suffix.length, 0)
                var rest = Head(str, size)
                return Pair(true, rest)
            }
            return Pair(false, "")
        }

        fun TruncatedStart(str: String, chars: Int) : String {
            return str.dropLast(chars)
        }

        fun TruncatedEnd(str: String, chars: Int) : String {
            return str.drop(chars)
        }

        fun TruncateMiddle(str: String, maxChars: Int, separator: String) : String {
            // sss...eee of longer string
            if (str.length > maxChars) {
                return Head(str, chars = maxChars / 2) + separator + Tail(str, chars = maxChars / 2)
            }
            return str
        }

        fun ConcatNonEmpty(sep: String = " ", items: List<String>) : String {
            var str = ""
            var first = true
            for (item in items) {
                if (!item.isEmpty()) {
                    if (!first) {
                        str += sep
                    }
                    str += item
                    first = false
                }
            }
            return str
        }

        fun Compare(a: String, b: String, reverse: Boolean = false, caseless: Boolean = true, removeThe: Boolean = false, sortAlphaFirst: Boolean = false) : Boolean {
            var order = false
            var va = a
            var vb = b
            if (removeThe) {
                if (va.toLowerCase().startsWith("the ")) {
                    va = Body(va, pos = 4)
                }
                if (vb.toLowerCase().startsWith("the ")) {
                    vb = Body(vb, pos = 4)
                }
            }
            if (sortAlphaFirst && !va.isEmpty() && !vb.isEmpty()) {
                ZNOTIMPLEMENTED()
            }
            if (caseless) {
                va = va.toLowerCase()
                vb = vb.toLowerCase()
            } else {
                order = va.compareTo(vb) == 0
            }
            return if (reverse) !order else order
        }

        fun Sorted(strings: List<String>, reverse: Boolean = false, caseless: Boolean = true, removeThe: Boolean = false) : List<String> {
            return strings.sortedWith(object: Comparator<String> {
                override fun compare(a: String, b: String): Int = when {
                    Compare(a = a, b = b, reverse = reverse, caseless = caseless, removeThe = removeThe) -> 1
                    else -> 0
                }
            })
        }

        fun ReplaceWhiteSpaces(str: String, with:String) : String {
            return str.replace("\\s".toRegex(), with)
        }

        fun Replace(str: String, find: String, with: String, caseless: Boolean = false) : String =
            str.replace(oldValue = find, newValue =  with, ignoreCase = caseless)

        fun Evaluate(str: String, args: Map<String, Any> = mapOf()) : Double? {
            ZNOTIMPLEMENTED()
            return null
        }

        fun Trim(str: String, chars: String = " ") : String {
            return str.trim(*chars.toCharArray())
        }

        fun CountInstances(instance: String, str: String) : Int =
             str.split(instance).size - 1

        fun FilterToAlphaNumeric(str: String) : String {
            val re = Regex("[^A-Za-z0-9]")
            return re.replace(str, "")
        }

        fun FilterToNumeric(str: String) : String {
            val re = Regex("[^0-9]")
            return re.replace(str, "")
        }

        fun CamelCase(str: String) : String =
                str.capitalize().replace(oldValue = " ", newValue= "")

        fun IsUppercase(c: Character) : Boolean {
            val s = c.toString()
            return (s == s.toUpperCase())
        }

        fun SplitCamelCase(str: String) : List<String> {
            var matches = mutableListOf<String>()
            val m = Pattern.compile("[A-Z][a-z]+").matcher(str);
            while (m.find()) {
                matches.add(m.group())
            }
            return matches
        }

        fun HashToU64(str: String) : Long {
            var result = 5381L
            val buf = str.toByteArray()
            for (b in buf) {
                result = 127 * (result and 0x00ffffffffffffff) + b.toLong()
            }
            return result
        }

        fun MakeHashTagWord(str: String) : String {
            val split = SplitByChars(str, chars = " .-,/()_")
            var words = mutableListOf<String>()
            for (s in split) {
                words.addAll(SplitCamelCase(FilterToAlphaNumeric(s)))
            }
            val flat = words.reduce { sum, it -> sum + it.capitalize() }
            return flat
        }

        fun Unescape(str: String) : String {
            var vstr = str.replace(oldValue = "\\n", newValue= "\n")
            vstr = vstr.replace(oldValue = "\\r", newValue = "\r")
            vstr = vstr.replace(oldValue = "\\t", newValue = "\t")
            vstr = vstr.replace(oldValue = "\\\"", newValue = "\"")
            vstr = vstr.replace(oldValue = "\\'", newValue = "'")
            vstr = vstr.replace(oldValue = "\\\\", newValue = "\\")
            return vstr
        }

        fun ForEachLine(str: String, forEach: (sline: String) -> Boolean) {
            for (s in str.lines()) {
                if (!forEach(s)) {
                    break
                }
            }
        }

        fun Base64Encode(str: String) : String {
            val base64 = Base64.getEncoder().encodeToString(str.toByteArray())
            return base64
        }

        fun UrlEncode(str: String) : String? {
            return URLEncoder.encode(str, "UTF-8")
        }

        fun UrlDecode(str: String) : String? {
            return URLDecoder.decode(str, "UTF-8")
        }

        fun StrMatchsWildcard(str: String, wild: String) : Boolean {
            ZNOTIMPLEMENTED()
            return false
        }

/*
        fun CopyToCCharArray(carray: UnsafeMutablePointer<Byte>, str: String) {
            ZNOTIMPLEMENTED()
            return ""
        }

        // str is coerced to c-string amazingly enough
        fun StrFromCCharArray(carray: UnsafeMutablePointer<Byte>?) : String {
            ZNOTIMPLEMENTED()
            return ""
        }

        fun CopyStrToAllocedCStr(str: String, len: Int) : UnsafeMutablePointer<Byte> {
            ZNOTIMPLEMENTED()
            return null
        }
*/

        fun NiceDouble(d: Double, maxSig: Int = 8, separator: String = ",") : String {
            val format = ZStr.Format("%%.%df", maxSig)
            val s = ZStr.Format(format, d)
            val parts = ZStr.Split(s, ".")
            var n = parts[0]
            var f = ""
            if (parts.count() > 1) {
                f = parts[1]

                while (ZStr.Tail(f) == "0") {
                    f = f.removedLast()
                }
            }
            if (separator != "") {
                n = ZStr.Join(ZStr.SplitIntoLengths(n.reversed(), 3).asReversed(), separator)
            }
            if (f != "") {
                n += "." + f
            }
            return n
        }

        fun SplitLines(str: String, skipEmpty: Boolean = true) : List<String> {
            if (skipEmpty) {
                return str.lines().filter { !it.isEmpty() }
            } else {
                return str.lines()
            }
        }

        fun ToDouble(str:String, def:Double? = null) : Double? {
            try {
                return str.toDouble()
            }
            catch (e:NumberFormatException) {
                return def
            }
        }

        fun ToInt(str:String, def:Int? = null) : Int? {
            try {
                return str.toInt()
            }
            catch (e:NumberFormatException) {
                return def
            }
        }

        fun Base64CharToNumber(char: Int) : Int {
            val iA = 'A'.toInt()
            val iZ = 'Z'.toInt()
            val ia = 'a'.toInt()
            val iz = 'z'.toInt()
            val i0 = '0'.toInt()
            val i9 = '9'.toInt()
            val iPlus = '+'.toInt()
            val iSlash = '/'.toInt()
            return when (char) {
                in iA .. iZ -> char - iA
                in ia .. iz -> char - ia + 26
                in i0 .. i9 -> char - i0 + 26 + 26
                iPlus -> 62
                iSlash -> 63
                else -> -1
            }
        }

        fun NumberToBase64String(num: Int) : String {
            val scalar = NumberToBase64Char(num)
            if (scalar != null) {
                return scalar.toChar().toString()
            }
            return ""
        }

        fun NumberToBase64Char(num: Int) : Int? {
            val iA = 'A'.toInt()
            val iZ = 'Z'.toInt()
            val ia = 'a'.toInt()
            val iz = 'z'.toInt()
            val i0 = '0'.toInt()
            val i9 = '9'.toInt()
            val iPlus = '+'.toInt()
            val iSlash = '/'.toInt()
            return when (num) {
                in 0 until 26 -> iA + num
                in 26 until 52 -> ia + num - 26
                in 52 until 62 -> i0 + num - 26 - 26
                62 -> iPlus
                63 -> iSlash
                else -> null
            }
        }

        fun GetStemAndExtension(fileName:String) : Pair<String, String> {
            val (ext, stem) = TailUntilWithRest(fileName, sep = ".")
            return Pair(stem, ext)
        }
    }
}

/*
private fun rangeOfWordAtEnd(str: String, sep: String, options: ZStringCompareOptions) : Range<String.Index>? {
    val voptions = options.union(ZStringCompareOptions.backwards)
    val foundRange = str.range(of = sep, options = voptions)
    if (foundRange != null) {
        val range = foundRange.upperBound until str.endIndex
        return range
    }
    return null
}

private fun rangeOfWordAtStart(str: String, sep: String, options: ZStringCompareOptions) : Range<String.Index>? {
    val foundRange = str.range(of = sep, options = options)
    if (foundRange != null) {
        val range = str.startIndex until foundRange.lowerBound
        return range
    }
    return null
}

*/

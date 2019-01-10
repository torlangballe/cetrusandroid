//
//  ZTextField.Android.kt
//
//  Created by Tor Langballe on /18/09/18.
//

package com.github.torlangballe.cetrusandroid

import android.graphics.Canvas
import android.widget.EditText
import android.text.InputType
import android.view.Gravity
import android.widget.LinearLayout

enum class ZKeyboardType { default, asciiCapable, numbersAndPunctuation, URL, numberPad, phonePad, namePhonePad, emailAddress, decimalPad, twitter, webSearch, asciiCapableNumberPad }
enum class ZAutocapitalizationType { none, words, sentences, allCharacters }
enum class ZReturnKeyType { default, go, google, join, next, route, search, send, yahoo, done, emergencyCall, `continue` }
enum class ZTextClearButtonMode { never, whileEditing, unlessEditing, always }

// typealias ZTextPosition = UITextPosition

interface ZTextBase {
    val assumedLangCode: String
    var Color: ZColor
    var TextString: String
    var Selection: Pair<Int, Int>
    val KeyboardLocale: String
}

fun ZTextDismissKeyboard() {
}

val ZTextBase.SelectedText: String
    get() {
        val (s, e) = Selection
        if (s == e) {
            return TextString
        }
        return ZStr.Body(TextString, pos = s, size = e - s)
    }
val ZTextBase.KeyboardLangCode: String
    get() {
        return ZLocale.GetLangCodeAndCountryFromLocaleId(KeyboardLocale).first
    }

data class ZKeyboardInfo(
        var keyboardType: ZKeyboardType? = null,
        var autoCapType: ZAutocapitalizationType? = null,
        var returnType: ZReturnKeyType? = null) {}

interface ZTextEditDelegate {
    fun HandleFocus(focused: Boolean, from: ZView) { }
    fun HandleTextShouldBeginEditing(from: ZView) : Boolean { return false }
    fun HandleTextShouldEndEditing(from: ZView) : Boolean { return false }
    fun HandleTextShouldReturn(from: ZView) : Boolean { return false }
    fun HandleTextDidChange(from: ZView) { }
    fun HandleTextDidChangeSelection() { }
}

fun ZTextEditDelegate.HandleFocus(focused: Boolean, from: ZView) {}

fun ZTextEditDelegate.HandleTextShouldBeginEditing(from: ZView) : Boolean =
        true

fun ZTextEditDelegate.HandleTextShouldEndEditing(from: ZView) : Boolean =
        true

fun ZTextEditDelegate.HandleTextShouldReturn(from: ZView) : Boolean =
        true

fun ZTextEditDelegate.HandleTextDidChange(from: ZView) {}

fun ZTextEditDelegate.HandleTextDidChangeSelection() {}

open class ZTextField: EditText, ZTextBase, ZView, ZCustomViewDelegate {
    override var assumedLangCode:String = ""
    override var isHighlighted:Boolean = false
    override var Usable:Boolean = true
    var minWidth: Double = 0.0
    var maxWidth: Double = 0.0
    var xMaxLines: Int = 0
    override var objectName = "ZTextField"
    var marginY = 4
    var margin = ZSize()
    var useMenu = true
    var edited = false

    var xCornerRadius = 0.0
    var xBgColor = ZColor.Clear()
    var xStrokeColor = ZColor.Clear()
    var xStrokeWidth = 0.0
    var xFont: ZFont = ZFont.Nice(20.0)
    var xAlignment: ZAlignment = ZAlignment.None
    var xFirst = true

    private var target: ZTextEditDelegate? = null

    var keyboardType = ZKeyboardType.default
    var capType = ZAutocapitalizationType.none
    var returnType = ZReturnKeyType.default

    var TextLines:Int
        get() {
            return xMaxLines
        }
        set(m) {
            xMaxLines = m
            setLines(m)
            maxLines = m
        }
    override var Color: ZColor
        get() {
            return ZColor(colorInt = currentTextColor)
        }
        set(c) {
            this.setTextColor(c.color.toArgb())
        }

    override var TextString: String
        get() {
            return text.toString()
        }
        set(t) {
            setText(t)
        }

    override val KeyboardLocale: String
        get() = "XXX"

    var Real: Double?
        get() {
            if (text != null) {
                return ZStr.ToDouble(TextString, null)
            }
            return null
        }
        set(d) {
            if (d != null) {
                TextString = "$d!!"
            } else {
                text = null
            }
        }

    //    func SetPlaceholder(_ text:String, color:ZColor) {
    //        attributedPlaceholder = ZAttributedString(string:text, attributes:[NSAttributedStringKey.foregroundColor:color.rawColor])
    //    }
    constructor(text: String = "", minWidth: Double = 0.0, maxWidth: Double = 0.0, font: ZFont? = null, alignment: ZAlignment = ZAlignment.Left, margin: ZSize = ZSize(0.0, 0.0)) : super(zMainActivityContext!!) {
        TextString = text
        this.minWidth = minWidth
        this.maxWidth = maxWidth
        this.xAlignment = alignment

        if (font != null) {
            xFont = font
        }
        if (font != null) {
            this.typeface = font.typeface
            this.textSize = font.size.toFloat()
        }
        this.margin = margin
        this.SetAlignment(alignment)
        // this.delegate = this

        val lp = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f)
        this.setLayoutParams(lp)
        this.setSingleLine(true)
        this.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE)
    }

    override fun CalculateSize(total: ZSize): ZSize {
        if (TextString == "" && lineCount == 0) {
            return ZSize(0.0, 0.0)
        }
        var s = total
        if (maxWidth != 0.0 && maxWidth < s.w) {
            s.w = maxWidth
        }
        s.h = 29999.0

        var tdraw = ZTextDraw()
        tdraw.rect = ZRect(size = s)
        tdraw.font = xFont
        tdraw.alignment = xAlignment
        tdraw.maxLines = TextLines
        tdraw.text = text.toString()
        var bsize = tdraw.GetBounds().size
        bsize.w += 1
        bsize.h += margin.h + 10
        bsize.h = maxOf(bsize.h, 55.0)
        bsize.w += margin.w

        return bsize
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val wmode = MeasureSpec.getMode(widthMeasureSpec)
        val w = MeasureSpec.getSize(widthMeasureSpec).toDouble()
        val h = MeasureSpec.getSize(heightMeasureSpec).toDouble()

        val scale = ZScreen.Scale
//        var total = ZSize(w.toDouble(), h.toDouble())
//        total.Maximize(ZSize(100, 16))
//        total /= scale

        val total = ZSize(800, 100)
        if (w > 0 && w < ZScreen.Main.size.w) {
            total.w = w
        }
        if (h > 0 && h < ZScreen.Main.size.h) {
            total.h = h
        }
        var s = CalculateSize(total)
        s *= scale
        setMeasuredDimension((s.w).toInt(), s.h.toInt())
    }

    override fun View() : ZNativeView =
            this

    override var Selection: Pair<Int, Int>
        get() {
            return Pair(0, 0)
        }
        set(s) {
        }

    fun SetAlignment(a: ZAlignment) {
        this.gravity = Gravity.TOP
        //this.textAlignment = TEXT_ALIGNMENT_VIEW_START
    }

    override fun DrawInRect(rect: ZRect, canvas: ZCanvas) {

    }

    private fun updateBGAndCorner() {
        if (xCornerRadius != 0.0 || xStrokeWidth != 0.0 || xBgColor.tileImage != null) {
            View().setBackgroundColor(ZColor.Clear().color.toArgb())
        } else {
            View().setBackgroundColor(xBgColor.color.toArgb())
        }
        Expose()
    }

    override fun SetCornerRadius(radius: Double) {
        xCornerRadius = radius
        updateBGAndCorner()
    }

    override fun SetBackgroundColor(color: ZColor) {
        xBgColor = color
        updateBGAndCorner()
    }

    override fun SetStroke(width: Double, color: ZColor) {
        xStrokeColor = color
        xStrokeWidth = width
        updateBGAndCorner()
    }

    override fun draw(canvas: Canvas?) {
        if (canvas != null) {
            val scale = ZScreen.Scale
            val c = ZCanvas(canvas)
            c.PushState()
//            val cs = ZSize(canvas.width, canvas.height) / scale
            canvas.scale(scale.toFloat(), scale.toFloat())
            val r = LocalRect
            if (xCornerRadius != 0.0 || xStrokeWidth != 0.0 || xBgColor.tileImage != null) {
                c.SetColor(xBgColor)
                val path = ZPath(rect = r, corner = ZSize(xCornerRadius, xCornerRadius))
                c.FillPath(path)

                if (xStrokeWidth != 0.0) {
                    val rs = r + ZRect(xStrokeWidth / 2.0, xStrokeWidth / 2.0, - xStrokeWidth / 2.0 - 1, - xStrokeWidth / 2.0 - 1)
                    val spath = ZPath(rect = rs, corner = ZSize(xCornerRadius, xCornerRadius))
                    c.SetColor(xStrokeColor)
                    c.StrokePath(spath, width = xStrokeWidth)
                }
            }
            DrawInRect(r, canvas = c)
            c.PopState()
        }
        if (xFirst) {
            val scale = ZScreen.Scale
            val w = ZMath.Ceil(margin.w * scale).toInt()
            var hm = maxOf(margin.h, 6.0)
            val h = (hm * scale).toInt()
            this.setPadding(w, h, w, 0)
            xFirst = false
        }
        super.draw(canvas)
    }

/*
    override fun sizeThatFits(size: CGSize) : CGSize {
        var box = ZSize()
        if (maxWidth != 0) {
            box.w = maxWidth
        }
        box.h = 1000
        var gs = super.sizeThatFits(box.GetCGSize())
        if (minWidth != 0.0) {
            gs.width = maxOf(gs.width, CGFloat(minWidth))
        }
        if (maxWidth != 0.0) {
            gs.width = minOf(gs.width, CGFloat(maxWidth))
        }
        gs.height = maxOf(gs.height, 14)
        gs.height += CGFloat(2 * marginY)
        return gs
    }
*/
    override fun Unfocus() {
//        this.resignFirstResponder()
    }

    fun SetTintColor(c: ZColor) {

    }

    fun SetClearButtonMode(mode: ZTextClearButtonMode) {
//        clearButtonMode = mode
    }

    fun SetAutoCorrect(on: Boolean) {
//        this.autocorrectionType = if (on) .yes else .no
    }

    fun SetKeyboardType(type: ZKeyboardType) {
        keyboardType = type
        updateKeyboardPreferences()
    }

    fun updateKeyboardPreferences() {
        var t =
        when (keyboardType) {
            ZKeyboardType.`default` -> InputType.TYPE_CLASS_TEXT
            ZKeyboardType.asciiCapable -> InputType.TYPE_CLASS_TEXT
            ZKeyboardType.numbersAndPunctuation -> InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL or InputType.TYPE_NUMBER_FLAG_SIGNED
            ZKeyboardType.URL -> InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_URI
            ZKeyboardType.numberPad -> InputType.TYPE_CLASS_NUMBER
            ZKeyboardType.phonePad -> InputType.TYPE_CLASS_PHONE
            ZKeyboardType.namePhonePad -> InputType.TYPE_CLASS_PHONE
            ZKeyboardType.emailAddress -> InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
            ZKeyboardType.decimalPad -> InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL or InputType.TYPE_NUMBER_FLAG_SIGNED
            ZKeyboardType.twitter -> InputType.TYPE_CLASS_TEXT
            ZKeyboardType.webSearch -> InputType.TYPE_CLASS_TEXT
            ZKeyboardType.asciiCapableNumberPad -> InputType.TYPE_CLASS_NUMBER
        }
        if (TextLines > 1) {
            t = t or InputType.TYPE_TEXT_FLAG_MULTI_LINE
        }
        var c = when(capType) {
            ZAutocapitalizationType.allCharacters -> InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS
            ZAutocapitalizationType.words -> InputType.TYPE_TEXT_FLAG_CAP_WORDS
            ZAutocapitalizationType.sentences -> InputType.TYPE_TEXT_FLAG_CAP_SENTENCES
            else -> 0
        }
        val i = t or c
        this.inputType = i
    }

    fun SetAutoCapType(type: ZAutocapitalizationType) {
        capType = type
        updateKeyboardPreferences()
    }

    fun SetReturnKeyType(type: ZReturnKeyType) {
        returnType = type
        updateKeyboardPreferences()
    }

    fun SetEnablesReturnKeyAutomatically(on: Boolean) {
//        this.enablesReturnKeyAutomatically = on
    }

    fun SetKeyboardDark(dark: Boolean) {
//        this.keyboardAppearance = if (dark) .dark else .light
    }

    fun InsertTextAtSelection(str: String) {
//        val r = this.selectedTextRange
//        if (r != null) {
//            this.replace(r, withText = str)
//        }
    }

    fun EndEditing() {
    //    this.endEditing(true)
    }

//    override fun becomeFirstResponder() : Boolean {
//        target?.HandleFocus(true, from = this)
//        return super.becomeFirstResponder()
//    }

    fun SetTarget(target: ZTextEditDelegate) {
        this.target = target
//        NotificationCenter.default.addObserver(this, selector = #selector(ZTextField.textFieldDidChange(_:)), name = NSNotification.Name.UITextFieldTextDidChange, object = null)
    }

    // afterTextChanged and beforeTextChanged too
    override fun onTextChanged(text: CharSequence?, start: Int, lengthBefore: Int, lengthAfter: Int) {
        if (!xFirst) {
            super.onTextChanged(text, start, lengthBefore, lengthAfter)
            if (target != null) {
                target!!.HandleTextDidChange(this)
            }
        }
    }
/*
    @objc fun textFieldDidChange(notification: Notification) {
        val from = notification.object as? UITextField
        if (from != null) {
            if (from == this) {
                target?.HandleTextDidChange(this)
            }
        }
    }

    fun textFieldShouldBeginEditing(textField: UITextField) : Boolean {
        if (target != null) {
            return target!!.HandleTextShouldBeginEditing(this)
        }
        return true
    }

    fun textFieldShouldEndEditing(textField: UITextField) : Boolean {
        if (target != null) {
            return target!!.HandleTextShouldEndEditing(this)
        }
        return true
    }

    fun textFieldShouldReturn(textField: UITextField) : Boolean {
        if (target != null) {
            return target!!.HandleTextShouldReturn(this)
        }
        return true
    }

    fun textFieldDidBeginEditing(textField: UITextField) {
        edited = true
        ZScrollView.ScrollViewToMakeItVisible(this)
    }

    fun textField(textField: UITextField, range: NSRange, string: String) : Boolean =
            true

    override fun canPerformAction(action: Selector, sender: Any?) : Boolean {
        if (!useMenu) {
            ZMainQue.async {   ->
                UIMenuController.shared.setMenuVisible(false, animated = false)
            }
        }
        return super.canPerformAction(action, withSender = sender)
    }

    fun getRange(s: Int, e: Int) : UITextRange? {
        val beginning = this.beginningOfDocument
        val start = this.position(from = beginning, offset = s)
        val end = this.position(from = beginning, offset = e)
        return this.textRange(from = start!!, to = end!!)
    }
*/

    fun GetKeyboardLocale() : String {
     //   this.textInputMode!!.primaryLanguage!!
        return "en-US"
    }

    fun ShowClearButton(show: Boolean) {
//        this.clearButtonMode = if (show) .always else .never
    }

    fun SetPlaceholderText(placeholder: String, color: ZColor = ZColor()) {
        var col = color
        if (col.undefined) {
            if (this.Color.GrayScale > 0.5) {
                col = ZColor(white = 1.0, a = 0.3)
            } else {
                col = ZColor(white = 0.0, a = 0.3)
            }
        }
        hint = placeholder
        setHintTextColor(col.color.toArgb())
    }


    /*s
    override fun textRect(bounds: CGRect) : CGRect =
            bounds.insetBy(dx = CGFloat(margin.w), dy = CGFloat(margin.h))

    override fun editingRect(bounds: CGRect) : CGRect =
            bounds.insetBy(dx = CGFloat(margin.w), dy = CGFloat(margin.h))

    override fun layoutSubviews() {
        super.layoutSubviews()
        var g: CGFloat = 0
        var a: CGFloat = 0
        if (clearButtonMode != .never) {
            backgroundColor?.getWhite(andg, alpha = anda)
            if (g < 0.5) {
                for (view in subviews) {
                    if (view is UIButton) {
                        val button = view as! UIButton
                        val image = ZImage(named = "ztextclear.png")
                        if (image != null) {
                            button.setImage(image, for = .highlighted)
                            button.setImage(image, for = UIControlState())
                        }
                    }
                }
            }
        }
    }
    var tintedClearImage: UIImage? = null
    */
}

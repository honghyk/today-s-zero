package com.github.windsekirun.koreanindexer

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.os.Handler
import android.text.TextUtils
import android.util.AttributeSet
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.SectionIndexer
import android.widget.Toast

import org.apache.commons.lang3.StringUtils

import java.util.ArrayList
import java.util.Collections
import java.util.Comparator
import java.util.LinkedHashMap
import kotlin.math.log

class KoreanIndexerListView : ListView {
    //private var context: Context? = null
    private val listHandler = Handler()

    private var showLetter = true
    private var leftPosition: Float = 0.toFloat()
    private var indexSize: Int = 0
    private var radius: Float = 0.toFloat()
    private var indWidth: Int = 0
    private var delayMillis: Int = 0
    private var indexerMargin: Int = 0
    private var useSection: Boolean = false
    private var section: String? = null
    private var positionRect: RectF? = null
    private var sectionPositionRect: RectF? = null
    private var backgroundPaint: Paint? = null
    private var sectionBackgroundPaint: Paint? = null
    private var textPaint: Paint? = null
    private var sectionTextPaint: Paint? = null
    private var mGesture: GestureDetector? = null
    private val onClickListener: View.OnClickListener? = null


    private val density: Float
        get() = context!!.resources.displayMetrics.density

    private val scaledDensity: Float
        get() = context!!.resources.displayMetrics.scaledDensity

    private val showLetterRunnable = Runnable {
        showLetter = false
        this@KoreanIndexerListView.invalidate()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        //this.context = context

        init(attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle) {
        //this.context = context

        init(attrs)
    }

    private fun init(attrs: AttributeSet) {
        // initialize variables
        positionRect = RectF()
        sectionPositionRect = RectF()
        textPaint = Paint()
        sectionTextPaint = Paint()
        backgroundPaint = Paint()
        sectionBackgroundPaint = Paint()

        backgroundPaint!!.isAntiAlias = true
        sectionBackgroundPaint!!.isAntiAlias = true

        val array = context!!.theme.obtainStyledAttributes(attrs, R.styleable.KoreanIndexerListView, 0, 0)

        val indexerBackground = array.getColor(R.styleable.KoreanIndexerListView_indexerBackground, -0x1)
        val sectionBackground = array.getColor(R.styleable.KoreanIndexerListView_sectionBackground, -0x1)
        val indexerTextColor = array.getColor(R.styleable.KoreanIndexerListView_indexerTextColor, -0x1000000)
        val sectionTextColor = array.getColor(R.styleable.KoreanIndexerListView_sectionTextColor, -0x1000000)
        val indexerRadius = array.getFloat(R.styleable.KoreanIndexerListView_indexerRadius, 60f)
        val indexerWidth = array.getInt(R.styleable.KoreanIndexerListView_indexerWidth, 20)
        val sectionDelay = array.getInt(R.styleable.KoreanIndexerListView_sectionDelay, 3 * 1000)
        val useSection = array.getBoolean(R.styleable.KoreanIndexerListView_useSection, true)
        val indexerMargin = array.getInt(R.styleable.KoreanIndexerListView_indexerMargin, 0)

        setIndexerBackgroundColor(indexerBackground)
        setSectionBackgroundColor(sectionBackground)
        setIndexerTextColor(indexerTextColor)
        setSectionTextColor(sectionTextColor)
        setIndexerRadius(indexerRadius)
        setIndexerWidth(indexerWidth)
        setSectionDelayMillis(sectionDelay)
        setUseSection(useSection)
        setIndexerMargin(indexerMargin)

        array.recycle()

        mGesture = GestureDetector(getContext(), object : GestureDetector.SimpleOnGestureListener() {
            override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
                return detectClickScope(e)
            }
        })
    }

    private fun detectClickScope(e: MotionEvent): Boolean {
        if (e.action != MotionEvent.ACTION_DOWN)
            return true

        val position = pointToPosition(e.x.toInt(), e.y.toInt())

        if (position != ListView.INVALID_POSITION) {
            performItemClick(getChildAt(position - firstVisiblePosition), position, getItemIdAtPosition(position))
        }

        return true
    }

    /**
     * 인덱서 여백값 조정 (상, 하)
     * [XML Field] indexerMargin
     *
     * @param margin 설정할 여백 값
     */
    fun setIndexerMargin(margin: Int) {
        this.indexerMargin = indexerMargin
    }

    /**
     * 인덱서 백그라운드 색상 설정
     * [XML Field] indexerBackground
     *
     * @param colorInt 설정할 색상
     */
    fun setIndexerBackgroundColor(colorInt: Int) {
        backgroundPaint!!.color = colorInt
    }

    /**
     * 패스트 스크롤 텍스트의 배경 색상
     * [XML Field] sectionBackground
     *
     * @param colorInt 설정할 색상
     */
    fun setSectionBackgroundColor(colorInt: Int) {
        sectionBackgroundPaint!!.color = colorInt
    }

    /**
     * 인덱서 텍스트 색상 설정
     * [XML Field] indexerTextColor
     *
     * @param colorInt 설정할 색상
     */
    fun setIndexerTextColor(colorInt: Int) {
        textPaint!!.color = colorInt
    }

    /**
     * 패스트 스크롤 텍스트의 색상 설정
     * [XML Field] sectionTextColor
     *
     * @param colorInt 설정할 색상
     */
    fun setSectionTextColor(colorInt: Int) {
        sectionTextPaint!!.color = colorInt
    }

    /**
     * 인덱서 배경의 곡선도
     * [XML Field] indexerRadius
     *
     * @param radius 설정할 곡선도
     */
    fun setIndexerRadius(radius: Float) {
        this.radius = radius
    }

    /**
     * 인덱서의 전체 너비
     * [XML Field] indexerWidth
     *
     * @param width 설정할 너비
     */
    fun setIndexerWidth(width: Int) {
        this.indWidth = width
    }

    /**
     * 패스트 스크롤 텍스트의 표시 시간 설정
     * [XML Field] sectionDelay
     *
     * @param delayMillis 설정할 시간 (단위: ms)
     */
    fun setSectionDelayMillis(delayMillis: Int) {
        this.delayMillis = delayMillis
    }

    /**
     * 패스트 스크롤 텍스트의 표시 여부
     * [XML Field] useSection
     *
     * @param useSection 표시 여부
     */
    fun setUseSection(useSection: Boolean) {
        this.useSection = useSection
    }

    /**
     * 키워드 리스트 설정
     *
     *
     * 이 메소드에 넘겨지는 리스트 파라미터는 정렬 여부와 상관이 없습니다.
     * setAdapter() 전에 호출해주세요.
     *
     *
     * 이 메소드에 넘겨지는 리스트 파라미터는 Generic를 지원하지 않습니다. 반드시 String 형태로 넣어주세요.
     *
     * @param keywordList 키워드 리스트
     */
    fun setKeywordList(keywordList: ArrayList<String>) {
        //Collections.sort(keywordList, OrderingByKorean.comparator)
        mapIndex = LinkedHashMap<String, Int>()
        for (i in keywordList.indices) {
            val item = keywordList[i]
            var index = item.substring(0, 1).toUpperCase()

            val c = index.get(0)
            if (OrderingByKorean.isKorean(c)) {
                index = KoreanChar.getCompatChoseong(c).toString()
            }


            if (mapIndex[index] == null)
                mapIndex[index] = i
        }

        val indexList = ArrayList(mapIndex.keys)
        sections = arrayOfNulls(indexList.size)
        var j = 0
        for(i in indexList){
            sections[j] = i
            j++
        }
        //indexList.toTypedArray<String>(sections)

        indexList.clear()
        indexList.trimToSize()
    }

    override fun dispatchDraw(canvas: Canvas) {
        super.dispatchDraw(canvas)
        if (sections.size == 0) {
            return
        }

        val scaledWidth = indWidth * density
        val scaledCompensation = indWidth * density
        val indexerMargin = this.indexerMargin * density
        leftPosition = this.width.toFloat() - this.paddingRight.toFloat() - scaledWidth

        positionRect!!.left = leftPosition
        positionRect!!.right = leftPosition + scaledWidth
        positionRect!!.top = this.paddingTop.toFloat()
        positionRect!!.bottom = (this.height - this.paddingBottom).toFloat()

        canvas.drawRoundRect(positionRect!!, radius, radius, backgroundPaint!!)
        indexSize = (this.height - this.paddingTop - paddingBottom) / sections.size

        textPaint!!.textSize = scaledWidth / 2

        for (i in sections.indices) {
            val x = leftPosition + textPaint!!.textSize / 2
            var calY:Float
            if (this.height - (scaledCompensation + indexSize * i) > 100)
                calY = scaledCompensation + paddingTop.toFloat() + indexerMargin + (indexSize * i).toFloat()
            else
                calY = scaledCompensation + paddingTop.toFloat() + (indexSize * i).toFloat()

            canvas.drawText(sections[i]?.toUpperCase(), x, calY, textPaint!!)
        }

        sectionTextPaint!!.textSize = 50 * scaledDensity
        if (useSection && showLetter and !TextUtils.isEmpty(section)) {
            val mPreviewPadding = 5 * density
            val previewTextWidth = sectionTextPaint!!.measureText(section!!.toUpperCase())
            val previewSize = 2 * mPreviewPadding + sectionTextPaint!!.descent() - sectionTextPaint!!.ascent()

            sectionPositionRect!!.left = (width - previewSize) / 2
            sectionPositionRect!!.right = (width - previewSize) / 2 + previewSize
            sectionPositionRect!!.top = (height - previewSize) / 2
            sectionPositionRect!!.bottom = (height - previewSize) / 2 + previewSize

            canvas.drawRoundRect(sectionPositionRect!!, mPreviewPadding, mPreviewPadding, sectionBackgroundPaint!!)
            canvas.drawText(
                section!!.toUpperCase(),
                sectionPositionRect!!.left + (previewSize - previewTextWidth) / 2 - 1,
                sectionPositionRect!!.top + mPreviewPadding - sectionTextPaint!!.ascent() + 1, sectionTextPaint!!
            )
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        val x = event.x
        mGesture!!.onTouchEvent(event)

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                if (x < leftPosition) {
                    return super.onTouchEvent(event)
                } else {
                    try {
                        val y = event.y - this.paddingTop.toFloat() - paddingBottom.toFloat()
                        val currentPosition = Math.floor((y / indexSize).toDouble()).toInt()
                        section = sections[currentPosition]
                        showLetter = true
                        this.setSelection((adapter as SectionIndexer).getPositionForSection(currentPosition))
                    } catch (e: Exception) {
                        Log.v(
                            KoreanIndexerListView::class.java!!.getSimpleName(),
                            "Something error happened. but who ever care this exception? " + e.message
                        )
                    }

                }
            }

            MotionEvent.ACTION_MOVE -> {
                if (x < leftPosition) {
                    return super.onTouchEvent(event)
                } else {
                    try {
                        val y = event.y
                        val currentPosition = Math.floor((y / indexSize).toDouble()).toInt()
                        section = sections[currentPosition]
                        showLetter = true
                        this.setSelection((adapter as SectionIndexer).getPositionForSection(currentPosition))
                    } catch (e: Exception) {
                        Log.v(
                            KoreanIndexerListView::class.java!!.getSimpleName(),
                            "Something error happened. but who ever care this exception? " + e.message
                        )
                    }

                }

            }

            MotionEvent.ACTION_UP -> {
                listHandler.postDelayed(showLetterRunnable, delayMillis.toLong())
            }
        }
        return true
    }

    abstract class KoreanIndexerAdapter<T>(context: Context, list: ArrayList<T>) : ArrayAdapter<T>(context, 0, list),
        SectionIndexer {

        override fun getSections(): Array<Any> {
            //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.

            return KoreanIndexerListView.sections as Array<Any>
        }
        /*override fun getSections(): Array<Any> {
            return sections
        }*/

        override fun getPositionForSection(section: Int): Int {
            val letter = sections[section]
            return mapIndex.get(letter)!!
        }

        override fun getSectionForPosition(position: Int): Int {
            return 0
        }
    }

    // this minimum class forked from https://github.com/bangjunyoung/KoreanTextMatcher
    /*
     * Copyright 2014 Bang Jun-young
     * All rights reserved.
     *
     * Redistribution and use in source and binary forms, with or without
     * modification, are permitted provided that the following conditions
     * are met:
     * 1. Redistributions of source code must retain the above copyright
     *    notice, this list of conditions and the following disclaimer.
     * 2. Redistributions in binary form must reproduce the above copyright
     *    notice, this list of conditions and the following disclaimer in the
     *    documentation and/or other materials provided with the distribution.
     *
     * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
     * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
     * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
     * IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
     * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
     * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
     * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
     * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
     * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
     * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
     */
    private object KoreanChar {

        private val CHOSEONG_COUNT = 19
        private val JUNGSEONG_COUNT = 21
        private val JONGSEONG_COUNT = 28
        private val HANGUL_SYLLABLE_COUNT = CHOSEONG_COUNT * JUNGSEONG_COUNT * JONGSEONG_COUNT
        private val HANGUL_SYLLABLES_BASE = 0xAC00
        private val HANGUL_SYLLABLES_END = HANGUL_SYLLABLES_BASE + HANGUL_SYLLABLE_COUNT

        private val COMPAT_CHOSEONG_MAP = intArrayOf(
            0x3131,
            0x3132,
            0x3134,
            0x3137,
            0x3138,
            0x3139,
            0x3141,
            0x3142,
            0x3143,
            0x3145,
            0x3146,
            0x3147,
            0x3148,
            0x3149,
            0x314A,
            0x314B,
            0x314C,
            0x314D,
            0x314E
        )


        private fun isSyllable(c: Char): Boolean {
            return HANGUL_SYLLABLES_BASE <= c.toInt() && c.toInt() < HANGUL_SYLLABLES_END
        }

        fun getCompatChoseong(value: Char): Char {
            if (!isSyllable(value))
                return '\u0000'

            val choseongIndex = getChoseongIndex(value)
            return COMPAT_CHOSEONG_MAP[choseongIndex].toChar()
        }

        private fun getChoseongIndex(syllable: Char): Int {
            val syllableIndex = syllable.toInt() - HANGUL_SYLLABLES_BASE
            return syllableIndex / (JUNGSEONG_COUNT * JONGSEONG_COUNT)
        }
    }// Can never be instantiated.

    // this class come from http://reimaginer.tistory.com/entry/한글영어특수문자-순-정렬하는-java-compare-메서드-만들기
    object OrderingByKorean {
        private val REVERSE = -1
        private val LEFT_FIRST = -1
        private val RIGHT_FIRST = 1

        val comparator: Comparator<String>
            get() = Comparator { left, right -> OrderingByKorean.compare(left, right) }

        fun compare(left: String, right: String): Int {
            var left = left
            var right = right

            left = StringUtils.upperCase(left).replace(" ".toRegex(), "")
            right = StringUtils.upperCase(right).replace(" ".toRegex(), "")

            val leftLen = left.length
            val rightLen = right.length
            val minLen = Math.min(leftLen, rightLen)

            for (i in 0 until minLen) {
                val leftChar = left[i]
                val rightChar = right[i]

                if (leftChar != rightChar) {
                    return if (isKoreanAndEnglish(leftChar, rightChar) || isKoreanAndNumber(leftChar, rightChar)
                        || isEnglishAndNumber(leftChar, rightChar) || isKoreanAndSpecial(leftChar, rightChar)
                    ) {
                        (leftChar - rightChar) * REVERSE
                    } else if (isEnglishAndSpecial(leftChar, rightChar) || isNumberAndSpecial(leftChar, rightChar)) {
                        if (isEnglish(leftChar) || isNumber(leftChar)) {
                            LEFT_FIRST
                        } else {
                            RIGHT_FIRST
                        }
                    } else {
                        leftChar - rightChar
                    }
                }
            }

            return leftLen - rightLen
        }

        private fun isKoreanAndEnglish(ch1: Char, ch2: Char): Boolean {
            return isEnglish(ch1) && isKorean(ch2) || isKorean(ch1) && isEnglish(ch2)
        }

        private fun isKoreanAndNumber(ch1: Char, ch2: Char): Boolean {
            return isNumber(ch1) && isKorean(ch2) || isKorean(ch1) && isNumber(ch2)
        }

        private fun isEnglishAndNumber(ch1: Char, ch2: Char): Boolean {
            return isNumber(ch1) && isEnglish(ch2) || isEnglish(ch1) && isNumber(ch2)
        }

        private fun isKoreanAndSpecial(ch1: Char, ch2: Char): Boolean {
            return isKorean(ch1) && isSpecial(ch2) || isSpecial(ch1) && isKorean(ch2)
        }

        private fun isEnglishAndSpecial(ch1: Char, ch2: Char): Boolean {
            return isEnglish(ch1) && isSpecial(ch2) || isSpecial(ch1) && isEnglish(ch2)
        }

        private fun isNumberAndSpecial(ch1: Char, ch2: Char): Boolean {
            return isNumber(ch1) && isSpecial(ch2) || isSpecial(ch1) && isNumber(ch2)
        }

        private fun isEnglish(ch: Char): Boolean {
            return ch.toInt() >= 'A'.toInt() && ch.toInt() <= 'Z'.toInt() || ch.toInt() >= 'a'.toInt() && ch.toInt() <= 'z'.toInt()
        }

        fun isKorean(ch: Char): Boolean {
            return ch.toInt() >= Integer.parseInt("AC00", 16) && ch.toInt() <= Integer.parseInt("D7A3", 16)
        }

        private fun isNumber(ch: Char): Boolean {
            return ch.toInt() >= '0'.toInt() && ch.toInt() <= '9'.toInt()
        }

        private fun isSpecial(ch: Char): Boolean {
            return (ch.toInt() >= '!'.toInt() && ch.toInt() <= '/'.toInt() // !"#$%&'()*+,-./

                    || ch.toInt() >= ':'.toInt() && ch.toInt() <= '@'.toInt() //:;<=>?@

                    || ch.toInt() >= '['.toInt() && ch.toInt() <= '`'.toInt() //[\]^_`

                    || ch.toInt() >= '{'.toInt() && ch.toInt() <= '~'.toInt()) //{|}~
        }
    }

    companion object {
        var sections = arrayOf<String?>()
        private var mapIndex = LinkedHashMap<String, Int>()
    }

}
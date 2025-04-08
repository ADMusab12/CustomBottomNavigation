package com.tenclouds.fluidbottomnavigation

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.os.SystemClock
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.annotation.VisibleForTesting
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.tenclouds.fluidbottomnavigation.extension.calculateHeight
import com.tenclouds.fluidbottomnavigation.extension.setTintColor
import com.tenclouds.fluidbottomnavigation.listener.OnTabSelectedListener
import com.tenclouds.fluidbottomnavigation.view.CircleView
import com.tenclouds.fluidbottomnavigation.view.IconView
import com.tenclouds.fluidbottomnavigation.view.RectangleView
import com.tenclouds.fluidbottomnavigation.view.TitleView
import kotlin.math.abs

class FluidBottomNavigation : FrameLayout {

    constructor(context: Context) : super(context) {
        init(null)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(attrs)
    }

    var items: List<FluidBottomNavigationItem> = listOf()
        set(value) {
            check(value.size >= 3) { resources.getString(R.string.exception_not_enough_items) }
            check(value.size <= 5) { resources.getString(R.string.exception_too_many_items) }

            field = value
            drawLayout()
        }

    var onTabSelectedListener: OnTabSelectedListener? = null

    var accentColor: Int = ContextCompat.getColor(context, R.color.accentColor)
    var backColor: Int = ContextCompat.getColor(context, R.color.backColor)
    var iconColor: Int = ContextCompat.getColor(context, R.color.textColor)
    var iconSelectedColor: Int = ContextCompat.getColor(context, R.color.iconColor)
    var textColor: Int = ContextCompat.getColor(context, R.color.iconSelectedColor)
    var textFont: Typeface = ResourcesCompat.getFont(context, R.font.rubik_regular)
            ?: Typeface.DEFAULT

    val selectedTabItem: FluidBottomNavigationItem? get() = items[selectedTabPosition]

    private var bottomBarHeight = resources.getDimension(R.dimen.fluidBottomNavigationHeightWithOpacity).toInt()
    private var bottomBarWidth = 0

    @VisibleForTesting
    internal var isVisible = true

    private var selectedTabPosition = DEFAULT_SELECTED_TAB_POSITION
        set(value) {
            field = value
            onTabSelectedListener?.onTabSelected(value)
        }

    private var backgroundView: View? = null
    private val views: MutableList<View> = ArrayList()
    private var lastItemClickTimestamp = 0L

    private fun init(attrs: AttributeSet?) {
        getAttributesOrDefaultValues(attrs)
        clipToPadding = false
        layoutParams =
                ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        bottomBarHeight)
    }

    fun selectTab(position: Int) {
        if (position == selectedTabPosition) return

        if (views.size > 0) {
            views[selectedTabPosition].animateDeselectItemView()
            views[position].animateSelectItemView()
        }

        this.selectedTabPosition = position
    }

    fun show() {
        if (isVisible.not()) {
            animateShow()
            isVisible = true
        }
    }

    fun hide() {
        if (isVisible) {
            animateHide()
            isVisible = false
        }
    }

    private fun drawLayout() {
        bottomBarHeight = resources.getDimension(R.dimen.fluidBottomNavigationHeightWithOpacity).toInt()
        backgroundView = View(context)

        removeAllViews()
        views.clear()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    calculateHeight(bottomBarHeight)
            ).let {
                addView(backgroundView, it)
            }
        }

        post { requestLayout() }

        LinearLayout(context)
                .apply {
                    orientation = LinearLayout.HORIZONTAL
                    gravity = Gravity.CENTER
                }
                .let { linearLayoutContainer ->
                    val layoutParams =
                            LayoutParams(
                                    ViewGroup.LayoutParams.MATCH_PARENT,
                                    bottomBarHeight,
                                    Gravity.BOTTOM)
                    addView(linearLayoutContainer, layoutParams)
                    post {
                        bottomBarWidth = width
                        drawItemsViews(linearLayoutContainer)
                    }
                }
    }

    private fun drawItemsViews(linearLayout: LinearLayout) {
        if (bottomBarWidth == 0 || items.isEmpty()) {
            return
        }

        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        val itemViewHeight = resources.getDimension(R.dimen.fluidBottomNavigationHeightWithOpacity)
        val itemViewWidth = (bottomBarWidth / items.size)

        for (itemPosition in items.indices) {
            inflater
                    .inflate(R.layout.item, this, false)
                    .let {
                        views.add(it)
                        linearLayout
                                .addView(it,
                                        LayoutParams(
                                                itemViewWidth,
                                                itemViewHeight.toInt()))
                    }
            drawItemView(itemPosition)
        }
    }

    private fun drawItemView(position: Int) {
        val view = views[position]
        val item = items[position]

        val container = view.findViewById<ConstraintLayout>(R.id.container)
        val icon = view.findViewById<IconView>(R.id.icon)
        val title = view.findViewById<TitleView>(R.id.title)
        val circle = view.findViewById<CircleView>(R.id.circle)
        val rectangle = view.findViewById<RectangleView>(R.id.rectangle)
        val backgroundContainer = view.findViewById<ConstraintLayout>(R.id.backgroundContainer)

        with(view) {
            if (items.size > 3) {
                container.setPadding(0, 0, 0, container.paddingBottom)
            }

            with(icon) {
                selectColor = iconSelectedColor
                deselectColor = iconColor

                setImageDrawable(item.drawable)
                if (selectedTabPosition == position)
                    views[position].animateSelectItemView()
                else
                    setTintColor(deselectColor)
            }
            with(title) {
                typeface = textFont
                setTextColor(this@FluidBottomNavigation.textColor)
                text = item.title
                setTextSize(
                        TypedValue.COMPLEX_UNIT_PX,
                        resources.getDimension(R.dimen.fluidBottomNavigationTextSize))
            }
            with(circle) {
                setTintColor(accentColor)
            }
            with(rectangle) {
                setTintColor(accentColor)
            }

            changeBackgroundOnSelect(selectedTabPosition)

            backgroundContainer.setOnClickListener {
                val nowTimestamp = SystemClock.uptimeMillis()
                if (abs(lastItemClickTimestamp - nowTimestamp) > ITEMS_CLICKS_DEBOUNCE) {
                    selectTab(position)
                    changeBackgroundOnSelect(position)
                    lastItemClickTimestamp = nowTimestamp
                }
            }
        }
    }

    private fun changeBackgroundOnSelect(position: Int) {
        when (position) {
            0 -> setBackgroundColor(Color.parseColor("#D84879"))
            1 -> setBackgroundColor(Color.parseColor("#4CAF50"))
            2 -> setBackgroundColor(Color.parseColor("#ffa500"))
            3 -> setBackgroundColor(Color.parseColor("#ff69b4"))
            4 -> setBackgroundColor(Color.parseColor("#6495ed"))
            else -> setBackgroundColor(Color.parseColor("#D84879"))
        }
    }

    fun getTabsSize() = items.size

    private fun getAttributesOrDefaultValues(attrs: AttributeSet?) {
        if (attrs != null) {
            with(context
                    .obtainStyledAttributes(
                            attrs,
                            R.styleable.FluidBottomNavigation,
                            0, 0)) {
                selectedTabPosition = getInt(
                        R.styleable.FluidBottomNavigation_defaultTabPosition,
                        DEFAULT_SELECTED_TAB_POSITION)
                accentColor = getColor(
                        R.styleable.FluidBottomNavigation_accentColor,
                        ContextCompat.getColor(context, R.color.accentColor))
                backColor = getColor(
                        R.styleable.FluidBottomNavigation_backColor,
                        ContextCompat.getColor(context, R.color.backColor))
                iconColor = getColor(
                        R.styleable.FluidBottomNavigation_iconColor,
                        ContextCompat.getColor(context, R.color.iconColor))
                textColor = getColor(
                        R.styleable.FluidBottomNavigation_textColor,
                        ContextCompat.getColor(context, R.color.iconSelectedColor))
                iconSelectedColor = getColor(
                        R.styleable.FluidBottomNavigation_iconSelectedColor,
                        ContextCompat.getColor(context, R.color.iconSelectedColor))
                textFont = ResourcesCompat.getFont(
                        context,
                        getResourceId(
                                R.styleable.FluidBottomNavigation_textFont,
                                R.font.rubik_regular)) ?: Typeface.DEFAULT
                recycle()
            }
        }
    }

    fun getSelectedTabPosition() = this.selectedTabPosition

    override fun onSaveInstanceState() =
            Bundle()
                    .apply {
                        putInt(EXTRA_SELECTED_TAB_POSITION, selectedTabPosition)
                        putParcelable(EXTRA_SELECTED_SUPER_STATE, super.onSaveInstanceState())
                    }

    override fun onRestoreInstanceState(state: Parcelable?) =
            if (state is Bundle?) {
                selectedTabPosition = state
                        ?.getInt(EXTRA_SELECTED_TAB_POSITION) ?: DEFAULT_SELECTED_TAB_POSITION
                state?.getParcelable(EXTRA_SELECTED_SUPER_STATE)
            } else {
                state
            }
                    .let {
                        super.onRestoreInstanceState(it)
                    }
}

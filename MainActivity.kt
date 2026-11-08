package br.com.minimallauncher

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import java.text.Collator
import java.util.Locale

class MainActivity : Activity() {

    private lateinit var appGrid: GridLayout
    private val columns = 4
    private val backgroundColor = Color.rgb(16, 16, 16)
    private val labelColor = Color.WHITE

    data class AppEntry(
        val label: String,
        val packageName: String,
        val icon: Drawable
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = backgroundColor
        window.navigationBarColor = backgroundColor
        window.decorView.systemUiVisibility = 0

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(backgroundColor)
        }

        val header = TextView(this).apply {
            text = "Aplicativos"
            textSize = 21f
            setTextColor(labelColor)
            typeface = Typeface.create("sans-serif-medium", Typeface.NORMAL)
            gravity = Gravity.CENTER
            setPadding(12, dp(22), 12, dp(18))
        }
        root.addView(header, LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
        ))

        val scroll = ScrollView(this).apply {
            isFillViewport = false
            overScrollMode = View.OVER_SCROLL_NEVER
        }

        appGrid = GridLayout(this).apply {
            columnCount = columns
            useDefaultMargins = false
            setPadding(dp(6), dp(4), dp(6), dp(18))
        }
        scroll.addView(appGrid, ScrollView.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
        ))
        root.addView(scroll, LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f
        ))

        setContentView(root)
        loadApps()
    }

    override fun onResume() {
        super.onResume()
        if (::appGrid.isInitialized) loadApps()
    }

    private fun loadApps() {
        val launcherIntent = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER)
        val pm = packageManager

        val resolved = if (android.os.Build.VERSION.SDK_INT >= 33) {
            pm.queryIntentActivities(
                launcherIntent,
                PackageManager.ResolveInfoFlags.of(PackageManager.MATCH_ALL.toLong())
            )
        } else {
            @Suppress("DEPRECATION")
            pm.queryIntentActivities(launcherIntent, PackageManager.MATCH_ALL)
        }

        val collator = Collator.getInstance(Locale("pt", "BR")).apply {
            strength = Collator.PRIMARY
        }

        val apps = resolved.mapNotNull { info ->
            try {
                AppEntry(
                    label = info.loadLabel(pm).toString(),
                    packageName = info.activityInfo.packageName,
                    icon = info.loadIcon(pm)
                )
            } catch (_: Exception) {
                null
            }
        }.distinctBy { it.packageName + "/" + it.label }
            .sortedWith { a, b ->
                val result = collator.compare(a.label, b.label)
                if (result != 0) result else a.packageName.compareTo(b.packageName)
            }

        appGrid.removeAllViews()
        apps.forEach { app ->
            appGrid.addView(createAppCell(app))
        }
    }

    private fun createAppCell(app: AppEntry): View {
        val cell = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(dp(3), dp(10), dp(3), dp(10))
            isClickable = true
            isFocusable = true
            setOnClickListener {
                val launchIntent = packageManager.getLaunchIntentForPackage(app.packageName)
                if (launchIntent != null) {
                    launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    try {
                        startActivity(launchIntent)
                    } catch (_: Exception) {
                    }
                }
            }
        }

        val iconView = ImageView(this).apply {
            setImageDrawable(app.icon)
            scaleType = ImageView.ScaleType.FIT_CENTER
            contentDescription = app.label
        }
        cell.addView(iconView, LinearLayout.LayoutParams(dp(46), dp(46)))

        val label = TextView(this).apply {
            text = app.label
            textSize = 11f
            setTextColor(labelColor)
            gravity = Gravity.CENTER
            maxLines = 2
            ellipsize = android.text.TextUtils.TruncateAt.END
            setPadding(0, dp(5), 0, 0)
        }
        cell.addView(label, LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
        ))

        val params = GridLayout.LayoutParams().apply {
            width = 0
            height = ViewGroup.LayoutParams.WRAP_CONTENT
            columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
            setMargins(dp(1), 0, dp(1), 0)
        }
        cell.layoutParams = params
        return cell
    }

    private fun dp(value: Int): Int =
        (value * resources.displayMetrics.density + 0.5f).toInt()
}

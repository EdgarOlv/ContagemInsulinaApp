package com.example.contagemglicemia.modules.Report.pages.page2

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.androidplot.xy.CatmullRomInterpolator
import com.androidplot.xy.LineAndPointFormatter
import com.androidplot.xy.PanZoom
import com.androidplot.xy.SimpleXYSeries
import com.androidplot.xy.XYGraphWidget
import com.androidplot.xy.XYSeries
import com.example.contagemglicemia.databinding.FragmentReportTwoBinding
import java.text.FieldPosition
import java.text.Format
import java.text.ParsePosition
import java.util.Arrays

class ReportTwo : Fragment() {

    private lateinit var binding: FragmentReportTwoBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentReportTwoBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val plot = binding.chartGlicemy

        val domainLabels = arrayOf<Number>(1, 2, 3, 6, 7, 8, 9, 10, 13, 14)
        val series1Number = arrayOf<Number>(1, 4, 8, 12, 16, 32, 26, 29, 10, 13)
        val series2Number = arrayOf<Number>(2, 8, 4, 7, 32, 16, 64, 12, 7, 10)

        val series1: XYSeries = SimpleXYSeries(
            Arrays.asList(* series1Number),
            SimpleXYSeries.ArrayFormat.Y_VALS_ONLY,
            "Series 1",
        )
        val series2: XYSeries = SimpleXYSeries(
            Arrays.asList(* series2Number),
            SimpleXYSeries.ArrayFormat.Y_VALS_ONLY,
            "Series 1",
        )

        val series1Format = LineAndPointFormatter(Color.BLUE, Color.BLACK, null, null)
        val series2Format = LineAndPointFormatter(Color.DKGRAY, Color.LTGRAY, null, null)

        series1Format.setInterpolationParams(
            CatmullRomInterpolator.Params(
                10,
                CatmullRomInterpolator.Type.Centripetal,
            ),
        )
        series2Format.setInterpolationParams(
            CatmullRomInterpolator.Params(
                10,
                CatmullRomInterpolator.Type.Centripetal,
            ),
        )

        plot.addSeries(series1, series1Format)
        plot.addSeries(series2, series2Format)

        plot.graph.getLineLabelStyle(XYGraphWidget.Edge.BOTTOM).format = object : Format() {
            override fun format(
                obj: Any?,
                toAppendTo: StringBuffer,
                pos: FieldPosition,
            ): StringBuffer {
                val i = Math.round((obj as Number).toFloat())
                return toAppendTo.append(domainLabels[i])
            }

            override fun parseObject(source: String?, pos: ParsePosition): Any? {
                return null
            }
        }
        PanZoom.attach(plot)
    }

    companion object {
        fun newInstance() = ReportTwo()
    }
}

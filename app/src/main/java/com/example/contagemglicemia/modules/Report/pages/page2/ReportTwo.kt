package com.example.contagemglicemia.modules.Report.pages.page2

import android.graphics.Color
import android.graphics.DashPathEffect
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebSettings
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.androidplot.util.PixelUtils
import com.androidplot.xy.BoundaryMode
import com.androidplot.xy.CatmullRomInterpolator
import com.androidplot.xy.LineAndPointFormatter
import com.androidplot.xy.PanZoom
import com.androidplot.xy.SimpleXYSeries
import com.androidplot.xy.XYGraphWidget
import com.androidplot.xy.XYSeries
import com.example.contagemglicemia.R
import com.example.contagemglicemia.dao.MyDatabaseManager
import com.example.contagemglicemia.databinding.FragmentReportTwoBinding
import com.example.contagemglicemia.model.Glicemia
import com.example.contagemglicemia.modules.Report.ReportViewModel
import com.example.contagemglicemia.modules.Report.pages.page1.adapter.RegistroGlicemiaAdapter
import com.example.contagemglicemia.utils.EventObserver
import java.text.FieldPosition
import java.text.Format
import java.text.ParsePosition
import java.util.Arrays

class ReportTwo : Fragment() {

    private lateinit var binding: FragmentReportTwoBinding
    private lateinit var viewModel: ReportViewModel

    private lateinit var dbManager: MyDatabaseManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

        dbManager = MyDatabaseManager(requireContext())
        setupViewModel()
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentReportTwoBinding.inflate(inflater, container, false)


        /*val glicemias = listOf(
            Glicemia(1, 110, "30 Jan", 5, "Pós café", 1),
            Glicemia(2, 140, "31 Jan", 6, "Pós almoço", 1),
            Glicemia(3, 90, "01 Fev", 4, "Jejum", 1),
            Glicemia(4, 130, "02 Fev", 5, "Antes do treino", 1),
        )*/


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /*val plot = binding.chartGlicemy

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
        PanZoom.attach(plot)*/


        viewModel.getAllGlicemy(dbManager)
        setupObserver()
    }
    private fun setupObserver() {
        viewModel.listGlicemy.observe(
            viewLifecycleOwner,
            EventObserver { list ->

                val glicemiaPlot = binding.glicemiaPlot

                val glicemias = list

                val domainLabels = glicemias.map { it.date }
                val seriesData = glicemias.mapIndexed { index, data -> index to data.value }

                val series = SimpleXYSeries(
                    seriesData.map { it.second },
                    SimpleXYSeries.ArrayFormat.Y_VALS_ONLY,
                    "Glicemia"
                )

                val lineFormatter = LineAndPointFormatter(Color.BLUE, Color.RED, null, null)
                glicemiaPlot.addSeries(series, lineFormatter)

                // 🔹 Fundo branco no gráfico
                glicemiaPlot.graph.backgroundPaint.color = Color.WHITE
                glicemiaPlot.backgroundPaint.color = Color.WHITE

                // 🔹 Configuração dos eixos X e Y
                glicemiaPlot.graph.domainGridLinePaint.color = Color.GRAY
                glicemiaPlot.graph.rangeGridLinePaint.color = Color.GRAY

                glicemiaPlot.graph.domainOriginLinePaint.color = Color.BLACK
                glicemiaPlot.graph.rangeOriginLinePaint.color = Color.BLACK

                // 🔹 Linhas pontilhadas nos eixos para melhorar a visibilidade
                glicemiaPlot.graph.rangeGridLinePaint.pathEffect = DashPathEffect(floatArrayOf(10f, 10f), 0f)
                glicemiaPlot.graph.domainGridLinePaint.pathEffect = DashPathEffect(floatArrayOf(10f, 10f), 0f)

                // 🔹 Configuração dos rótulos no eixo X (tempo)
                glicemiaPlot.graph.getLineLabelStyle(XYGraphWidget.Edge.BOTTOM).format = object : Format() {
                    override fun format(obj: Any, toAppendTo: StringBuffer, pos: FieldPosition): StringBuffer {
                        return toAppendTo.append(domainLabels[(obj as Number).toInt()])
                    }

                    override fun parseObject(source: String, pos: ParsePosition): Any? {
                        return null
                    }
                }

                // 🔹 Configuração dos labels no eixo Y
                glicemiaPlot.rangeTitle.text = "Glicemia (mg/dL)"
                glicemiaPlot.domainTitle.text = "Data"

                // 🔹 Ajuste da escala
                glicemiaPlot.setRangeBoundaries(50, 200, BoundaryMode.FIXED)
                glicemiaPlot.setDomainBoundaries(0, glicemias.size - 1, BoundaryMode.FIXED)

                // 🔹 Melhorando a legibilidade dos rótulos
                glicemiaPlot.graph.getLineLabelStyle(XYGraphWidget.Edge.LEFT).paint.textSize = 40f
                glicemiaPlot.graph.getLineLabelStyle(XYGraphWidget.Edge.BOTTOM).paint.textSize = 40f


            },
        )
    }

    private fun setupViewModel() {
        val viewModelFactory = ReportViewModel.Factory()
        viewModel = ViewModelProvider(this, viewModelFactory)
            .get(ReportViewModel::class.java)
    }



    companion object {
        fun newInstance() = ReportTwo()
    }
}

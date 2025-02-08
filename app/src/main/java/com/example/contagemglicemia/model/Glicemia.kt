package com.example.contagemglicemia.model

import android.os.Build

data class Glicemia(
    var id: Int,
    var value: Int,
    var date: String,
    var insulinaApply: Int,
    var observation: String,
    var sync: Int,
    var loc: String
)

data class GlicemiaClean(
    var valor: Int = 0,
    var data: String = "",
    var insulina_aplicada: Int = 0,
    var loc: String = Build.MODEL,
    var sync: Int = 1,
)

fun Glicemia.toGlicemiaCloud() = GlicemiaClean(
    valor = this.value,
    data = this.date,
    insulina_aplicada = this.insulinaApply,
    loc = Build.MODEL,
)

fun GlicemiaClean.toGlicemia() = Glicemia(
    id = 0,
    value = this.valor,
    date = this.data,
    insulinaApply = this.insulina_aplicada,
    observation = "",
    sync = sync,
    loc = this.loc
)


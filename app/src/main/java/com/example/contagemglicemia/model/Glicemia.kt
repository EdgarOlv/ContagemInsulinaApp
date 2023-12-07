package com.example.contagemglicemia.model

data class Glicemia(
    var id: Int,
    var value: Int,
    var date: String,
    var insulina_apply: Int
)

data class GlicemiaClean(
    var valor: Int = 0,
    var data: String = "",
    var insulina_aplicada: Int = 0,
    var loc: String = "android"
)

fun Glicemia.toGlicemiaCloud() = GlicemiaClean(
    valor = this.value,
    data = this.date,
    insulina_aplicada = this.insulina_apply,
    loc = "android"
)

fun GlicemiaClean.toGlicemia() = Glicemia(
    id = 0,
    value = this.valor,
    date = this.data,
    insulina_apply = this.insulina_aplicada
)
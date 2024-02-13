package com.example.contagemglicemia.model

import android.os.Build
import java.text.SimpleDateFormat
import java.util.Date
import java.util.TimeZone

data class Glicemia(
    var id: Int,
    var value: Int,
    var date: Date,
    var insulina_apply: Int,
    var observation: String,
    var sync: Int,

)

data class GlicemiaClean(
    var valor: Int = 0,
    var data: String = "",
    var insulina_aplicada: Int = 0,
    var loc: String = Build.DEVICE,
    var sync: Int = 1,
)

fun Glicemia.toGlicemiaCloud() = GlicemiaClean(
    valor = this.value,
    data = this.date.toString(),
    insulina_aplicada = this.insulina_apply,
    loc = Build.DEVICE,
)

fun GlicemiaClean.toGlicemia() = Glicemia(
    id = 0,
    value = this.valor,
    date = dateFormat.parse(this.data)!!,
    insulina_apply = this.insulina_aplicada,
    observation = "",
    sync = sync,
)

val timeZoneBahia = TimeZone.getTimeZone("America/Bahia")
val dateFormat = SimpleDateFormat("dd-MM-yyyy HH:mm:ss")

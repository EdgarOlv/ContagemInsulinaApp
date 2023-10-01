package com.example.contagemglicemia.Model

import java.util.*

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

class TreinoGym
{


    /*
     * Caso 1: x<70 =  Adiar
     * Caso 2: 70>x<100 =  25g a 50g de carb
     * Caso 3: 100x<180 =  20g a 50g carb antes
     * Caso 4: 180x<300 =  15g antes
     * Caso 5: 300>x =   Adiar
     *
     */

}

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
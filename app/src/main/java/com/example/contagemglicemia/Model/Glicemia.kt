package com.example.contagemglicemia.Model

import java.util.*

data class Glicemia(
    var id: Int,
    var value: Int,
    var date: String,
    var insulina_apply: Int
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
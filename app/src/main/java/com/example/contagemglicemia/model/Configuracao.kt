package com.example.contagemglicemia.model

data class Configuracao(
    var id: Int,
    var name: String,
    var value: Int
)

data class CarboAlimento(
    var cafe: Int,
    var lancheM: Int,
    var almoco: Int,
    var lancheT: Int,
    var jantar: Int,
    var ceia: Int
)

data class ConfigModel(
    var glicemiaAlvo: Int,
    var fatorSensibilidade: Int,
    var relacaoCarbo: Int,
)

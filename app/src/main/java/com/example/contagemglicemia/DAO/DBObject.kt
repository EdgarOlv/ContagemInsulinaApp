package com.example.contagemglicemia.DAO

object TableGlicemia {
    const val TABLE_NAME = "glicemias"
    const val ID = "id"
    const val VALUE = "valor"
    const val DATE = "data"
    const val INSULIN_APPLY = "insulina_aplicada"
    const val OBS = "obs"
}

object TableConfig {
    const val TABLE_NAME = "config"
    const val ID = "id"
    const val NAME = "nome"
    const val VALUE = "valor"
}

object TableFood {
    const val TABLE_NAME = "alimentos"
    const val ID = "id"
    const val ID_NAME = "id_nome"
    const val NAME = "nome"
    const val QTD_CARBO = "qtd_carboidrato"
}

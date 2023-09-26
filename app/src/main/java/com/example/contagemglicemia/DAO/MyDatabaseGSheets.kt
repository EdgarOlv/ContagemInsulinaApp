package com.example.contagemglicemia.DAO

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.contagemglicemia.Model.Glicemia
import com.example.contagemglicemia.R
import com.google.api.client.auth.oauth2.Credential
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.JsonFactory
import com.google.api.client.json.gson.GsonFactory
import com.google.api.client.util.store.FileDataStoreFactory
import com.google.api.services.sheets.v4.Sheets
import com.google.api.services.sheets.v4.SheetsScopes
import com.google.api.services.sheets.v4.model.ValueRange
import java.io.File
import java.io.FileNotFoundException
import java.io.InputStreamReader
import java.nio.charset.Charset
import java.util.*
import kotlin.collections.HashMap


class MyDatabaseGSheets(context: Context) {
    private lateinit var service: Sheets

    private val scopes = listOf("https://www.googleapis.com/auth/spreadsheets")
    private val applicationName = "AppGlicemia"
    private val spreadsheetID = "1fEalyNvWO89y1KpfMVVYmijQvMHWuA20OaOGpv4ffBk"
    private val sheet = "Pag1"

    fun connectDB(context: Context) {
        try {
            val credentials = context.resources.openRawResource(R.raw.credentials)
            val credential = GoogleCredential.fromStream(credentials)
                .createScoped(scopes)

            val transport = NetHttpTransport()
            val jsonFactory: JsonFactory = GsonFactory.getDefaultInstance()

            service = Sheets.Builder(transport, jsonFactory, credential)
                .setApplicationName(applicationName)
                .build()
            var teste = 1
        } catch (e: Exception) {
            showToast(context, "Erro ao conectar!")
        }
    }

    fun getGlicemiasNuvem(context: Context): MutableList<Glicemia> {
        try {
            connectDB(context)

            val range = "$sheet!A2:D2000"
            val request = service.spreadsheets().values().get(spreadsheetID, range)

            val response = request.execute()
            val values = response.getValues()
            val listGlicemia = mutableListOf<Glicemia>()

            if (values != null && values.isNotEmpty()) {
                for (row in values) {
                    var coluna1 = if (row.size > 0) row[0].toString() else "0"
                    var coluna2 = if (row.size > 1) row[1].toString() else "0"
                    var coluna3 = if (row.size > 2) row[2].toString() else "0"
                    var coluna4 = if (row.size > 3) row[3].toString() else "0"

                    val valor1 = coluna1.toInt()
                    val valor2 = coluna2.toInt()

                    val glicemia = Glicemia(valor1, valor2, coluna3, coluna4.toInt())
                    listGlicemia.add(glicemia)

                    // Limpa as variáveis para a próxima iteração
                    coluna1 = ""
                    coluna2 = ""
                    coluna3 = ""
                    coluna4 = ""
                }
            } else {
                // listGlicemia.add(null)
                println("Sem dados.")
            }
            return listGlicemia
        } catch (ex: Exception) {
            showToast(context, "Erro ao buscar glicemias na Nuvem")
            return mutableListOf()
        }
    }

    fun postVolley(context: Context) {
        val queue = Volley.newRequestQueue(context)
        val url = "https://private-4c0e8-simplestapi3.apiary-mock.com/message"

        val requestBody = "id=1" + "&msg=test_msg"
        val stringReq : StringRequest =
            object : StringRequest(Method.POST, url,
                Response.Listener { response ->
                    // response
                    var strResp = response.toString()
                    Log.d("API", strResp)

                    fun getParams(): Map<String, String> {
                        val params: MutableMap<String, String> = HashMap()
                        params["user"] = "YOUR USERNAME"
                        params["pass"] = "YOUR PASSWORD"
                        return params
                    }
                },
                Response.ErrorListener { error ->
                    Log.d("API", "error => $error")
                }
            ){
                override fun getBody(): ByteArray {
                    return requestBody.toByteArray(Charset.defaultCharset())
                }
            }
        queue.add(stringReq)
    }

    fun insertGlicemiaNuvem(context: Context, glicemia: Glicemia) {
        try {
            connectDB(context)

            val range = "$sheet!A:E"
            val valueRange = ValueRange()

            val objectList = listOf<Any>(
                glicemia.value,
                glicemia.insulina_apply,
                glicemia.date,
                "",
                "pc"
            )
            valueRange.setValues(listOf(objectList))



            /*val stringRequest =
                StringRequest(com.android.volley.Request.Method.POST, "", object : Listener<String?>() {
                    fun onResponse(response: String?) {}
                }, object : ErrorListener() {
                    fun onResponse(response: String?) {}
                })*/

            val appendRequest = service.spreadsheets().values()
                .append(spreadsheetID, range, valueRange)
            appendRequest.valueInputOption = "USER_ENTERED"
            val appendResponse = appendRequest.execute()
        } catch (ex: Exception) {
            showToast(context, "Erro ao inserir na nuvem!")
        }
    }



    fun showToast(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}

object SheetsQuickstart {
    private const val APPLICATION_NAME = "Google Sheets API Java Quickstart"
    private val JSON_FACTORY: JsonFactory = GsonFactory.getDefaultInstance()
    private const val TOKENS_DIRECTORY_PATH = "tokens"

    /**
     * Global instance of the scopes required by this quickstart.
     * If modifying these scopes, delete your previously saved tokens/ folder.
     */
    private val SCOPES: List<String> = Collections.singletonList(SheetsScopes.SPREADSHEETS_READONLY)
    private const val CREDENTIALS_FILE_PATH = "/credentials.json"

    /**
     * Creates an authorized Credential object.
     *
     * @param HTTP_TRANSPORT The network HTTP Transport.
     * @return An authorized Credential object.
     * @throws IOException If the credentials.json file cannot be found.
     */

    private fun getCredentials(HTTP_TRANSPORT: NetHttpTransport): Credential {
        // Load client secrets.
        val `in` = SheetsQuickstart::class.java.getResourceAsStream(CREDENTIALS_FILE_PATH)
            ?: throw FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH)
        val clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, InputStreamReader(`in`))

        // Build flow and trigger user authorization request.
        val flow = GoogleAuthorizationCodeFlow.Builder(
            HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES
        )
            .setDataStoreFactory(FileDataStoreFactory(File(TOKENS_DIRECTORY_PATH)))
            .setAccessType("offline")
            .build()
        val receiver = LocalServerReceiver.Builder().setPort(8888).build()
        return AuthorizationCodeInstalledApp(flow, receiver).authorize("user")
    }

    /**
     * Prints the names and majors of students in a sample spreadsheet:
     * https://docs.google.com/spreadsheets/d/1BxiMVs0XRA5nFMdKvBdBZjgmUUqptlbs74OgvE2upms/edit
     */
    @JvmStatic
    fun main(args: Array<String>) {
        // Build a new authorized API client service.
        val HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport()
        val spreadsheetId = "1BxiMVs0XRA5nFMdKvBdBZjgmUUqptlbs74OgvE2upms"
        val range = "Class Data!A2:E"
        val service = Sheets.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
            .setApplicationName(APPLICATION_NAME)
            .build()
        val response = service.spreadsheets().values()[spreadsheetId, range]
            .execute()
        val values = response.getValues()
        if (values == null || values.isEmpty()) {
            println("No data found.")
        } else {
            println("Name, Major")
            for (row in values) {
                // Print columns A and E, which correspond to indices 0 and 4.
                System.out.printf("%s, %s\n", row[0], row[4])
            }
        }
    }
}
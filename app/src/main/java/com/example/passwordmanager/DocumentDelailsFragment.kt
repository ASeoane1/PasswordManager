package com.example.passwordmanager

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class DocumentDetailsFragment : AppCompatActivity() {

    private lateinit var linearLayoutContainer: LinearLayout
    private lateinit var buttonSave: Button
    private var documentMap: MutableMap<String, String> = mutableMapOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_document_details)

        linearLayoutContainer = findViewById(R.id.linearLayoutContainer)

        val documentJson = intent.getStringExtra("document_json")
        val gson = Gson()
        documentMap = gson.fromJson(documentJson, object : TypeToken<MutableMap<String, String>>() {}.type)

        documentMap.forEach { (key, value) ->
            addEditTextForKeyValue(key, value)
        }

        buttonSave = Button(this).apply {
            text = "Save"
            setOnClickListener { saveChanges() }
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(0, 16, 0, 0)
            }
        }

        linearLayoutContainer.addView(buttonSave)
    }

    private fun addEditTextForKeyValue(key: String, value: String) {
        val horizontalLayout = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = 8
            }
        }

        val keyEditText = EditText(this).apply {
            setText(key)
            layoutParams = LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f
            ).apply {
                rightMargin = 8
            }
        }

        val valueEditText = EditText(this).apply {
            setText(value)
            layoutParams = LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f
            )
        }

        horizontalLayout.addView(keyEditText)
        horizontalLayout.addView(valueEditText)
        linearLayoutContainer.addView(horizontalLayout)
    }

    private fun saveChanges() {
        documentMap.clear()
        for (i in 0 until linearLayoutContainer.childCount - 1) {
            val horizontalLayout = linearLayoutContainer.getChildAt(i) as LinearLayout
            val keyEditText = horizontalLayout.getChildAt(0) as EditText
            val valueEditText = horizontalLayout.getChildAt(1) as EditText

            val key = keyEditText.text.toString()
            val value = valueEditText.text.toString()

            documentMap[key] = value
        }

        // Aquí puedes manejar el guardado de los datos actualizados en el HashMap
        // Por ejemplo, enviarlos de vuelta al servidor o guardarlos localmente

        Toast.makeText(this, "Changes saved!", Toast.LENGTH_SHORT).show()
    }
}

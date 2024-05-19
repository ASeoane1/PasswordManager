package com.example.passwordmanager

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DocumentListAdapter(private val context: Context, private val data: List<String>, private val token: String, private val user: String) :
    RecyclerView.Adapter<DocumentListAdapter.MyViewHolder>() {

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textView: TextView = itemView.findViewById(R.id.document_text)
        val buttonEdit: Button = itemView.findViewById(R.id.button_edit)
        val buttonDelete: Button = itemView.findViewById(R.id.button_delete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_document_list, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.textView.text = data[position]
        holder.buttonEdit.setOnClickListener {
            val documentName = data[position]
            val requestBody = DocumentRequest(user = user, document_name = documentName)

            RetrofitClient.getApiServiceWithToken(token).getDocument(requestBody).enqueue(object : Callback<DocumentResponse> {
                override fun onResponse(call: Call<DocumentResponse>, response: Response<DocumentResponse>) {
                    if (response.isSuccessful) {
                        val documentResponse = response.body()
                        val json = Gson().toJson(documentResponse?.response)
                        val intent = Intent(context, DocumentDetailsFragment::class.java).apply {
                            putExtra("document_json", json)
                        }
                        context.startActivity(intent)
                    } else {
                        Toast.makeText(context, "Error en la respuesta de la API", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<DocumentResponse>, t: Throwable) {
                    Toast.makeText(context, "Fallo en la llamada a la API", Toast.LENGTH_SHORT).show()
                }
            })
        }
        holder.buttonDelete.setOnClickListener {
            // Acción al hacer clic en el botón
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }
}

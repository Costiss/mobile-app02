package br.ufpr.musicbookmark

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.ListView
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import br.ufpr.musicbookmark.adapter.MusicAdapter
import br.ufpr.musicbookmark.controller.MusicController
import br.ufpr.musicbookmark.database.DBHelper
import br.ufpr.musicbookmark.model.Music
import com.google.android.material.textfield.TextInputEditText

class MainActivity : AppCompatActivity() {

    private lateinit var controller: MusicController
    private lateinit var adapter: MusicAdapter
    private lateinit var listView: ListView
    private lateinit var etBusca: TextInputEditText
    private lateinit var spinnerCampoBusca: Spinner
    private lateinit var spinnerOrdenacao: Spinner
    private lateinit var checkFavoritos: CheckBox
    private lateinit var spinnerFiltroStatus: Spinner

    private val columnMap = listOf(
        DBHelper.COL_TITLE,
        DBHelper.COL_ARTIST,
        DBHelper.COL_YEAR,
        DBHelper.COL_RATING,
        DBHelper.COL_GENRE
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        listView         = findViewById(R.id.listViewMusicas)
        etBusca          = findViewById(R.id.etBusca)
        spinnerCampoBusca = findViewById(R.id.spinnerCampoBusca)
        spinnerOrdenacao  = findViewById(R.id.spinnerOrdenacao)
        checkFavoritos      = findViewById(R.id.checkFavoritos)
        spinnerFiltroStatus = findViewById(R.id.spinnerFiltroStatus)

        controller = MusicController(this)
        adapter = MusicAdapter(this, mutableListOf())
        listView.adapter = adapter
        listView.emptyView = findViewById(R.id.tvListaVazia)

        fun darkAdapter(arrayRes: Int) = ArrayAdapter(
            this, R.layout.spinner_item_dark,
            resources.getStringArray(arrayRes)
        ).also { it.setDropDownViewResource(R.layout.spinner_item_dark) }

        spinnerCampoBusca.adapter   = darkAdapter(R.array.opcoes_busca)
        spinnerOrdenacao.adapter    = darkAdapter(R.array.opcoes_ordenacao)
        spinnerFiltroStatus.adapter = darkAdapter(R.array.opcoes_filtro_status)

        spinnerOrdenacao.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: android.view.View?, pos: Int, id: Long) = refreshList()
            override fun onNothingSelected(parent: AdapterView<*>) = Unit
        }

        spinnerCampoBusca.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: android.view.View?, pos: Int, id: Long) {
                etBusca.text?.clear()
                refreshList()
            }
            override fun onNothingSelected(parent: AdapterView<*>) = Unit
        }

        etBusca.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit
            override fun afterTextChanged(s: Editable?) = refreshList()
        })

        checkFavoritos.setOnCheckedChangeListener { _, _ -> refreshList() }

        spinnerFiltroStatus.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: android.view.View?, pos: Int, id: Long) = refreshList()
            override fun onNothingSelected(parent: AdapterView<*>) = Unit
        }

        findViewById<com.google.android.material.floatingactionbutton.FloatingActionButton>(R.id.fabAdicionar)
            .setOnClickListener {
                startActivity(Intent(this, MusicDetailActivity::class.java))
            }

        listView.setOnItemClickListener { _, _, position, _ ->
            val music = adapter.getItem(position) as Music
            val intent = Intent(this, MusicDetailActivity::class.java)
            intent.putExtra("MUSIC_ID", music.id)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        refreshList()
    }

    private fun refreshList() {
        val query = etBusca.text?.toString().orEmpty().trim()
        val field = columnMap[spinnerCampoBusca.selectedItemPosition]
        val sort  = columnMap[spinnerOrdenacao.selectedItemPosition]
        val onlyFavorites = checkFavoritos.isChecked
        val statusFilter = spinnerFiltroStatus.selectedItemPosition

        val list = if (query.isEmpty()) controller.getAll(sort) else controller.search(field, query)
        val filtered = list
            .let { if (onlyFavorites) it.filter { m -> m.isFavorite } else it }
            .let { when (statusFilter) {
                1 -> it.filter { m -> m.status == MusicController.STATUS_TO_LISTEN }
                2 -> it.filter { m -> m.status == MusicController.STATUS_LISTENED }
                else -> it
            }}

        adapter.updateList(filtered)
    }
}

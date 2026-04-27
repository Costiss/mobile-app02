package br.ufpr.musicbookmark

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CheckBox
import android.widget.RatingBar
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import br.ufpr.musicbookmark.controller.MusicController
import br.ufpr.musicbookmark.controller.ValidationResult
import br.ufpr.musicbookmark.model.Music
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class MusicDetailActivity : AppCompatActivity() {

    private val musicId: Long by lazy { intent.getLongExtra("MUSIC_ID", -1L) }
    private val isEditMode: Boolean get() = musicId != -1L

    private lateinit var controller: MusicController
    private lateinit var toolbar: MaterialToolbar
    private lateinit var tilTitulo: TextInputLayout
    private lateinit var etTitulo: TextInputEditText
    private lateinit var tilArtista: TextInputLayout
    private lateinit var actvArtista: MaterialAutoCompleteTextView
    private lateinit var tilAno: TextInputLayout
    private lateinit var etAno: TextInputEditText
    private lateinit var tilGenero: TextInputLayout
    private lateinit var etGenero: TextInputEditText
    private lateinit var spinnerStatus: Spinner
    private lateinit var ratingBarInput: RatingBar
    private lateinit var checkFavorito: CheckBox
    private lateinit var btnSalvar: Button
    private lateinit var btnExcluir: Button

    // Snapshot of original values for unsaved-changes detection
    private var originalTitle  = ""
    private var originalArtist = ""
    private var originalYear   = ""
    private var originalGenre  = ""
    private var originalStatus = 0
    private var originalRating = 0f
    private var originalFav    = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_music_detail)

        toolbar       = findViewById(R.id.toolbar)
        tilTitulo     = findViewById(R.id.tilTitulo)
        etTitulo      = findViewById(R.id.etTitulo)
        tilArtista    = findViewById(R.id.tilArtista)
        actvArtista   = findViewById(R.id.actvArtista)
        tilAno        = findViewById(R.id.tilAno)
        etAno         = findViewById(R.id.etAno)
        tilGenero     = findViewById(R.id.tilGenero)
        etGenero      = findViewById(R.id.etGenero)
        spinnerStatus = findViewById(R.id.spinnerStatus)
        ratingBarInput = findViewById(R.id.ratingBarInput)
        checkFavorito = findViewById(R.id.checkFavorito)
        btnSalvar     = findViewById(R.id.btnSalvar)
        btnExcluir    = findViewById(R.id.btnExcluir)

        controller = MusicController(this)

        toolbar.title = getString(if (isEditMode) R.string.titulo_editar else R.string.titulo_adicionar)
        toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }

        val artists = controller.getDistinctArtists()
        val autoAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, artists)
        actvArtista.setAdapter(autoAdapter)

        if (isEditMode) {
            val music = controller.getById(musicId)
            if (music != null) {
                populateFields(music)
            }
            btnExcluir.visibility = View.VISIBLE
        }

        saveOriginalValues()

        btnSalvar.setOnClickListener { trySave() }
        btnExcluir.setOnClickListener { confirmDelete() }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (hasUnsavedChanges()) {
                    AlertDialog.Builder(this@MusicDetailActivity)
                        .setTitle(R.string.dialogo_descartar_titulo)
                        .setMessage(R.string.dialogo_descartar_mensagem)
                        .setPositiveButton(R.string.btn_descartar) { _, _ -> finish() }
                        .setNegativeButton(R.string.btn_cancelar, null)
                        .show()
                } else {
                    finish()
                }
            }
        })
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }

    private fun populateFields(music: Music) {
        etTitulo.setText(music.title)
        actvArtista.setText(music.artist, false)
        etAno.setText(music.year.toString())
        etGenero.setText(music.genre)
        spinnerStatus.setSelection(if (music.status == MusicController.STATUS_LISTENED) 0 else 1)
        ratingBarInput.rating = music.rating.toFloat()
        checkFavorito.isChecked = music.isFavorite
    }

    private fun saveOriginalValues() {
        originalTitle  = etTitulo.text?.toString().orEmpty()
        originalArtist = actvArtista.text?.toString().orEmpty()
        originalYear   = etAno.text?.toString().orEmpty()
        originalGenre  = etGenero.text?.toString().orEmpty()
        originalStatus = spinnerStatus.selectedItemPosition
        originalRating = ratingBarInput.rating
        originalFav    = checkFavorito.isChecked
    }

    private fun hasUnsavedChanges(): Boolean {
        return etTitulo.text?.toString().orEmpty()  != originalTitle  ||
               actvArtista.text?.toString().orEmpty() != originalArtist ||
               etAno.text?.toString().orEmpty()      != originalYear   ||
               etGenero.text?.toString().orEmpty()   != originalGenre  ||
               spinnerStatus.selectedItemPosition    != originalStatus ||
               ratingBarInput.rating                 != originalRating ||
               checkFavorito.isChecked               != originalFav
    }

    private fun trySave() {
        val title   = etTitulo.text?.toString().orEmpty().trim()
        val artist  = actvArtista.text?.toString().orEmpty().trim()
        val yearStr = etAno.text?.toString().orEmpty().trim()
        val genre   = etGenero.text?.toString().orEmpty().trim()
        val status  = spinnerStatus.selectedItem as String
        val rating  = ratingBarInput.rating.toInt()
        val isFavorite = checkFavorito.isChecked

        val year = yearStr.toIntOrNull() ?: -1

        val music = Music(
            id         = if (isEditMode) musicId else 0L,
            title      = title,
            artist     = artist,
            year       = year,
            genre      = genre,
            isFavorite = isFavorite,
            status     = status,
            rating     = rating
        )

        tilTitulo.error  = null
        tilArtista.error = null
        tilAno.error     = null
        tilGenero.error  = null

        when (val result = controller.validate(music)) {
            is ValidationResult.Valid -> {
                if (isEditMode) controller.update(music) else controller.insert(music)
                finish()
            }
            is ValidationResult.Invalid -> {
                result.errors.forEach { (key, message) ->
                    when (key) {
                        "title"  -> tilTitulo.error  = message
                        "artist" -> tilArtista.error = message
                        "year"   -> tilAno.error     = message
                        "genre"  -> tilGenero.error  = message
                        "rating" -> Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                        "status" -> Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun confirmDelete() {
        AlertDialog.Builder(this)
            .setTitle(R.string.dialogo_excluir_titulo)
            .setMessage(R.string.dialogo_excluir_mensagem)
            .setPositiveButton(R.string.btn_confirmar) { _, _ ->
                controller.delete(musicId)
                finish()
            }
            .setNegativeButton(R.string.btn_cancelar, null)
            .show()
    }
}

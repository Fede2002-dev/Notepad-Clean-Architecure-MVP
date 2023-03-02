package com.devbin.notepad_clean_architecure_mvp.ui.list

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.devbin.domain.data.Note
import com.devbin.domain.repository.NoteRepository
import com.devbin.domain.usecase.GetNotesUseCase
import com.devbin.notepad_clean_architecure_mvp.databinding.ActivityNoteListBinding
import com.devbin.notepad_clean_architecure_mvp.storage.RoomNoteDataSource
import com.devbin.notepad_clean_architecure_mvp.ui.note.NoteActivity

class NoteListActivity : AppCompatActivity(), NoteListViewContract {

    private val binding: ActivityNoteListBinding by lazy {
        ActivityNoteListBinding.inflate(layoutInflater)
    }
    private val presenter: NoteListPresenter by lazy {
        val repository = NoteRepository(RoomNoteDataSource(applicationContext))
        NoteListPresenter(this, GetNotesUseCase(repository))
    }
    private val adapter: NotesRecyclerViewAdapter by lazy {
        NotesRecyclerViewAdapter(onNoteClicked = { id ->
            val editNoteIntent = Intent(this, NoteActivity::class.java).apply {
                putExtra(NoteActivity.MODE_KEY, NoteActivity.EDIT_MODE)
                putExtra(NoteActivity.NOTE_ID_KEY, id)
            }
            startActivityForResult(editNoteIntent, REQUEST_CODE)
        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setupViews()
        presenter.getAllNotes()
    }

    override fun updateNotesList(notes: List<Note>) {
        adapter.noteList = notes
        adapter.notifyDataSetChanged()
    }

    override fun onFailure(cause: String) {
        Toast.makeText(applicationContext, cause, Toast.LENGTH_SHORT).show()
    }

    private fun setupViews() {
        binding.floatingActionButton.setOnClickListener {
            val noteActivityIntent = Intent(this, NoteActivity::class.java).apply {
                putExtra(NoteActivity.MODE_KEY, NoteActivity.CREATE_MODE)
            }
            startActivityForResult(noteActivityIntent, REQUEST_CODE)
        }
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        binding.notesList.adapter = adapter
        val landscape = resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
        val rows = if (landscape) LANDSCAPE_ROWS else PORTRAIT_ROWS
        binding.notesList.layoutManager = StaggeredGridLayoutManager(rows, StaggeredGridLayoutManager.VERTICAL)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE && resultCode == RESULT_NOTE_CREATED) {
            presenter.getAllNotes()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        presenter.onDestroy()
    }

    companion object {
        private const val PORTRAIT_ROWS = 3
        private const val LANDSCAPE_ROWS = 5
        const val RESULT_NOTE_CREATED = 95
        private const val REQUEST_CODE = 96
    }
}

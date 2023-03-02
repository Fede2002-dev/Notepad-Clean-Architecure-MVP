package com.devbin.notepad_clean_architecure_mvp.ui.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.devbin.domain.data.Note
import com.devbin.notepad_clean_architecure_mvp.databinding.ItemNoteBinding

class NotesRecyclerViewAdapter(
    var noteList: List<Note> = emptyList(),
    var onNoteClicked: (id: Int) -> Unit
): RecyclerView.Adapter<NotesRecyclerViewAdapter.NoteItemViewHolder>() {

    class NoteItemViewHolder(val binding: ItemNoteBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteItemViewHolder = NoteItemViewHolder(
        ItemNoteBinding.inflate(LayoutInflater.from(parent.context), parent, false)
    )

    override fun onBindViewHolder(holder: NoteItemViewHolder, position: Int) {
        with(noteList[position]) {
            holder.binding.title.text = title
            holder.binding.content.text = content
            holder.binding.date.text = date
            holder.binding.card.setOnClickListener {
                onNoteClicked(id)
            }
        }
    }

    override fun getItemCount(): Int = noteList.size
}

package com.example.spoteam_android


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.spoteam_android.databinding.FragmentSearchBinding
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipDrawable

class SearchFragment : Fragment() {

    lateinit var binding: FragmentSearchBinding
    private val recentSearches = mutableListOf<String>()
    private lateinit var boardAdapter: BoardAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val itemList = ArrayList<BoardItem>()
        itemList.add(BoardItem(1,"피아노 스터디", "스터디 목표", "피아노 스터디입니다", 1, 5, 100,10,"ALL",listOf("어학"),listOf("1111053000"),"String", liked = false))
        itemList.add(BoardItem(1,"태권도 스터디", "스터디 목표", "태권도 스터디입니다", 2, 1, 100,10,"ALL",listOf("어학"),listOf("1111053000"),"String", liked = false))
        itemList.add(BoardItem(1,"보컬 스터디", "스터디 목표", "보컬 스터디입니다", 3, 1, 100,10,"ALL",listOf("어학"),listOf("1111053000"),"String", liked = false))

        boardAdapter = BoardAdapter(itemList,
            onItemClick = { selectedItem ->
                // 임시로 로그 출력
                Log.d("SearchFragment", "Item clicked: ${selectedItem.title}")
            },
            onLikeClick = { selectedItem, likeButton ->
                // 임시로 로그 출력
                Log.d("SearchFragment", "Like button clicked for: ${selectedItem.title}")
                // 좋아요 버튼 클릭 시 임시로 아이콘을 변경
                val newIcon = if (selectedItem.liked) R.drawable.study_like else R.drawable.ic_heart_filled
                likeButton.setImageResource(newIcon)
                // 좋아요 상태 변경
                selectedItem.liked = !selectedItem.liked
            }
        )

        binding.rvBoard2.apply {
            adapter = boardAdapter
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        }

        binding.searchView.setOnQueryTextListener(object :
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    addSearchChip(it)
                    binding.searchView.setQuery("", false)
                    binding.searchView.clearFocus()
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // Do nothing for now
                return true
            }
        })
    }


    private fun addSearchChip(query: String) {
        if (query in recentSearches) return

        recentSearches.add(query)
        binding.txRecentlySearchedWord.visibility = View.VISIBLE

        val chip = Chip(requireContext()).apply {
            text = query
            isCloseIconVisible = true
            setOnCloseIconClickListener {
                binding.chipGroup.removeView(this)
                recentSearches.remove(query)
//                if (recentSearches.isEmpty()) {
//                    binding.txRecentlySearchedWord.visibility = View.GONE
//                }
            }
            setChipDrawable(
                ChipDrawable.createFromAttributes(
                    requireContext(), null, 0, R.style.find_ChipStyle
                )
            )
        }

        binding.chipGroup.addView(chip)

    }
}

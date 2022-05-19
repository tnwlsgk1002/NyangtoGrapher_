package kr.co.nyangtographer.android.nyangtographer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kr.co.nyangtographer.android.nyangtographer.databinding.FragmentStageBinding

@AndroidEntryPoint
class SelectStageFragment : Fragment() {
    private val mainViewModel : MainViewModel by activityViewModels()

    private var clickableMaxStage : Int = 1
    var binding: FragmentStageBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (activity as MainActivity)
    }

    companion object {
        fun newInstance() = SelectStageFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentStageBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.flStage1?.setOnClickListener {
            clickStageButton(it)
        }
        binding?.flStage2?.setOnClickListener {
            clickStageButton(it)
        }
        binding?.flStage3?.setOnClickListener {
            clickStageButton(it)
        }
        binding?.flStage4?.setOnClickListener {
            clickStageButton(it)
        }
        binding?.flStage5?.setOnClickListener {
            clickStageButton(it)
        }
        binding?.flStage6?.setOnClickListener {
            clickStageButton(it)
        }

        mainViewModel.allRecord.observe(
            viewLifecycleOwner, Observer {
                initOpenStage()
            }
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    fun initOpenStage() {
        CoroutineScope(Dispatchers.IO).launch {
            if (mainViewModel.getNPassStageRecordList(2).value.isNullOrEmpty())
                binding?.ivStage2?.setVisibility(View.VISIBLE)
            else {
                clickableMaxStage = 1
                return@launch
            }
            if (mainViewModel.getNPassStageRecordList(3).value.isNullOrEmpty())
                binding?.ivStage3?.setVisibility(View.VISIBLE)
            else {
                clickableMaxStage = 2
                return@launch
            }
            if (mainViewModel.getNPassStageRecordList(4).value.isNullOrEmpty())
                binding?.ivStage4?.setVisibility(View.VISIBLE)
            else {
                clickableMaxStage = 3
                return@launch
            }
            if (mainViewModel.getNPassStageRecordList(5).value.isNullOrEmpty())
                binding?.ivStage4?.setVisibility(View.VISIBLE)
            else {
                clickableMaxStage = 4
                return@launch
            }
            if (mainViewModel.getNPassStageRecordList(6).value.isNullOrEmpty())
                binding?.ivStage6?.setVisibility(View.VISIBLE)
            else {
                clickableMaxStage = 5
                return@launch
            }
            clickableMaxStage = 6
        }
    }

    fun clickStageButton(view: View) {
        val clickStageNumber = if (view == binding?.flStage1) 1
        else if (view == binding?.flStage2) 2
        else if (view == binding?.flStage3) 3
        else if (view == binding?.flStage4) 4
        else if (view == binding?.flStage5) 5
        else 6
        if (clickStageNumber <= clickableMaxStage) {
            mainViewModel.setClickStageRecordList(clickStageNumber)
            // mainViewModel.clickStageNumber = clickStageNumber
            (activity as MainActivity).replaceSelectStageRecordFragment()
        }
    }
}
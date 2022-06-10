package kr.co.nyangtographer.android.nyangtographer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kr.co.nyangtographer.android.nyangtographer.databinding.FragmentSelectStageRecordBinding
import kr.co.nyangtographer.android.nyangtographer.databinding.FragmentStageBinding

// TODO : PhotoAdpater 수정
@AndroidEntryPoint
class SelectStageRecordFragment : Fragment() {
    private val mainViewModel : MainViewModel by activityViewModels()

    var binding : FragmentSelectStageRecordBinding? = null
    private var mRecordRecycleView : RecyclerView? = null
    private var mRecordAdapter : StageRecordAdapter?  = null
    private var mRecordDataSet = emptyList<Record>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_select_stage_record, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mRecordAdapter = StageRecordAdapter(requireContext(), mainViewModel)

        binding!!.rvPhoto.apply {
            adapter = mRecordAdapter
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
            scrollToPosition(0)
        }

        mRecordAdapter?.setItemClickListener(object: StageRecordAdapter.OnItemClickListener{
            override fun onClick(v: View, position: Int) {
                mainViewModel.setClickRecord(mRecordDataSet.get(position).id)
                (activity as MainActivity).replaceChallengeFragment()
            }
        })

        //initRecyclerView()
        mainViewModel.clickStageRecordList?.observe(
            viewLifecycleOwner, Observer {
                initRecyclerView()
            }
        )
    }

    fun initRecyclerView() {
        mRecordDataSet = mainViewModel.clickStageRecordList!!.value!!
        mRecordAdapter!!.setDataSet(mRecordDataSet)
    }
}
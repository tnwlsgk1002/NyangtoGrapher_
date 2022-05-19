package kr.co.nyangtographer.android.nyangtographer

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kr.co.nyangtographer.android.nyangtographer.databinding.FragmentChallengeBinding
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import java.io.File

const val PASS_SCORE = 80

class ChallengeFragment : Fragment() {
    var binding: FragmentChallengeBinding? = null
    private val mainViewModel: MainViewModel by activityViewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        (activity as MainActivity).showGetImageDialog()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChallengeBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding?.btnSelectCamera?.setOnClickListener {
            clickReSelectButton()
        }
        binding?.btnSelectChallenge?.setOnClickListener {
            cilckChallengeButton()
        }
        mainViewModel.clickRecord?.observe(
            viewLifecycleOwner, Observer {
                changeImageView()
            }
        )
    }

    // Fragment에서 두 이미지 변경
    fun changeImageView() {
        val record = mainViewModel.clickRecord?.value
        val questImg = record?.quest_img
        val challengeImg = record?.challenge_img
        binding?.ivQuestImg?.setImageBitmap(questImg)
        binding?.ivChallengeImg?.setImageBitmap(challengeImg)
    }

    // 도전을 눌렀을 때
    private fun cilckChallengeButton() {
        val record = mainViewModel.clickRecord?.value
        val challengeImg = record?.challenge_img
        val answerId = record?.answer_id
        val imagePath = mainViewModel.imagePath

        if (answerId != null && challengeImg != null) {
            (activity as MainActivity).showProgressDialog()
            lifecycleScope.launch(Dispatchers.IO) {
                val serverRequest = ServerRequest()
                serverRequest.prediction(answerId, imagePath)
                var similarity: Int = serverRequest.similarity
                var pass: Boolean = if (similarity >= PASS_SCORE) true else false
                if (similarity == -1) {
                    Toast.makeText(activity, "알 수 없는 에러가 발생하였습니다.", Toast.LENGTH_LONG).show()
                }
                else {
                    updateRecord(similarity, pass)

                    (activity as MainActivity).runOnUiThread {
                        (activity as MainActivity).cancelProgressDialog()
                        (activity as MainActivity).showGameResultDialog(similarity, pass, challengeImg)
                    }
                }
            }
        } else {
            Toast.makeText(activity, "알 수 없는 에러가 발생하였습니다.", Toast.LENGTH_LONG).show()
        }
    }

    private suspend fun updateRecord(similarity: Int, pass: Boolean) {
        val oriRecord = mainViewModel.clickRecord?.value?.let {
            val id = it.id
            val answerId = it.answer_id
            val stageNumber = it.stage_number
            val title = it.title
            val questImg = it.quest_img
            val challengeImg = it.challenge_img

            val newRecord = Record(
                id,
                answerId,
                stageNumber,
                title,
                challengeImg,
                questImg,
                similarity,
                pass
            )
            mainViewModel.update(newRecord)
        }
    }

    // 다시 선택 눌렀을 때 -> 카메라 혹은 갤러리 선택
    private fun clickReSelectButton() {
        (activity as MainActivity).showGetImageDialog()
    }
}
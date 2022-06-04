package kr.co.nyangtographer.android.nyangtographer

import android.Manifest
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.AppCompatButton
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kr.co.nyangtographer.android.nyangtographer.databinding.ActivityMainBinding
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

//enum class CatTalk(
//    val talk : String
//) {
//    SelectStage("집사, 스테이지를 선택해라냥."),
//    SelectRecord("도전하고 사진을 선택해라냥."),
//    ClickChallenge("")
//}
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    var currentFragmentNumber: Int? = 1 // 현재 프레그먼트
    val REQUEST_TAKE_PHOTO = 1
    lateinit var mCameraPhotoPath: String

    private lateinit var mainViewModel: MainViewModel

    var customProgressDialog : Dialog? = null

    private val cameraResultLauncher: ActivityResultLauncher<String> =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            if (isGranted) {
                openCameraLauncher()
            } else {
                Toast.makeText(this, "카메라 권한 승인 요청이 무시되었습니다.", Toast.LENGTH_LONG).show()
            }
        }

    private val galleryResultLauncher: ActivityResultLauncher<Array<String>> =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            permissions.entries.forEach {
                val permissionName = it.key
                val isGranted = it.value
                if (isGranted) { // 승인했을 경우
                    val pickIntent =
                        Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    openGalleryLauncher.launch(pickIntent)
                } else { // 승인하지 않았을 경우
                    if (permissionName == Manifest.permission.READ_EXTERNAL_STORAGE) {
                        Toast.makeText(
                            this@MainActivity,
                            "Oops you just denied the permission",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }

        }

    private val openGalleryLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK && result.data != null) {
                var challengeImg: Bitmap? = null
                println("openGalleryLauncher")
                challengeImg =
                    MediaStore.Images.Media.getBitmap(this.contentResolver, result.data?.data)
                lifecycleScope.launch(Dispatchers.IO) {
                        mainViewModel.clickRecord?.value?.let{
                        val id = it.id
                        val answerId = it.answer_id
                        val stageNumber = it.stage_number
                        val title = it.title
                        val questImg = it.quest_img
                        val similarity = it.similarity
                        val pass = it.pass

                        val addRecord = Record(
                            id,
                            answerId,
                            stageNumber,
                            title,
                            challengeImg,
                            questImg,
                            similarity,
                            pass
                        )
                        mainViewModel.imagePath = absolutelyPath(result.data?.data!!)
                        mainViewModel.update(addRecord)
                    }
                }
            }
        }

    // 절대경로 변환
    fun absolutelyPath(path: Uri): String? {

        var proj: Array<String> = arrayOf(MediaStore.Images.Media.DATA)
        var c: Cursor? = contentResolver.query(path, proj, null, null, null)
        var index = c?.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        c?.moveToFirst()

        var result = index?.let { c?.getString(it) }

        return result
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val selectStageFragment = SelectStageFragment()
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        binding.activity = this

        replaceSelectStageFragment()

        binding.tvUndo.setOnClickListener {
            undoFragment()
        }

        mainViewModel =
            ViewModelProvider(
                this,
                MainViewModelFactory((application as NyangApplication).repository)
            )
                .get(MainViewModel::class.java)

    }

    override fun onBackPressed() {
        super.onBackPressed()
        if (currentFragmentNumber == 1) finish()
        else if (currentFragmentNumber == 2) {
            currentFragmentNumber = 1
            binding.tvTalk.text = "집사, 스테이지를 선택해라냥."
        } else if (currentFragmentNumber == 3) {
            currentFragmentNumber = 2
            binding.tvTalk.text = "도전하고 싶은 사진을 선택해라냥."
        }
    }

    private fun undoFragment() {
        onBackPressed()
    }

    fun replaceSelectStageFragment() {
        val transaction = supportFragmentManager.beginTransaction()
            .replace(
                R.id.fl_center,
                SelectStageFragment()
            )
            .addToBackStack(null)
            .commit()
        currentFragmentNumber = 1
        binding.tvTalk.text = "집사, 스테이지를 선택해라냥."
    }

    fun replaceSelectStageRecordFragment() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fl_center, SelectStageRecordFragment())
            .addToBackStack(null)
            .commit() // 포토 선택 화면으로 교체
        supportFragmentManager.executePendingTransactions()
        currentFragmentNumber = 2
        binding.tvTalk.text = "도전하고 싶은 사진을 선택해라냥."
        //(supportFragmentManager.findFragmentById(R.id.fl_center) as SelectStageRecordFragment).initRecyclerView()
    }

    fun replaceChallengeFragment() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fl_center, ChallengeFragment())
            .addToBackStack(null)
            .commit()
        supportFragmentManager.executePendingTransactions()
        currentFragmentNumber = 3
        binding.tvTalk.text = "선택한 사진으로 하고 싶으면\n 도전을, 아니면 다시 선택해라냥"
    }

    // 권한 확인 후 카메라 실행, 사진 받아옴
    private fun selectCamera() {
        if (shouldShowRequestPermissionRationale(android.Manifest.permission.CAMERA)) {
            showRationableDialog("냥토그래퍼", "냥토그래퍼" + "앱은 카메라 권한 요청을 필요로 합니다.")
        } else {
            cameraResultLauncher.launch(android.Manifest.permission.CAMERA)
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            mCameraPhotoPath = absolutePath
        }
    }

    private fun openCameraLauncher() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            // Ensure that there's a camera activity to handle the intent
            takePictureIntent.resolveActivity(packageManager)?.also {
                // TODO : 카메라 권한 요청 - takePictureIntent NULL 오류 확인
                // Create the File where the photo should go
                val photoFile: File? = try {
                    createImageFile()
                } catch (ex: IOException) {
                    // Error occurred while creating the File
                    Toast.makeText(
                        this,
                        "Error occurred while creating the File",
                        Toast.LENGTH_LONG
                    )
                        .show()
                    null
                }
                // Continue only if the File was successfully created
                photoFile?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(
                        this,
                        "com.example.android.fileprovider",
                        it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO)
                }
            }
        }
    }

    private fun selectGallery() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                this, Manifest.permission.READ_EXTERNAL_STORAGE
            )
        ) {
            showRationableDialog("냥토그래퍼", "냥토그래퍼" + "앱은 갤러리 권한 요청을 필요로 합니다.")
        } else {
            galleryResultLauncher.launch(
                arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            )
        }
    }

    private fun showRationableDialog(title: String, message: String) {
        val builder: androidx.appcompat.app.AlertDialog.Builder =
            androidx.appcompat.app.AlertDialog.Builder(this)
        builder.setTitle(title)
            .setMessage(message)
            .setPositiveButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
        builder.create().show()
    }

    // 스토리지에서 읽을 수 있는 권한이 있는지 확인
    private fun isReadStorageAllowed(): Boolean {
        val result = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_EXTERNAL_STORAGE // 성공시 반환 0
        ) // 권한 확인
        return result == PackageManager.PERMISSION_GRANTED // PERMISSION_GRANTED = 0
    }

    // 카메라 혹은 갤러리 선택 다이얼로그 띄우기
    fun showGetImageDialog() {
        val dialog = Dialog(this)

        dialog.setContentView(R.layout.dialog_get_image)
        // 카메라 선택 시
        dialog.findViewById<Button>(R.id.btn_select_camera).setOnClickListener {
            // TODO : 권한 확인 후 카메라에서 사진찍고 가지고 오기
            selectCamera()
            dialog.dismiss()
        }
        // 갤러리 선택 시
        dialog.findViewById<Button>(R.id.btn_select_gallery).setOnClickListener {
            selectGallery()
            dialog.dismiss()
        }
        dialog.show()
    }

    fun showGameResultDialog(score: Int, pass: Boolean, img: Bitmap) { //
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_game_result)
        dialog.findViewById<TextView>(R.id.tv_score).text = "${score}점"
        dialog.findViewById<TextView>(R.id.tv_result).text = if (pass) "미션 성공!!" else "미션 실패"
        dialog.findViewById<ImageView>(R.id.iv_user_img).setImageBitmap(img)
        dialog.findViewById<AppCompatButton>(R.id.btn_undo_select_stage_record).setOnClickListener {
            undoFragment()
            dialog.dismiss()
        }
        dialog.show()
    }

    fun cancelProgressDialog() {
        if (customProgressDialog != null) {
            customProgressDialog?.dismiss()
            customProgressDialog = null
        }
    }

    fun showProgressDialog() {
        customProgressDialog = Dialog(this@MainActivity)

        customProgressDialog?.setContentView(R.layout.dialog_custom_progress)

        customProgressDialog?.show()
    }
}
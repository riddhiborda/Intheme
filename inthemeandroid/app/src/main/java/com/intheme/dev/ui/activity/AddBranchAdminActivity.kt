package com.intheme.dev.ui.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.graphics.Matrix
import android.net.Uri
import android.os.Build.VERSION.SDK_INT
import android.os.Environment
import android.provider.MediaStore
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import com.intheme.dev.R
import com.intheme.dev.base.BaseActivity
import com.intheme.dev.data.model.EventModel
import com.intheme.dev.databinding.ActivityAddBranchAdminBinding
import com.intheme.dev.ui.dailogs.BranchSelectDialog
import com.intheme.dev.ui.dailogs.ProgressDialog
import com.intheme.dev.utils.ApiState
import com.intheme.dev.utils.ArgConst.Companion.ARG_BRANCH_ADMIN_ID
import com.intheme.dev.utils.ArgConst.Companion.ARG_BRANCH_ADMIN_IMAGE
import com.intheme.dev.utils.ArgConst.Companion.ARG_BRANCH_ID
import com.intheme.dev.utils.ArgConst.Companion.ARG_BRANCH_NAME
import com.intheme.dev.utils.ArgConst.Companion.ARG_EMAIL_ID
import com.intheme.dev.utils.ArgConst.Companion.ARG_IS_EDIT
import com.intheme.dev.utils.ArgConst.Companion.ARG_MOBILE
import com.intheme.dev.utils.ArgConst.Companion.ARG_MOBILE_NO
import com.intheme.dev.utils.ArgConst.Companion.ARG_NAME
import com.intheme.dev.utils.Constants.Companion.ARG_EMAIL
import com.intheme.dev.utils.Constants.Companion.BRANCH_SELECT_DIALOG
import com.intheme.dev.utils.EVENT_TYPE
import com.intheme.dev.utils.GlobalEventBus
import com.intheme.dev.utils.checkPermissionsForCameraAndStorage
import com.intheme.dev.utils.errorHandling
import com.intheme.dev.utils.gone
import com.intheme.dev.utils.handlePermissionDenied
import com.intheme.dev.utils.imageSelectDialog
import com.intheme.dev.utils.launchWhenStarted
import com.intheme.dev.utils.load
import com.intheme.dev.utils.reqAct
import com.intheme.dev.utils.setImageUpload
import com.intheme.dev.utils.setRequestBody
import com.intheme.dev.utils.showIfNotExists
import com.intheme.dev.utils.showSuccessToast
import com.intheme.dev.utils.startActivity
import com.intheme.dev.utils.visible
import com.intheme.dev.viewmodel.BranchAdminViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException

@AndroidEntryPoint
class AddBranchAdminActivity : BaseActivity<ActivityAddBranchAdminBinding>(ActivityAddBranchAdminBinding::inflate) {
    private var branchSelectId:String=""
    private var adminImage:String=""
    private var id:String=""
    private var branchSelectName:String=""
    enum class VALIDATION_BRANCH {
        IMAGE,NAME,EMAIL,MOBILE_NO,BRANCH,APICALL
    }

    private var isStarted: Boolean = false
    private var isEdit : Boolean = false

    private var mSelectedFileLogoImg: File? = null

    private var mUrlLogoImg: String? = ""

    private val branchAdminViewModel: BranchAdminViewModel by viewModels()

    private var image_uri: Uri? =null

    private val progressDialog: ProgressDialog by lazy {
        ProgressDialog()
    }

    private var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            onSelectFromGalleryResult(result.data?.data)
            adminImage = "image"
        }
    }

    private val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        onSelectFromGalleryResult(uri)
        adminImage = "image"
    }

    private var cameraActivityResultLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == Activity.RESULT_OK) {
            val inputImage = image_uri?.let { it1 -> uriToBitmap(it1) }
            val rotated = inputImage?.let { it1 -> rotateBitmap(it1) }
            rotated?.let { it1 -> onCaptureImageResult(it1) }
            adminImage = "image"
        }
    }

    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            if (!permissions.values.contains(false)) {
                image_uri = imageSelectDialog(cameraActivityResultLauncher,resultLauncher,pickMedia)
            }else{
                val arrayList = ArrayList<String>()
                permissions.entries.forEach { entry ->
                    if (!entry.value) {
                        arrayList.add(entry.key)
                    }
                }
                val array: Array<String> = arrayList.toArray(arrayOfNulls<String>(arrayList.size))
                handleDenied(array)
            }
        }


    override fun initView() {
        isEdit = intent.getBooleanExtra(ARG_IS_EDIT,false)
        id = intent.getStringExtra(ARG_BRANCH_ADMIN_ID)?:""
        binding.edName.setText(intent.getStringExtra(ARG_NAME)?:"")
        binding.edPhone.setText(intent.getStringExtra(ARG_MOBILE_NO)?:"")
        binding.edEmail.setText(intent.getStringExtra(ARG_EMAIL_ID)?:"")
        branchSelectId = intent.getStringExtra(ARG_BRANCH_ID)?:""
        branchSelectName = intent.getStringExtra(ARG_BRANCH_NAME)?:""
        adminImage = intent.getStringExtra(ARG_BRANCH_ADMIN_IMAGE)?:""
        binding.edBranch.setText(branchSelectName)
        binding.ivProfile.load(this@AddBranchAdminActivity,adminImage)
        if(isEdit){
            binding.lblToolbar.text = getString(R.string.update_branch_admin)
        }
    }

    override fun setupObservers() {
        launchWhenStarted {
            branchAdminViewModel.mCreateBranchAdminStateFlow.collect {
                when (it) {
                    is ApiState.Loading -> {
                        progressDialog.showProgress(this@AddBranchAdminActivity)
                        isStarted = true
                    }

                    is ApiState.Failure -> {
                        progressDialog.dismissProgress()
                        errorHandling(logout = {
                            branchAdminViewModel.setLogout()
                            startActivity<LoginActivity> {  }
                            finish()
                            finishAffinity()
                        },it.isNetworkError, it.msg)
                        isStarted = false
                    }

                    is ApiState.Success -> {
                        progressDialog.dismissProgress()
                        it.data.message.showSuccessToast(this@AddBranchAdminActivity)
                        isStarted = false
                        GlobalEventBus.event.emit(EventModel(EVENT_TYPE.ADD_BRANCH_ADMIN,""))
                        onBackPress()
                    }
                    else -> {

                    }
                }
            }
        }

        launchWhenStarted {
            branchAdminViewModel.mUpdateBranchAdminStateFlow.collect {
                when (it) {
                    is ApiState.Loading -> {
                        progressDialog.showProgress(this@AddBranchAdminActivity)
                        isStarted = true
                    }

                    is ApiState.Failure -> {
                        progressDialog.dismissProgress()
                        errorHandling(logout = {
                            branchAdminViewModel.setLogout()
                            startActivity<LoginActivity> {  }
                            finish()
                            finishAffinity()
                        },it.isNetworkError, it.msg)
                        isStarted = false
                    }

                    is ApiState.Success -> {
                        progressDialog.dismissProgress()
                        it.data.message.showSuccessToast(this@AddBranchAdminActivity)
                        isStarted = false
                        GlobalEventBus.event.emit(EventModel(EVENT_TYPE.UPDATE_BRANCH_ADMIN,""))
                        onBackPress()

                    }
                    else -> {

                    }
                }
            }
        }
    }

    override fun setOnClicks() {
       binding.edBranch.setOnClickListener {
           showBranchDialog()
       }

       binding.btnSubmit.setOnClickListener {
           if (mSelectedFileLogoImg == null && adminImage.isEmpty()) {
               showValidation(VALIDATION_BRANCH.IMAGE,"")
           } else if (binding.edName.text.toString().isEmpty()) {
               showValidation(VALIDATION_BRANCH.NAME,getString(R.string.enter_full_name))
           } else if (binding.edEmail.text.toString().isEmpty()) {
               showValidation(VALIDATION_BRANCH.EMAIL,getString(R.string.enter_email_id))
           } else if (binding.edPhone.text.toString().isEmpty()) {
               showValidation(VALIDATION_BRANCH.MOBILE_NO,getString(R.string.enter_mobile_no))
           } else if (binding.edBranch.text.toString().isEmpty()) {
               showValidation(VALIDATION_BRANCH.BRANCH,getString(R.string.select_branch))
           }else {
               showValidation(VALIDATION_BRANCH.APICALL,null)
               val name = binding.edName.text.toString().setRequestBody()
               val email = binding.edEmail.text.toString().setRequestBody()
               val mobile = binding.edPhone.text.toString().setRequestBody()
               val branch = branchSelectId.setRequestBody()
               val image = mSelectedFileLogoImg?.setImageUpload(
                   "image"
               )
               if(isEdit){
                   branchAdminViewModel.updateBranchAdmin(id = id,name = name,email=email, mobileNo = mobile, branchId = branch,profileImage = image)
               }else{
                   branchAdminViewModel.createBranchAdmin(name = name,email=email, mobileNo = mobile, branchId = branch,profileImage = image)
               }
           }
       }

       binding.ivEdit.setOnClickListener {
           checkPermissionsForCameraAndStorage(permissionLauncher) {
               image_uri = imageSelectDialog(cameraActivityResultLauncher,resultLauncher,pickMedia)
           }
       }

       binding.ivBack.setOnClickListener {
           onBackPress()
       }
    }

    override fun onBackPress() {
        if(!isStarted){
            finish()
        }
    }

    private fun handleDenied(permissions: Array<String>){
        handlePermissionDenied(permissions,permissionLauncher)
    }

    private fun showBranchDialog() {
        BranchSelectDialog().also {
            it.branchSelect = { branch,_->
                branchSelectName = branch.branchName?:""
                branchSelectId = branch.id?:""
                binding.edBranch.setText(branch.branchName?:"")
            }
            it.showIfNotExists(supportFragmentManager, BRANCH_SELECT_DIALOG)
        }
    }

    private fun showValidation(validateType:VALIDATION_BRANCH?, msg: String?) {
        binding.tvValidationIvCustomer.gone()
        binding.tilName.isErrorEnabled = false
        binding.tilName.errorIconDrawable = null
        binding.tilEmail.isErrorEnabled = false
        binding.tilEmail.errorIconDrawable = null
        binding.tilPhone.isErrorEnabled = false
        binding.tilPhone.errorIconDrawable = null
        binding.tilBranch.isErrorEnabled = false
        binding.tilBranch.errorIconDrawable = null
        when (validateType) {
            VALIDATION_BRANCH.IMAGE -> {
                binding.tvValidationIvCustomer.visible()
            }
            VALIDATION_BRANCH.NAME -> {
                binding.tilName.isErrorEnabled = true
                binding.tilName.error = msg
            }
            VALIDATION_BRANCH.EMAIL -> {
                binding.tilEmail.isErrorEnabled = true
                binding.tilEmail.error = msg
            }
            VALIDATION_BRANCH.MOBILE_NO -> {
                binding.tilPhone.isErrorEnabled = true
                binding.tilPhone.error = msg
            }
            VALIDATION_BRANCH.BRANCH -> {
                binding.tilBranch.isErrorEnabled = true
                binding.tilBranch.error = msg
            }
            else->{

            }
        }
    }

    private fun onSelectFromGalleryResult(data: Uri?) {
        var bm: Bitmap? = null
        if (data != null) {
            try {
                bm = when {
                    SDK_INT < 28 -> MediaStore.Images.Media.getBitmap(
                        contentResolver,
                        data
                    )
                    else -> {
                        val source = ImageDecoder.createSource(contentResolver, data)
                        ImageDecoder.decodeBitmap(source)
                    }
                }
                val bytes = ByteArrayOutputStream()
                bm?.compress(Bitmap.CompressFormat.JPEG, 90, bytes)

                mSelectedFileLogoImg = File(
                    Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_PICTURES
                    ), System.currentTimeMillis().toString() + ".jpg"
                )
                val fo: FileOutputStream
                try {
                    mSelectedFileLogoImg?.createNewFile()
                    fo = FileOutputStream(mSelectedFileLogoImg!!)
                    fo.write(bytes.toByteArray())
                    fo.close()
                    mUrlLogoImg = "file://${mSelectedFileLogoImg?.path}"
                    binding.ivProfile.load(this@AddBranchAdminActivity, mUrlLogoImg ?: "")
                } catch (e: FileNotFoundException) {
                    e.printStackTrace()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun onCaptureImageResult(data: Bitmap) {
        val bytes = ByteArrayOutputStream()
        data.compress(Bitmap.CompressFormat.JPEG, 90, bytes)
        mSelectedFileLogoImg = File(
            Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES
            ), System.currentTimeMillis().toString() + ".jpg"
        )
        val fo: FileOutputStream
        try {
            mSelectedFileLogoImg?.createNewFile()
            fo = FileOutputStream(mSelectedFileLogoImg!!)
            fo.write(bytes.toByteArray())
            fo.close()
            mUrlLogoImg = "file://${mSelectedFileLogoImg!!.path}"
            binding.ivProfile.load(this@AddBranchAdminActivity, mUrlLogoImg ?: "")
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun uriToBitmap(selectedFileUri: Uri): Bitmap? {
        try {
            val parcelFileDescriptor = contentResolver.openFileDescriptor(selectedFileUri, "r")
            val fileDescriptor = parcelFileDescriptor!!.fileDescriptor
            val image = BitmapFactory.decodeFileDescriptor(fileDescriptor)
            parcelFileDescriptor.close()
            return image
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }

    @SuppressLint("Range")
    fun rotateBitmap(input: Bitmap): Bitmap {
        val orientationColumn =
            arrayOf(MediaStore.Images.Media.ORIENTATION)
        val cur = contentResolver.query(image_uri!!, orientationColumn, null, null, null)
        var orientation = -1
        if (cur != null && cur.moveToFirst()) {
            orientation = cur.getInt(cur.getColumnIndex(orientationColumn[0]))
        }

        val rotationMatrix = Matrix()
        rotationMatrix.setRotate(orientation.toFloat())
        return Bitmap.createBitmap(input, 0, 0, input.width, input.height, rotationMatrix, true)
    }

}
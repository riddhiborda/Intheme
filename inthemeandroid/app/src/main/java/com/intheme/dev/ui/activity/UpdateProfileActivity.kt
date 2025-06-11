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
import com.intheme.dev.databinding.ActivityUpdateProfileBinding
import com.intheme.dev.ui.dailogs.ConformationDialog
import com.intheme.dev.utils.ApiState
import com.intheme.dev.utils.ConformationDlgClick
import com.intheme.dev.utils.Constants.Companion.CONFORMATION_DIALOG
import com.intheme.dev.utils.checkPermissionsForCameraAndStorage
import com.intheme.dev.utils.disable
import com.intheme.dev.utils.enable
import com.intheme.dev.utils.errorHandling
import com.intheme.dev.utils.getStr
import com.intheme.dev.utils.gone
import com.intheme.dev.utils.handlePermissionDenied
import com.intheme.dev.utils.imageSelectDialog
import com.intheme.dev.utils.launchWhenStarted
import com.intheme.dev.utils.load
import com.intheme.dev.utils.setImageUpload
import com.intheme.dev.utils.setRequestBody
import com.intheme.dev.utils.startActivity
import com.intheme.dev.utils.text
import com.intheme.dev.utils.visible
import com.intheme.dev.viewmodel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException

@AndroidEntryPoint
class UpdateProfileActivity : BaseActivity<ActivityUpdateProfileBinding>(ActivityUpdateProfileBinding::inflate) {
    private var mSelectedFileLogoImg: File? = null
    private var mUrlLogoImg: String? = ""
    private var isStarted: Boolean = false
    private enum class VALIDATION_EDIT_PROFILE {
        NAME,MOBILE_NO,EMAIL_ID,APICALL
    }

    private var image_uri: Uri? =null

    private val userViewModel: UserViewModel by viewModels()

    private var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            onSelectFromGalleryResult(result.data?.data)
        }
    }

    private val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        onSelectFromGalleryResult(uri)
    }

    private var cameraActivityResultLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == Activity.RESULT_OK) {
            val inputImage = image_uri?.let { it1 -> uriToBitmap(it1) }
            val rotated = inputImage?.let { it1 -> rotateBitmap(it1) }
            rotated?.let { it1 -> onCaptureImageResult(it1) }
        }
    }

    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            if (!permissions.values.contains(false)) {
                image_uri =  imageSelectDialog(cameraActivityResultLauncher,resultLauncher,pickMedia)
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
        binding.ivProfile.load(this@UpdateProfileActivity,userViewModel.getUserDetail().image?:"")
        binding.edName.text(userViewModel.getUserDetail().name?:"")
        binding.edMobileNo.text(userViewModel.getUserDetail().mobileNo?:"")
        binding.edEmail.text(userViewModel.getUserDetail().email?:"")
    }

    override fun setupObservers() {
        launchWhenStarted {
            userViewModel.mUpdateProfileStateFlow.collect {
                when (it) {
                    is ApiState.Loading -> {
                        showProgressApiCall(isStart = true)
                    }
                    is ApiState.Failure -> {
                        showProgressApiCall()
                        errorHandling(logout = {
                            userViewModel.logoutUser()
                            startActivity<LoginActivity> {  }
                            finish()
                            finishAffinity()
                        },it.isNetworkError, it.msg)
                    }
                    is ApiState.Success -> {
                        showProgressApiCall()
                        userViewModel.setUserDetail(it.data.data)
                        onBackPress()
                    }
                    else -> {
                    }
                }
            }
        }

        launchWhenStarted {
            userViewModel.mLogoutStateFlow.collect {
                when (it) {
                    is ApiState.Loading -> {
                        showProgressLogoutApiCall(isStart = true)
                    }
                    is ApiState.Failure -> {
                        showProgressLogoutApiCall()
                        errorHandling(logout = {
                            userViewModel.logoutUser()
                            startActivity<LoginActivity> {  }
                            finish()
                            finishAffinity()
                        },it.isNetworkError, it.msg)
                    }
                    is ApiState.Success -> {
                        showProgressLogoutApiCall()
                        userViewModel.logoutUser()
                        startActivity<LoginActivity>()
                        finish()
                        finishAffinity()
                    }
                    else -> {
                    }
                }
            }

        }
    }



    override fun setOnClicks() {
        binding.ivEdit.setOnClickListener {
            checkPermissionsForCameraAndStorage(permissionLauncher) {
                image_uri = imageSelectDialog(cameraActivityResultLauncher,resultLauncher,pickMedia)
            }
        }

        binding.ivBack.setOnClickListener {
            onBackPress()
        }

        binding.btnLogout.setOnClickListener {
            userViewModel.getUserDetail().id?.let {
                mExitDialog(it)
            }

        }

        binding.btnUpdate.setOnClickListener {
            if (binding.edName.text.toString().isEmpty()) {
                showValidation(VALIDATION_EDIT_PROFILE.NAME,getString(R.string.enter_full_name))
            } else if (binding.edEmail.text.toString().isEmpty()) {
                showValidation(VALIDATION_EDIT_PROFILE.EMAIL_ID,getString(R.string.enter_email_id))
            } else if (binding.edMobileNo.text.toString().isEmpty()) {
                showValidation(VALIDATION_EDIT_PROFILE.MOBILE_NO,getString(R.string.enter_mobile_no))
            } else {
                showValidation(VALIDATION_EDIT_PROFILE.APICALL,null)
                val name = binding.edName.text.toString().setRequestBody()
                val email = binding.edEmail.text.toString().setRequestBody()
                val mobile = binding.edMobileNo.text.toString().setRequestBody()
                val image = mSelectedFileLogoImg?.setImageUpload(
                    "image"
                )
                userViewModel.putEditProfile(id = userViewModel.getUserDetail().id?:"",name = name,email=email, mobileNo = mobile, profileImage =image)
            }
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

    private fun mExitDialog(id:String) {
        val conformationDialog = ConformationDialog.newInstance(
            title = getString(R.string.app_name),
            message = getString(R.string.sure_logout)
        )
        conformationDialog.onClick = { it ->
            when (it) {
                ConformationDlgClick.YES -> {
                    userViewModel.postLogout(id)
                }
                ConformationDlgClick.NO -> {

                }
            }
        }
        conformationDialog.show(supportFragmentManager, CONFORMATION_DIALOG)
    }


    private fun showProgressApiCall(isStart:Boolean=false){
        isStarted = isStart
        if(isStart){
            binding.pbUpdateProfile.visible()
            binding.btnUpdate.text("")
            binding.btnUpdate.disable()
            binding.btnLogout.disable()
            binding.ivEdit.disable()
        }else{
            binding.pbUpdateProfile.gone()
            binding.btnUpdate.text(getStr(R.string.btn_update))
            binding.btnUpdate.enable()
            binding.btnLogout.enable()
            binding.ivEdit.enable()
        }
    }

    private fun showProgressLogoutApiCall(isStart:Boolean=false){
        isStarted = isStart
        if(isStart){
            binding.pbLogoutProfile.visible()
            binding.btnLogout.text("")
            binding.btnUpdate.disable()
            binding.btnLogout.disable()
            binding.ivEdit.disable()
        }else{
            binding.pbLogoutProfile.gone()
            binding.btnLogout.text(getStr(R.string.btn_logout))
            binding.btnUpdate.enable()
            binding.btnLogout.enable()
            binding.ivEdit.enable()
        }
    }


    private fun showValidation(validateType: VALIDATION_EDIT_PROFILE?, msg: String?) {
        binding.tilName.isErrorEnabled = false
        binding.tilName.errorIconDrawable = null

        binding.tilEmail.isErrorEnabled = false
        binding.tilEmail.errorIconDrawable = null

        binding.tilMobileNo.isErrorEnabled = false
        binding.tilMobileNo.errorIconDrawable = null

        when (validateType) {
            VALIDATION_EDIT_PROFILE.NAME -> {
                binding.tilName.isErrorEnabled = true
                binding.tilName.error = msg
            }
            VALIDATION_EDIT_PROFILE.EMAIL_ID -> {
                binding.tilEmail.isErrorEnabled = true
                binding.tilEmail.error = msg
            }
            VALIDATION_EDIT_PROFILE.MOBILE_NO -> {
                binding.tilMobileNo.isErrorEnabled = true
                binding.tilMobileNo.error = msg
            }
            else ->{

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
                    binding.ivProfile.load(this@UpdateProfileActivity, mUrlLogoImg ?: "")
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
            binding.ivProfile.load(this@UpdateProfileActivity, mUrlLogoImg ?: "")
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
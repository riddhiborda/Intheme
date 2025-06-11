package com.intheme.dev.utils

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.os.Build
import android.os.Build.VERSION.SDK_INT
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.util.Patterns
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import androidx.annotation.DrawableRes
import androidx.annotation.LayoutRes
import androidx.annotation.RequiresApi
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.viewbinding.ViewBinding
import androidx.viewpager2.widget.ViewPager2
import com.intheme.dev.R
import com.intheme.dev.data.apiservice.ApiConstant
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.textview.MaterialTextView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.intheme.dev.data.preference.PrefConstant.Companion.PREF_NAME
import com.intheme.dev.data.preference.SharePreferenceManage
import com.intheme.dev.databinding.DlgExternalstorageBinding
import com.intheme.dev.ui.activity.LoginActivity
import com.intheme.dev.ui.dailogs.showAlertDialog
import com.intheme.dev.ui.dailogs.showSettingsDialog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import retrofit2.HttpException
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**REQUIRED ACTIVITY*/
fun Fragment.reqAct() = requireActivity() as AppCompatActivity

fun ViewPager2.disableSwipe() {
    val view = this.getChildAt(0)
    view.setOnTouchListener { _, _ -> true }
}

fun ViewPager2.enableSwipe() {
    val view = this.getChildAt(0)
    view.setOnTouchListener(null)
}


fun View.visible() {
    visibility = View.VISIBLE
}

fun View.gone() {
    visibility = View.GONE
}

fun View.invisible() {
    visibility = View.INVISIBLE
}

fun View.isVisible(): Boolean {
    return this.visibility == View.VISIBLE
}

fun TextView.setTextColorRes(colorResId: Int) {
    this.setTextColor(ContextCompat.getColor(context, colorResId))
}

fun Context.getColorRes(@ColorRes colorResId: Int): Int {
    return ContextCompat.getColor(this, colorResId)
}



//------------------------------------------------------------------
//              START NEW ACTIVITY INTENT & USES
//------------------------------------------------------------------

/*** Intent */
inline fun <reified T : Activity> Context.startActivity(crossinline block: Intent.() -> Unit = {}) {
    startActivity(Intent(this, T::class.java).apply(block))
}

//Get intent
inline fun <reified T> Intent.getExtra(key: String, defaultValue: T?): T? {
    return when (T::class) {
        Int::class -> extras?.getInt(key, defaultValue as Int? ?: 0) as T
        Float::class -> extras?.getFloat(key, defaultValue as? Float ?: 0f) as T
        String::class -> extras?.getString(key, defaultValue as String) as T
        Boolean::class -> extras?.getBoolean(key, defaultValue as? Boolean ?: false) as T
        Double::class -> extras?.getDouble(key, defaultValue as? Double ?: 0.0) as T
        else -> defaultValue
    }
}

/*--------------------HOW TO USED GET INTENT?--------------------------------------*/
/** val intent = intent // Get your intent

val intValue = intent.getExtra("myIntExtra", defaultValue = 0)
val floatValue = intent.getExtra("myFloatExtra", defaultValue = 0.0f)
val stringValue = intent.getExtra("myStringExtra", defaultValue = "defaultString")
val booleanValue = intent.getExtra("myBooleanExtra", defaultValue = true)
val doubleValue = intent.getExtra("myDoubleExtra", defaultValue = 0.0)
val serializableValue = intent.getExtra<MySerializable>("mySerializableExtra")
val hashMapValue = intent.getExtra<HashMap<String, Any>>("myHashMapExtra")*/


/**
startActivityWithExtras<SecondActivity>()

// Start a new activity with extras

val myHashMap = hashMapOf<String, Any>(
"key1" to "Value 1",
"key2" to 42
)

startNewActivity<HomeActivity>(
"hashMapExtra" to myHashMap,
"keyInt" to 42,
"keyString" to "Hello, World!",
"keyBoolean" to true,
flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP

)

//get value from intent

val intent = intent
val hashMap = intent.getSerializableExtra("hashMapExtra") as? HashMap<String, Serializable>
if (hashMap != null) {
// Use the HashMap
val value1 = hashMap["key1"] as? String
val value2 = hashMap["key2"] as? Int
}

 */
//------------------------------------------------------------------


/**
 * To get Color, Resource, String from xml or res folder
 * Usage :
 *
 * getStr :
 *      getStr(R.string.your_string)
 *
 * getRes :
 *      getRes(R.drawable.your_drawable)
 *
 * getClr :
 *      getClr(R.color.your_color)
 *
 * getDimen :
 *      getDimen(R.dimen.your_dimension)
 *
 * */

fun Context.getStr(@StringRes id: Int) = resources.getString(id)

fun Context.getRes(@DrawableRes id: Int) = ResourcesCompat.getDrawable(resources, id, theme)!!

fun Context.getClr(@ColorRes id: Int) = ResourcesCompat.getColor(resources, id, theme)

fun Context.getClrStateList(@ColorRes id: Int) = ColorStateList.valueOf(getClr(id))

fun Context.getDimen(@DimenRes id: Int) = resources.getDimension(id)


fun ImageView.setImageRes(@DrawableRes drawableResId: Int, @DrawableRes backgroundResId: Int? = null, @ColorRes colorResId: Int? = null) {
    val drawable = ContextCompat.getDrawable(context, drawableResId)
    setImageDrawable(drawable)

    backgroundResId?.let {
        val backgroundDrawable = ContextCompat.getColor(context, it)
        setBackgroundColor(backgroundDrawable)
    }

    colorResId?.let {
        val color = ContextCompat.getColor(context, colorResId)
        this.imageTintList = ColorStateList.valueOf(color)
    }

}
fun View.setViewBg(@DrawableRes drawableRes: Int) {
    val drawable = ContextCompat.getDrawable(context, drawableRes)
    background = drawable
}

fun View.setBgColorRes(@ColorRes colorResId: Int) {
    val color = ContextCompat.getColor(context, colorResId)
    setBackgroundColor(color)
}

fun ImageView.setImageTint(@ColorRes colorResId: Int) {
    val color = ContextCompat.getColor(context, colorResId)
   this.imageTintList = ColorStateList.valueOf(color)
}


fun Activity.hideKeyboard(view: View) {
    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(view.windowToken, 0)
}



/*------------------------------------------------------------------------------------------------*/
/*------------------------------------------------------------------------------------------------*/
/*------------------------------------------------------------------------------------------------*/

/**
 * Inflate view in adapter
 *
 * Usage :
 *      1.) parent.inflate(R.layout.my_layout) -> default root is false
 *      2.) parent.inflate(R.layout.my_layout, true)
 * */
fun ViewGroup.inflateWithView(@LayoutRes layoutRes: Int, attachToRoot: Boolean = false): View =
    LayoutInflater.from(context).inflate(layoutRes, this, attachToRoot)

inline fun <reified T : ViewBinding> LayoutInflater.inflateWithInflater(
    @LayoutRes layoutRes: Int,
    viewGroup: ViewGroup? = null,
    attachToRoot: Boolean = false,
): T {
    val bindingInflater = T::class.java.getMethod("inflate", LayoutInflater::class.java, ViewGroup::class.java, Boolean::class.java)
    return bindingInflater.invoke(null, this, viewGroup, attachToRoot) as T
}

fun Context.inflateView(@LayoutRes layoutRes: Int): View {
    return LayoutInflater.from(this).inflate(layoutRes, null)
}

inline fun <reified T : ViewBinding> ViewGroup.inflateAdapter(@LayoutRes layoutRes: Int): T {
    return LayoutInflater.from(this.context).inflateWithInflater(layoutRes, this, false)
}

inline fun <reified T : ViewBinding> ViewGroup.inflateViewBinding(@LayoutRes layoutRes: Int): T {
    return LayoutInflater.from(this.context).inflateWithInflater(layoutRes, null, false)
}

fun Context.inflateDialogBinding(@LayoutRes layoutResId: Int): ViewBinding {
    return LayoutInflater.from(this).inflateWithInflater(layoutResId, null, false)
}

inline fun <reified T : ViewBinding> Context.inflateBindingView(@LayoutRes layoutRes: Int): T {
    return T::class.java.getMethod("inflate", LayoutInflater::class.java)
        .invoke(null, LayoutInflater.from(this)) as T
}



/**String check*/
fun View.asString(): String {
    return when (this) {
        is EditText -> text.toString().trim().ifNotNullOrElse({ it }, { "" })
        is AppCompatEditText -> text.toString().trim().ifNotNullOrElse({ it }, { "" })
        is AppCompatTextView -> text.toString().trim().ifNotNullOrElse({ it }, { "" })
        is AutoCompleteTextView -> text.toString().trim().ifNotNullOrElse({ it }, { "" })
        is MaterialButton -> text.toString().trim().ifNotNullOrElse({ it }, { "" })
        else -> ""
    }
}

inline fun <T : Any, R> T?.ifNotNullOrElse(ifNotNullPath: (T) -> R, elsePath: () -> R) =
    let { if (it == null) elsePath() else ifNotNullPath(it) }

/** Image Enable/ Disable */
fun ImageView.enable() {
    this.isClickable = true
    this.isFocusable = true
    this.setColorFilter(
        ContextCompat.getColor(context, R.color.black),
        PorterDuff.Mode.SRC_IN
    )
}

fun ImageView.disable() {
    this.isClickable = false
    this.isFocusable = false
    this.setColorFilter(
        ContextCompat.getColor(context, R.color.color_navigation_unselected),
        PorterDuff.Mode.SRC_IN
    )
}

fun View.enable() {
    this.isClickable = true
    this.isFocusable = true
    this.isEnabled = true
}

fun View.disable() {
    this.isClickable = false
    this.isFocusable = false
    this.isEnabled = false
}

fun AppCompatButton.enable() {
    this.isClickable = true
    this.isFocusable = true
    this.isEnabled = true
}

fun AppCompatButton.disable() {
    this.isClickable = false
    this.isFocusable = false
    this.isEnabled = false
}

fun MaterialTextView.disable() {
    this.isClickable = false
    this.isFocusable = false
    this.isEnabled = false
}
fun MaterialTextView.enable() {
    this.isClickable = true
    this.isFocusable = true
    this.isEnabled = true
}

fun Button.styles(
    context: Context,
    tintColor: Int? = null,
    isEnabled: Boolean = true,
    drawableResId: Int?,
    isClickable: Boolean = true,
    textColor: Int? = null,
) {
    tintColor?.let {
        val backgroundColor = ContextCompat.getColor(context, it)
        backgroundTintList = ColorStateList.valueOf(backgroundColor)
    }

    this.isEnabled = isEnabled

    drawableResId?.let {
        background = ContextCompat.getDrawable(context, it)
    }
    textColor?.let {
        setTextColor(ContextCompat.getColor(context, it))
    }
    elevation = 0f
    this.isClickable = isClickable
}

fun View.text(text: CharSequence?) {
    when (this) {
        is TextView -> this.text = text
        is Button -> this.text = text
        is EditText -> this.setText(text)
    }
}


fun TextView.setLeftDrawable(drawableId: Int) {
    val newIcon: Drawable? = ContextCompat.getDrawable(context, drawableId)
    setCompoundDrawablesWithIntrinsicBounds(newIcon, null, null, null)
}

fun <T : Number?> T?.formatAsTwoDigits(): String {
    return this?.toInt()?.let { "%02d".format(it) } ?: "00"
}

/**Filter Dialog dismiss on device back button press
 * Also get dismiss callback to perform other operation*/
fun BottomSheetDialogFragment.onDeviceBackButtonPress(onDismiss: () -> Unit = {}) {
    dialog?.setOnKeyListener { _, keyCode, _ ->
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            dismiss()
            onDismiss.invoke()
            true
        } else {
            false
        }
    }
}

@SuppressLint("ClickableViewAccessibility")
fun MaterialTextView.endDrawableClick(onClick: () -> Unit = {}) {
    this.setOnTouchListener { v, event ->
        if (event.action == MotionEvent.ACTION_DOWN) {
            val drawableEnd: Drawable? = this.compoundDrawablesRelative[2]
            if (drawableEnd != null) {
                val drawableStart = this.width - this.paddingEnd - drawableEnd.bounds.width()
                if (event.x >= drawableStart) {
                    onClick.invoke()
                    return@setOnTouchListener true
                }
            }
        }
        false
    }
}



/**Orientation to Portrait*/
@SuppressLint("SourceLockedOrientationActivity")
fun Activity.setScreenOrientationPortrait() {
    //requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
}

fun <T : Any> T.getClassName(): String {
    return this::class.java.simpleName
}


fun View.setVisibleView(isVisible: Boolean) {
    visibility = if (isVisible) {
        View.VISIBLE
    } else {
        View.GONE
    }
}

fun DialogFragment.showIfNotExists(fragmentManager: FragmentManager, tag: String) {
    val existingFragment = fragmentManager.findFragmentByTag(tag)
    if (existingFragment == null) {
        // Fragment is not added, show it
        show(fragmentManager, tag)
    } else {
        // Fragment is already added, handle as needed
        // You might want to dismiss the existing fragment or update its content
        // based on your application logic.
    }
}

fun TextView.enableMarquee() {
    isSelected = true
}


inline fun <reified T> String.fromJson(): T {
    return Gson().fromJson(this, object : TypeToken<T>() {}.type)
}

inline fun <reified T> T.toJson(): String {
    return Gson().toJson(this)
}


fun MaterialButton.setGradientColor(startColor: Int,endColor: Int){
    val gradientDrawable = GradientDrawable(
        GradientDrawable.Orientation.BL_TR,
        intArrayOf(startColor, endColor)
    )
    gradientDrawable.cornerRadius = 16f
    this.background = gradientDrawable
}



inline fun Activity.checkBtPermission(crossinline checkedPermission: (Boolean) -> Unit) {
    val hasPermission = if (SDK_INT >= Build.VERSION_CODES.S) {
        arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.BLUETOOTH_CONNECT
        ).all {
            ActivityCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
        }
    } else {
        arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
        ).all {
            ActivityCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
        }
    }
    if (!hasPermission) {
        checkedPermission.invoke(false)
    } else {
        checkedPermission.invoke(true)
    }
}

inline fun Activity.checkGalleryAndCameraPermission(crossinline checkedPermission: (Boolean) -> Unit) {
    val hasPermission = if (SDK_INT >= Build.VERSION_CODES.S) {
        arrayOf(
            Manifest.permission.READ_MEDIA_IMAGES,
            Manifest.permission.CAMERA
        ).all {
            ActivityCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
        }
    } else {
        arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
        ).all {
            ActivityCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
        }
    }
    if (!hasPermission) {
        checkedPermission.invoke(false)
    } else {
        checkedPermission.invoke(true)
    }
}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
inline fun Context.checkNotificationPermission(crossinline checkedPermission: (Boolean) -> Unit) {
    val hasPermission = arrayOf(
        Manifest.permission.POST_NOTIFICATIONS,
    ).all {
        ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
    }
    if (!hasPermission) {
        checkedPermission.invoke(false)
    } else {
        checkedPermission.invoke(true)
    }
}

@SuppressLint("HardwareIds")
fun Context.getAndroidId(): String {
    return Settings.Secure.getString(this.contentResolver, Settings.Secure.ANDROID_ID)
}

inline fun Activity.checkCameraPermission(crossinline checkedPermission: (Boolean) -> Unit) {
    val hasPermission = arrayOf(
        Manifest.permission.CAMERA,
        Manifest.permission.ACCESS_FINE_LOCATION
    ).all {
        ActivityCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
    }
    if (!hasPermission) {
        checkedPermission.invoke(false)
    } else {
        checkedPermission.invoke(true)
    }
}


fun String.isValidBluetoothMacAddress(): Boolean {
    if(this.isNotEmpty()){
        // Regular expression pattern for a valid Bluetooth MAC address
        val macAddressPattern = Regex("([0-9A-Fa-f]{2}[:-]){5}([0-9A-Fa-f]{2})")
        // Check if the input string matches the pattern
        return macAddressPattern.matches(this)

    }else{
        return false
    }
}



fun Triple<String,String,Boolean>.messageToast(context:Context){
    if(this.third){
        MotionToast.createColorToast(context,
            this.first,
            this.second,
            MotionToastStyle.ERROR,
            MotionToast.GRAVITY_BOTTOM,
            MotionToast.SHORT_DURATION,
            ResourcesCompat.getFont(context, R.font.lexend_regular))
    }else{
        MotionToast.createColorToast(context,
            this.first,
            this.second,
            MotionToastStyle.SUCCESS,
            MotionToast.GRAVITY_BOTTOM,
            MotionToast.SHORT_DURATION,
            ResourcesCompat.getFont(context, R.font.lexend_regular))
    }
}

fun Pair<String,String>.internetMessageToast(context:Context){
    MotionToast.createColorToast(context,
        this.first,
        this.second,
        MotionToastStyle.NO_INTERNET,
        MotionToast.GRAVITY_BOTTOM,
        MotionToast.SHORT_DURATION,
        ResourcesCompat.getFont(context, R.font.lexend_regular)
    )
}


private fun String.isEmailValid(): Boolean {
    val pattern = Patterns.EMAIL_ADDRESS
    return pattern.matcher(this).matches()
}

fun EditText?.validEmpty(strValidationMsg:String): Pair<String,Boolean> {
    val strText = this?.asString()
    return if (strText.isNullOrEmpty()) {
        Pair(strValidationMsg,false)
    }else{
        Pair(strText,true)
    }
}

fun EditText?.validEmail(context: Context): Pair<String,Boolean> {
    val email = this?.asString()
    return if (email.isNullOrEmpty()) {
        Pair(context.getStr(R.string.enter_email_id),false)
    } else if (!email.isEmailValid()) {
        Pair(context.getStr(R.string.validation_email_id),false)
    } else if (!email.nameSpace()) {
        Pair(email,true)
    } else {
        Pair(context.getStr(R.string.validation_email_id),false)
    }
}




fun String.nameSpace(): Boolean {
    var checkspaces = false
    var f = 0
    for (i in this.indices) {
        if (this.contains(" ")) {
            f = 1
            checkspaces = true
        }
    }
    return if (f == 1) {
        checkspaces
    } else checkspaces
}

@SuppressLint("CommitPrefEdits")
inline fun Context.errorHandling(crossinline logout:() -> Unit  ,isNetworkConnected:Boolean = false, msg: Throwable?=null,strErrorMsg:String ="") {
    if (isNetworkConnected) {
        Pair(getStr(R.string.connection),getStr(R.string.pls_check_your_network_connection)).internetMessageToast(this)
    }else if(strErrorMsg.isNotEmpty()){
        Triple(getStr(R.string.error),strErrorMsg,true).messageToast(this)
    }else if (msg is HttpException) {
        val exception: HttpException = msg
        when(exception.code()){
            500->{
                Triple(getStr(R.string.error),exception.message(),true).messageToast(this)
            }
            502->{
                Triple(getStr(R.string.error),exception.message(),true).messageToast(this)
            }
            503->{
                Triple(getStr(R.string.error),exception.message(),true).messageToast(this)
            }
            401->{
                Triple(getStr(R.string.error),exception.message(),true).messageToast(this)
                logout()
            }
            403->{
                Triple(getStr(R.string.error),exception.message(),true).messageToast(this)
            }
            else->{
                try{
                    val errorObj = exception.response()?.errorBody()?.string()?.let { JSONObject(it) }
                    errorObj?.getString(ApiConstant.ERROR_KEY_MESSAGE)?.let { Triple(getStr(R.string.error),it,true).messageToast(this)}
                }catch (e:Exception){
                    e.printStackTrace()
                    Triple(getStr(R.string.error),getStr(R.string.something_want_to_wrong),true).messageToast(this)
                }
            }
        }
    }else{
        msg?.printStackTrace()
        if("User cancelled the selector" != (msg?.message?:"")){
            Triple(getStr(R.string.error),msg?.message?:"",true).messageToast(this)
        }
    }
}

fun String.showSuccessToast(context:Context){
    Triple(context.getStr(R.string.success),this,false).messageToast(context)
}

fun ImageView.loadImage(drawable: Drawable) {
    Glide.with(this.context)
        .load(drawable)
        .into(this)
}

fun ImageView.loadGif(drawable: Drawable) {
    Glide.with(this.context).asGif()
        .load(drawable)
        .into(this)
}

fun ImageView.loadImage(url: String) {
    Glide.with(this.context)
        .load(url)
        .placeholder(ResourcesCompat.getDrawable(resources,R.drawable.ic_avatar,null))
        .error(ResourcesCompat.getDrawable(resources,R.drawable.ic_avatar,null))
        .into(this)
}

fun String.convertToTimeFormat(): String {
    val inputFormatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
    val date: Date? = inputFormatter.parse(this)
    val outputFormatter = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
    return date?.let { outputFormatter.format(it) } ?: ""
}

fun String.convertToDateFormat(): String {
    val inputFormatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
    val date: Date? = inputFormatter.parse(this)
    val outputFormatter = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
    return date?.let { outputFormatter.format(it) } ?: ""
}

fun ImageView.load(context: Context, strUrl: String) {
    Glide.with(context).load(strUrl).placeholder(ContextCompat.getDrawable(context,R.drawable.ic_avatar))
        .listener(object : RequestListener<Drawable> {
            override fun onLoadFailed(
                e: GlideException?,
                model: Any?,
                target: Target<Drawable>,
                isFirstResource: Boolean
            ): Boolean {
                Log.e("GlideError", "Image Load Failed", e)
                return false
            }

            override fun onResourceReady(
                resource: Drawable,
                model: Any,
                target: Target<Drawable>?,
                dataSource: DataSource,
                isFirstResource: Boolean
            ): Boolean {
                return false
            }

        }).into(this)
}

fun Activity.handlePermissionDenied(
    permissions: Array<String>,
    permissionsLauncher: ActivityResultLauncher<Array<String>>
) {
    if(permissions.isNotEmpty()){
        when(permissions.size){
            1->{
                if(this.shouldShowRequestPermissionRationale(permissions[0])){
                    showPermissionExplanationDialog(permissions = permissions, permissionsLauncher = permissionsLauncher)
                }else{
                    showSettingsDialog{}
                }
            }
            2->{
                if(this.shouldShowRequestPermissionRationale(permissions[0]) ||  this.shouldShowRequestPermissionRationale(permissions[1])){
                    showPermissionExplanationDialog(permissions = permissions, permissionsLauncher = permissionsLauncher)
                }else{
                    showSettingsDialog{}
                }
            }
            3 ->{
                if(this.shouldShowRequestPermissionRationale(permissions[0]) ||  this.shouldShowRequestPermissionRationale(permissions[1]) || this.shouldShowRequestPermissionRationale(permissions[2])){
                    showPermissionExplanationDialog(permissions = permissions, permissionsLauncher = permissionsLauncher)
                }else{
                    showSettingsDialog{}
                }
            }
        }

    }else{
        showSettingsDialog{}
    }

}


fun String.setRequestBody(): RequestBody {
    return this.toRequestBody("text/plain".toMediaType())
}

fun File.setImageUpload(strParameter: String): MultipartBody.Part{
    val fileData =  this.asRequestBody("multipart/form-data".toMediaTypeOrNull())
    return MultipartBody.Part.createFormData(strParameter,this.name,fileData)
}


fun Context.checkPermissionsForCameraAndStorage(
    permissionsLauncher: ActivityResultLauncher<Array<String>>,
    onAllPermissionsGranted: () -> Unit
) {

    val permissionsToRequest = mutableListOf<String>()

    // Check Camera permission
    if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
        permissionsToRequest.add(Manifest.permission.CAMERA)
    }

    if (SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        // Check Read Media Storage permission
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
            permissionsToRequest.add(Manifest.permission.READ_MEDIA_IMAGES)
        }
    }else{
        // Check Read External Storage permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissionsToRequest.add(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
        // Check Write External Storage permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissionsToRequest.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
    }

    // Request permissions if needed
    if (permissionsToRequest.isNotEmpty()) {
        showPermissionExplanationDialog(
            permissions = permissionsToRequest.toTypedArray(),
            permissionsLauncher = permissionsLauncher
        )
    } else {
        // All permissions are granted
        onAllPermissionsGranted()
    }
}


private fun Context.showPermissionExplanationDialog(
    permission: String = "",
    permissions: Array<String> = emptyArray(),
    permissionsLauncher: ActivityResultLauncher<Array<String>>
) {
    showAlertDialog(
        strTitle = getString(R.string.permission_required),
        strMessage = getString(R.string.permission_camera_storage_message),
        strPositiveBtnText = getString(R.string.btn_grant),
        strNegativeBtnText = getString(R.string.btn_cancel),
        positiveButtonCallBack={
            if(permission == ""){
                permissionsLauncher.launch(permissions)
            }else{
                permissionsLauncher.launch(arrayOf(permission))
            }
        },
        negativeButtonCallBack = {

        })
}

fun Activity.openCameraFun(intent:ActivityResultLauncher<Intent>): Uri? {
    var image_uri:Uri?=null
    val values = ContentValues()
    values.put(MediaStore.Images.Media.TITLE, "New Picture")
    values.put(MediaStore.Images.Media.DESCRIPTION, "From the Camera")
    image_uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
    val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
    cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri)
    intent.launch(cameraIntent)
    return image_uri
}

fun Activity.imageSelectDialog(
    cameraLauncher:ActivityResultLauncher<Intent>?=null,
    resultLauncher:ActivityResultLauncher<Intent>?=null,
    pickMedia: ActivityResultLauncher<PickVisualMediaRequest>?=null
):Uri? {
    var dialog: Dialog? = null
    var image_uri:Uri?=null
    try {
        dialog?.dismiss()
        dialog = Dialog(this, R.style.AppCompatAlertDialogStyleBig)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT
        )
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCancelable(true)
        val binding = DlgExternalstorageBinding.inflate(
            LayoutInflater.from(this),
            null,
            false
        )
        val finalDialog: Dialog = dialog
        binding.tvSetImageCamera.setOnClickListener {
            finalDialog.dismiss()
            image_uri = cameraLauncher?.let { it1 -> openCameraFun(it1) }
        }
        binding.tvSetImageGallery.setOnClickListener {
            finalDialog.dismiss()
            if (SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                pickMedia?.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            }else{
                val intent =  Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                resultLauncher?.launch(intent)
            }
        }
        dialog.setContentView(binding.root)
        if (!this.isFinishing) dialog.show()
    } catch (e: java.lang.Exception) {
        e.printStackTrace()
    }

    return image_uri
}







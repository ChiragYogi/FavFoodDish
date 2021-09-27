package com.example.favfooddish.ui.addupdatedish

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.favfooddish.R
import com.example.favfooddish.application.FavDishApplication

import com.example.favfooddish.databinding.ActivityAddUpdateDishBinding
import com.example.favfooddish.databinding.DialogCustomListBinding
import com.example.favfooddish.databinding.DialogCustomeImageSelectionBinding
import com.example.favfooddish.model.entites.FavDish
import com.example.favfooddish.viewmodel.FavDishViewModel
import com.example.favfooddish.viewmodel.FavDishViewModelFactory
import com.example.favfooddish.ui.adapters.CustomListItemAdepter
import com.example.favfooddish.utils.Constant
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.karumi.dexter.listener.single.PermissionListener
import java.io.*
import java.util.*

class AddUpdateDishActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityAddUpdateDishBinding
    private var mImagePath: String = ""

    private  var mFavDishDetail: FavDish? = null

    private lateinit var mCustomListDialog: Dialog
    private val mFavDishViewModel: FavDishViewModel by viewModels {
        FavDishViewModelFactory((application as FavDishApplication).repository)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddUpdateDishBinding.inflate(layoutInflater)
        setContentView(binding.root)


        if (intent.hasExtra(Constant.EXTRA_DISH_DETAILS)){
            mFavDishDetail = intent.getParcelableExtra(Constant.EXTRA_DISH_DETAILS)
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        if (mFavDishDetail != null && mFavDishDetail!!.id != 0){
            supportActionBar?.let {
                it.title = resources.getString(R.string.title_edit_dish)
            }
        }else{
            supportActionBar?.let {
                it.title = resources.getString(R.string.title_add_dish)
            }
        }

        mFavDishDetail?.let { favDish ->
            if (favDish.id != 0){

                binding.apply {
                    mImagePath = favDish.image
                    Glide.with(this@AddUpdateDishActivity).
                    load(mImagePath).centerCrop().into(ivDishImage)
                    etTitle.setText(favDish.title)
                    etType.setText(favDish.type)
                    etCategory.setText(favDish.category)
                    etIngredient.setText(favDish.ingredients)
                    etDishCookingTime.setText(favDish.cookingTime)
                    etDishCookingDirection.setText(favDish.directionToCook)
                    btnAddDish.text = resources.getString(R.string.lbl_update_dish)
                }
            }
        }






        binding.ivAddDishImage.setOnClickListener(this)
        binding.etType.setOnClickListener(this)
        binding.etCategory.setOnClickListener(this)
        binding.etDishCookingTime.setOnClickListener(this)
        binding.btnAddDish.setOnClickListener(this)

    }

    override fun onClick(v: View?) {
        if (v != null) {
            when(v.id){
               R.id.iv_add_dish_image -> {
                customImageSelectionDialog()
               }
               R.id.et_type -> {
                   customItemListDialog(resources.getString(R.string.title_select_dish_type),
                    Constant.dishTypes(),Constant.DISH_TYPE)
                   return
               }
                R.id.et_category -> {
                    customItemListDialog(resources.getString(R.string.title_select_dish_category),
                        Constant.dishCategories(),Constant.DISH_CATEGORY)
                    return
                }
                R.id.et_dish_cooking_time -> {
                    customItemListDialog(resources.getString(R.string.title_select_dish_cooking_time),
                        Constant.dishCookTime(),Constant.DISH_COOKING_TIME)
                    return
                }

                R.id.btn_add_dish -> {
                    addDishToDatabase()

                }
            }
        }
    }

    private fun addDishToDatabase(){
        val title = binding.etTitle.text.toString().trim {it <= ' '}
        val type = binding.etType.text.toString().trim{it <= ' '}
        val category = binding.etCategory.text.toString().trim{it <= ' '}
        val ingredient = binding.etIngredient.text.toString().trim{it <= ' '}
        val cookingTimeInMinutes = binding.etDishCookingTime.text.toString().trim{it <= ' '}
        val cookingDirection = binding.etDishCookingDirection.text.toString().trim{it <= ' '}

        when {
            mImagePath.isEmpty() -> {

                Toast.makeText(this@AddUpdateDishActivity,
                    R.string.err_msg_select_dish_image,Toast.LENGTH_LONG).show()
            }
            title.isEmpty() -> {

                Toast.makeText(this@AddUpdateDishActivity,
                    R.string.err_msg_enter_dish_title,Toast.LENGTH_LONG).show()
            }
            type.isEmpty() -> {

                Toast.makeText(this@AddUpdateDishActivity,
                    R.string.err_msg_select_dish_type,Toast.LENGTH_LONG).show()
            }
            category.isEmpty() -> {

                Toast.makeText(this@AddUpdateDishActivity,
                    R.string.err_msg_select_dish_category,Toast.LENGTH_LONG).show()
            }
            ingredient.isEmpty() -> {

                Toast.makeText(this@AddUpdateDishActivity,
                    R.string.err_msg_enter_dish_ingredients,Toast.LENGTH_LONG).show()
            }
            cookingTimeInMinutes.isEmpty() -> {
                Toast.makeText(this@AddUpdateDishActivity,
                    R.string.err_msg_select_dish_cooking_time,Toast.LENGTH_LONG).show()
            }
            cookingDirection.isEmpty() -> {

                Toast.makeText(this@AddUpdateDishActivity,
                    R.string.err_msg_enter_dish_cooking_instructions,Toast.LENGTH_LONG).show()
            }
            else -> {
                var favDishId = 0
                var imageSource = Constant.DISH_IMAGE_SOURCE_LOCAL
                var favoriteDish = false

                mFavDishDetail?.let {
                    if (it.id !=0){
                        favDishId = it.id
                        imageSource = it.imageSource
                        favoriteDish = it.favoriteDish

                    }
                }
                val favDishDetail = FavDish(mImagePath,
                    imageSource,title,type,category,
                    ingredient,cookingTimeInMinutes,cookingDirection,favoriteDish,favDishId)

                if (favDishId == 0){
                    mFavDishViewModel.insert(favDishDetail)
                    Toast.makeText(this, " ALl The Data is valid",
                        Toast.LENGTH_LONG).show()
                    Log.d("FavFoodDish","success fully inserted dish")

                }else{
                    mFavDishViewModel.update(favDishDetail)
                    Toast.makeText(this, " ALl The Data is Updated",
                        Toast.LENGTH_LONG).show()
                    Log.d("FavFoodDish","success fully updated dish")
                }
                finish()
            }

        }
    }
    private fun customImageSelectionDialog(){

      mCustomListDialog = Dialog(this)
        val dialogBinding: DialogCustomeImageSelectionBinding =
            DialogCustomeImageSelectionBinding.inflate(layoutInflater)
        mCustomListDialog.setContentView(dialogBinding.root)

        dialogBinding.tvCamera.setOnClickListener {

            Dexter.withContext(this).withPermissions(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                //Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA,
            ).withListener( object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                    report?.let {
                        if (report.areAllPermissionsGranted()){
                            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                          startActivityForResult(intent, CAMERA_VALUE)
                        }
                    }


                }

                override fun onPermissionRationaleShouldBeShown(
                    permission: MutableList<PermissionRequest>?,
                    p1: PermissionToken?
                ) {
                    showRationalDialogPermission()
                }

            }).onSameThread().check()

            mCustomListDialog.dismiss()
        }

        dialogBinding.tvGallery.setOnClickListener {

            Dexter.withContext(this).withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener( object : PermissionListener {
                    override fun onPermissionGranted(p0: PermissionGrantedResponse?) {
                        val intent = Intent(Intent.ACTION_PICK ,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                        startActivityForResult(intent, GALLERY_VALUE)
                    }

                    override fun onPermissionDenied(p0: PermissionDeniedResponse?) {
                        Toast.makeText(this@AddUpdateDishActivity,"You Have Not Granted Permission",Toast.LENGTH_LONG).show()

                    }

                    override fun onPermissionRationaleShouldBeShown(
                        p0: PermissionRequest?,
                        p1: PermissionToken?
                    ) {
                        showRationalDialogPermission()
                    }
                }).onSameThread().check()

            mCustomListDialog.dismiss()



        }
        mCustomListDialog.show()

    }

    fun selectedListItem(item: String, selection: String){
        when(selection){
            Constant.DISH_TYPE -> {
                mCustomListDialog.dismiss()
                binding.etType.setText(item)
            }
            Constant.DISH_CATEGORY -> {
                mCustomListDialog.dismiss()
                binding.etCategory.setText(item)
            }
            Constant.DISH_COOKING_TIME -> {
                mCustomListDialog.dismiss()
                binding.etDishCookingTime.setText(item)
            }


        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == CAMERA_VALUE) {
                data?.extras?.let {
                    val thumbnail: Bitmap = data.extras?.get("data") as Bitmap
                    //binding.ivDishImage.setImageBitmap(thumbnail)

                    Glide.with(this)
                        .load(thumbnail)
                        .fitCenter()
                        .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                        .into(binding.ivDishImage)

                    mImagePath = saveImageToInternalStorage(thumbnail)
                    Log.d("FavFoodDish", mImagePath)

                    binding.ivAddDishImage.setImageDrawable(
                        ContextCompat.getDrawable(this, R.drawable.ic_vector_edit)
                    )
                }

            }


            if (resultCode == Activity.RESULT_OK) {
                if (requestCode == GALLERY_VALUE) {
                    data?.let {

                        val selectedImage = data.data

                        Glide.with(this)
                            .load(selectedImage)
                            .fitCenter()
                            .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                            .listener(object : RequestListener<Drawable>{
                                override fun onLoadFailed(
                                    e: GlideException?,
                                    model: Any?,
                                    target: Target<Drawable>?,
                                    isFirstResource: Boolean
                                ): Boolean {
                                    return false
                                }

                                override fun onResourceReady(
                                    resource: Drawable?,
                                    model: Any?,
                                    target: Target<Drawable>?,
                                    dataSource: DataSource?,
                                    isFirstResource: Boolean
                                ): Boolean {
                                    resource?.let {
                                        val bitmap = resource.toBitmap()
                                        mImagePath = saveImageToInternalStorage(bitmap)
                                        Log.d("FavFoodDish", mImagePath)
                                    }
                                    return false
                                }

                            })
                            .into(binding.ivDishImage)
                        //binding.ivDishImage.setImageURI(selectedImage)



                        binding.ivAddDishImage.setImageDrawable(
                            ContextCompat.getDrawable(this, R.drawable.ic_vector_edit)
                        )
                    }

                }
            }
        } else if(resultCode == Activity.RESULT_CANCELED){
            Log.d("FavFoodDish","User Canceled the operation")
        }


    }

    private fun showRationalDialogPermission() {
        val dialog = androidx.appcompat.app.AlertDialog.Builder(this).apply {
            setMessage("It Look Like You turned off permission require " +
                    "for this feature. It can be enable under application setting" )
            setPositiveButton("Go To Setting") { _, _ ->
                try {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts("package", packageName, null)
                    intent.data = uri
                    startActivity(intent)
                } catch (e: ActivityNotFoundException) {
                    Toast.makeText(this@AddUpdateDishActivity, "$e", Toast.LENGTH_LONG).show()
                }
            }
            setNegativeButton("Cancel"){dialog,_ ->
                dialog.dismiss()
            }

        }
        dialog.show()
    }


    private fun saveImageToInternalStorage(bitmap: Bitmap): String{

        val wrapper = ContextWrapper(applicationContext)

        //setting file
        var file = wrapper.getDir(IMAGE_DIRECTORY, Context.MODE_PRIVATE)
        file = File(file, "${UUID.randomUUID()}.jpg")

        try {
            val stream: OutputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG,100, stream)
            stream.flush()
            stream.close()
        }catch (e: IOException){
            e.printStackTrace()
        }

        return file.absolutePath
    }

    private fun customItemListDialog(title: String, itemList: List<String>, selection: String){

        mCustomListDialog = Dialog(this)
        val binding: DialogCustomListBinding = DialogCustomListBinding.inflate(layoutInflater)

        mCustomListDialog.setContentView(binding.root)
        binding.tvTitle.text = title

        binding.rvList.layoutManager = LinearLayoutManager(this)

        val adepter = CustomListItemAdepter(this,null,itemList,selection)
        binding.rvList.adapter = adepter
        mCustomListDialog.show()

    }




    companion object{
        private const val CAMERA_VALUE = 1
        private const val GALLERY_VALUE = 2
        private const val IMAGE_DIRECTORY = "FavDishImages"
    }

}
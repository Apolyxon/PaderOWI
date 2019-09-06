package com.example.paderowi

import android.app.Activity
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import com.tom_roush.pdfbox.util.PDFBoxResourceLoader

import kotlinx.android.synthetic.main.activity_main.*
import android.widget.TextView
import android.graphics.Bitmap
import android.content.res.AssetManager
import android.util.Log
import com.tom_roush.pdfbox.cos.COSName
import java.io.File

import com.tom_roush.pdfbox.pdmodel.interactive.form.PDCheckbox
import com.tom_roush.pdfbox.pdmodel.interactive.form.PDTextField
import com.tom_roush.pdfbox.pdmodel.PDDocument
import com.tom_roush.pdfbox.pdmodel.font.PDType1Font
import java.io.IOException
import android.content.Intent

import androidx.core.content.FileProvider
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import java.security.AccessController.getContext
import android.widget.Toast
import android.content.DialogInterface
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity() {
    var assetManager: AssetManager? = null
    var pageImage: Bitmap? = null
    private var imageHolder: ImageView? = null
    var picture: File? = null
    private val requestCode = 20
    var mCurrentPhotoPath : String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        fab.setOnClickListener { fillForm() }

        imageHolder = findViewById(R.id.captured_photo);
        val capturedImageButton: Button? = findViewById(R.id.photo_button)
        capturedImageButton?.setOnClickListener{onClickPhoto()}
    }

    val REQUEST_TAKE_PHOTO = 1

    fun onClickPhoto() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->

            // Ensure that there's a camera activity to handle the intent
            takePictureIntent.resolveActivity(packageManager)?.also {
                // Create the File where the photo should go
                picture = try {
                    createImageFile()
                } catch (ex: IOException) {
                    // Error occurred while creating the File
                    null
                }

                // Continue only if the File was successfully created
                picture?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID, it)
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO)
                }
            }
        }
    }

    var currentPhotoPath: String = ""

    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        var storageDir: File? = this.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            // Save a file: path for use with ACTION_VIEW intents
            currentPhotoPath = absolutePath
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == Activity.RESULT_OK) {
            val bitmap = BitmapFactory.decodeFile(picture!!.absolutePath)

            imageHolder?.setImageBitmap(bitmap)
        }
    }

    override fun onStart() {
        super.onStart()
        setup()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    /**
     * Initializes variables used for convenience
     */
    private fun setup() {
        PDFBoxResourceLoader.init(getApplicationContext())
        assetManager = getAssets()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        when (item.itemId) {
            // Respond to the action bar's Up/Home button
            android.R.id.home -> {
                super.onBackPressed()
                return true
            }
            R.id.action_settings -> {
                val intent = Intent(this, SettingsActivity::class.java)
                startActivity(intent)
                return true
            }

        }

        return super.onOptionsItemSelected(item)
    }

    fun getEnteredTatTag(): String {
        val tv = findViewById (R.id.editTattag) as TextView
        return tv.text.toString()
    }


    fun getEnteredUhrzeit(): String {
        val tv = findViewById (R.id.editUhrzeit) as TextView
        return tv.text.toString()
    }

    fun getEnteredStrasseAdresse(): String {
        val tv = findViewById (R.id.editStrasseNr) as TextView
        return tv.text.toString()
    }

    fun getEnteredPLZOrt(): String {
        val tv = findViewById (R.id.editPLZOrt) as TextView
        return tv.text.toString()
    }

    fun getEnteredHerstellerFarbe(): String {
        val tv = findViewById (R.id.editHerstellerFarbe) as TextView
        return tv.text.toString()
    }

    fun getEnteredOWI(): String {
        val tv = findViewById (R.id.editOWI) as TextView
        return tv.text.toString()
    }
    
    fun getEnteredBehinderung(): String {
        val tv = findViewById (R.id.editBehinderung) as TextView
        return tv.text.toString()
    }

    fun getEnteredKennzeichen(): String {
        val tv = findViewById (R.id.editKennzeichen) as TextView
        return tv.text.toString()
    }

    fun showPersoenlicheDatenFehlen() {
        AlertDialog.Builder(this)
            //set icon
            .setIcon(android.R.drawable.ic_dialog_alert)
            //set title
            .setTitle("Persönliche Daten fehlen")
            //set message
            .setMessage("Bitte trage deine persönlichen Daten in den Einstellungen ein.")
            //set positive button
            .setNeutralButton("OK", DialogInterface.OnClickListener { dialogInterface, i ->
                //set what would happen when positive button is clicked

            })
            .show()
    }

    fun fillForm() {
        val preferences = PreferenceManager.getDefaultSharedPreferences(this)

        val nachname = preferences.getString("Nachname", "")
        val vorname = preferences.getString("Vorname", "")
        val strasseNr = preferences.getString("StrasseNr", "")
        val ort = preferences.getString("Ort", "")
        val email = preferences.getString("Email", "")
        val telefon = preferences.getString("Telefon", "")

        if (nachname!!.isEmpty() || vorname!!.isEmpty() || strasseNr!!.isEmpty() ||
            ort!!.isEmpty() || email!!.isEmpty() || telefon!!.isEmpty())
        {
            showPersoenlicheDatenFehlen()
            return
        }
        // PDF Creation Abstrahieren? SetAdresse etc.

        // Load the document and get the AcroForm
        val document = PDDocument.load(assetManager!!.open("Privatanzeigen Formular.pdf"))
        val docCatalog = document.documentCatalog
        val acroForm = docCatalog.acroForm

        //   var adobeDefaultAppearanceString = "/Helv 0 Tf 0 g "
        //    acroForm.setDefaultAppearance(adobeDefaultAppearanceString);

        acroForm.defaultResources.put(COSName.getPDFName("Helv"), PDType1Font.HELVETICA)

        try {
            val NachnameField = acroForm.getField("Text1") as PDTextField
            NachnameField.value = nachname

            val VornameField = acroForm.getField("Text2") as PDTextField
            VornameField.value = vorname

            val StrasseNrAbsField = acroForm.getField("Text3") as PDTextField
            StrasseNrAbsField.value = strasseNr

            val OrtAbsField = acroForm.getField("Text4") as PDTextField
            OrtAbsField.value = ort

            val EmailField = acroForm.getField("Text5") as PDTextField
            EmailField.value = email

            val TelefonField = acroForm.getField("Text6") as PDTextField
            TelefonField.value = telefon

            val DateField = acroForm.getField("Text7") as PDTextField
            DateField.value = getEnteredTatTag()

            val VonField = acroForm.getField("Text8") as PDTextField
            VonField.value = getEnteredUhrzeit()

            val BisField = acroForm.getField("Text9") as PDTextField
            BisField.value = getEnteredUhrzeit()

            val StrasseNummerOrt = acroForm.getField("Text10") as PDTextField
            StrasseNummerOrt.value = getEnteredStrasseAdresse() + ", " + getEnteredPLZOrt()

            val KennzeichenField = acroForm.getField("Text11") as PDTextField
            KennzeichenField.value = getEnteredKennzeichen()

            val HerstellerFarbeField = acroForm.getField("Text12") as PDTextField
            HerstellerFarbeField.value = getEnteredHerstellerFarbe()

            val OWIfield = acroForm.getField("Text13") as PDTextField
            OWIfield.value = getEnteredOWI()
            
            val Behinderungfield = acroForm.getField("Text14") as PDTextField
            Behinderungfield.value = getEnteredBehinderung()

            val checkbox1 = acroForm.getField("Kontrollkästchen1")
            (checkbox1 as PDCheckbox).check()

            val checkbox2 = acroForm.getField("Kontrollkästchen2")
            (checkbox2 as PDCheckbox).check()
        } catch (e: IOException) {
            Log.e("PdfBox-Android-Sample", "Exception thrown while filling form fields", e)
        }

        val f = File(filesDir, "FilledForm.pdf")

        document.save(f.absolutePath)

        createEmail(f)

        document.close()

    }

    fun createEmail(owiFile: File) {
        var uris = ArrayList<Uri>();

        val fileURI = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID, owiFile)
        uris.add(fileURI)

        if (imageHolder?.drawable != null && picture != null)
        {
            val uriBeweisFoto = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID, picture!!)
            uris.add(uriBeweisFoto)
        }

        val emailIntent = Intent(Intent.ACTION_SEND_MULTIPLE)
        emailIntent.type = "vnd.android.cursor.dir/email"
        emailIntent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION

        val to = arrayOf("boss@paderborn.de")
        emailIntent.putExtra(Intent.EXTRA_EMAIL, to)
        emailIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Parkverstoß vom " + getEnteredTatTag() + ", " + getEnteredStrasseAdresse())

        val preferences = PreferenceManager.getDefaultSharedPreferences(this)
        val nachname = preferences.getString("Nachname", "")
        val vorname = preferences.getString("Vorname", "")
        val emailBody = "Sehr geehrte Damen und Herren,\n\n" +
                                "Hiermit zeige ich – mit der Bitte um Weiterverfolgung durch Ihr Amt – eine Verkehrsordnungswidrigkeit an.\n\n" +
                                "Das Formular zur Privatanzeige mitsamt Beweisfoto ist angehängt.\n\n" +
                                "Danke, dass Sie sich durch Weiterverfolgung oben angezeigter Verkehrsordnungswidrigkeit für mehr Rücksicht, freie Wege und eine bessere Stadt einsetzen!\n\n" +
                                "Mit freundlichen Grüßen,\n" + vorname + " " + nachname

        emailIntent.putExtra(Intent.EXTRA_TEXT, emailBody)

        startActivity(Intent.createChooser(emailIntent, "Send email..."))
    }
}

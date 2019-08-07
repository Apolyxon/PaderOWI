package com.example.paderowi

import android.app.PendingIntent.getActivity
import android.os.Bundle
import android.os.Environment
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
import android.content.pm.PackageManager






class MainActivity : AppCompatActivity() {
    var root: File? = null
    var assetManager: AssetManager? = null
    var pageImage: Bitmap? = null
    var tv: TextView? = null


    private fun onNewPDF() {
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        fab.setOnClickListener { fillForm() }
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
     //   tv = (TextView) findViewById (R.id.statusTextView);

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    /**
     * Fills in a PDF form and saves the result
     */
    fun fillForm() {
        try {

            // PDF Creation Abstrahieren? SetAdresse etc.

            // Load the document and get the AcroForm
            val document = PDDocument.load(assetManager!!.open("Privatanzeigen Formular.pdf"))
            val docCatalog = document.documentCatalog
            val acroForm = docCatalog.acroForm

          //   var adobeDefaultAppearanceString = "/Helv 0 Tf 0 g "

            //    acroForm.setDefaultAppearance(adobeDefaultAppearanceString);

            acroForm.defaultResources.put(COSName.getPDFName("Helv"), PDType1Font.HELVETICA)


            // Fill the text field
            val field = acroForm.getField("Text1") as PDTextField

            field.value = "Filled Text Field"
            // Optional: don't allow this field to be edited
            field.isReadOnly = true

            val checkbox = acroForm.getField("Kontrollkästchen1")
            (checkbox as PDCheckbox).check()


            val f = File(filesDir, "FilledForm.pdf")

         document.save(f.absolutePath)


            // create new Intent
            val intent = Intent(Intent.ACTION_VIEW)

            intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
            val uri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID, f)
            intent.setDataAndType(uri, "application/pdf")

            val pm = getPackageManager()
            if (intent.resolveActivity(pm) != null) {
                startActivity(intent)
            }
            document.close()
        } catch (e: IOException) {
            Log.e("PdfBox-Android-Sample", "Exception thrown while filling form fields", e)
        }

    }

    fun createEmail(document: PDDocument) {

    }
}

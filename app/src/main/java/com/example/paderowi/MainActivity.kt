package com.example.paderowi

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
import androidx.appcompat.app.AlertDialog




class MainActivity : AppCompatActivity() {
    var assetManager: AssetManager? = null
    var pageImage: Bitmap? = null


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
        val uri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID, owiFile)

        val emailIntent = Intent(Intent.ACTION_SEND)

        emailIntent.type = "vnd.android.cursor.dir/email"
        emailIntent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION

        val to = arrayOf("boss@paderborn.de")
        emailIntent.putExtra(Intent.EXTRA_EMAIL, to)
        emailIntent.putExtra(Intent.EXTRA_STREAM, uri)
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Parkverstoß vom " + getEnteredTatTag() + ", " + getEnteredStrasseAdresse())

        startActivity(Intent.createChooser(emailIntent, "Send email..."))
    }
}

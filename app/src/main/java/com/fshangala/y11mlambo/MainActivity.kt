package com.fshangala.y11mlambo

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.SystemClock
import android.view.Menu
import android.view.MenuItem
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider

class MainActivity : AppCompatActivity(), OddsDialogFragment.OddsDialogListener {
    private var webView: WebView? = null
    private var model: MasterViewModel? = null
    private var masterStatus: TextView? = null
    var sharedPref: SharedPreferences? = null
    var toast: Toast? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        webView = findViewById(R.id.webView)
        true.also { webView!!.settings.javaScriptEnabled = it }
        model = ViewModelProvider(this)[MasterViewModel::class.java]
        sharedPref = getSharedPreferences("MySettings", Context.MODE_PRIVATE)
        masterStatus = findViewById(R.id.masterStatus)

        startBrowser()

        model!!.connectionStatus.observe(this) {
            toast = Toast.makeText(this,it,Toast.LENGTH_SHORT)
            toast!!.show()
        }
        model!!.createConnection(sharedPref!!)
    }

    private fun startBrowser(){
        val url = "https://jack9.io/d/index.html#/"
        webView!!.loadUrl(url)
        webView!!.webViewClient = object : WebViewClient(){
            override fun onPageFinished(view: WebView?, url: String?) {
                SystemClock.sleep(5000)
                view!!.evaluateJavascript("document.querySelector(\"input[name='loginName']\").value='fishing'"){
                    runOnUiThread{
                        masterStatus!!.text = it
                    }
                }
                view.evaluateJavascript("document.querySelector(\"input[name='password']\").value='somebody'"){
                    runOnUiThread{
                        masterStatus!!.text = it
                    }
                }
                runOnUiThread{
                    masterStatus!!.text = "Loaded!"
                }
            }

            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                runOnUiThread{
                    masterStatus!!.text = "Loading..."
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.y11menu,menu)

        model!!.connected.observe(this){
            if (it){
                menu.getItem(0).setIcon(R.mipmap.reset_green_round)
            } else {
                menu.getItem(0).setIcon(R.mipmap.reset_red_round)
            }
        }

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.preferencesBtn -> {
                openConfig()
            }

            R.id.oddsBtn -> {
                showOddsDialog()
            }

            R.id.reconnectBtn -> {
                model!!.createConnection(sharedPref!!)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun openConfig(){
        val intent = Intent(this,ConfigActivity::class.java)
        startActivity(intent)
    }

    override fun onDialogPositiveClick(dialog: DialogFragment) {
        dialog.dismiss()
    }

    override fun onDialogNegativeClick(dialog: DialogFragment) {
        dialog.dismiss()
    }

    private fun showOddsDialog() {
        val dialog = OddsDialogFragment()
        dialog.show(supportFragmentManager, "OddsDialog")
    }
}
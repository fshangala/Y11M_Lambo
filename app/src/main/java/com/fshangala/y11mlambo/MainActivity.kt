package com.fshangala.y11mlambo

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.SystemClock
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.webkit.WebResourceRequest
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
        true.also {
            webView!!.settings.javaScriptEnabled = it
            webView!!.settings.domStorageEnabled = it
        }
        model = ViewModelProvider(this)[MasterViewModel::class.java]
        sharedPref = getSharedPreferences("MySettings", Context.MODE_PRIVATE)
        masterStatus = findViewById(R.id.masterStatus)

        startBrowser()

        model!!.connectionStatus.observe(this) {
            toast = Toast.makeText(this,it,Toast.LENGTH_SHORT)
            toast!!.show()
        }
        model!!.automationEvents.observe(this) {
            when (it.eventName) {
                "place_bet" -> {
                    placeBet(it)
                    toast = Toast.makeText(this,it.eventArgs.toString(),Toast.LENGTH_LONG)
                    toast!!.show()
                }
                "open_bet" -> {
                    onOpenBet(it)
                    toast = Toast.makeText(this,it.eventArgs.toString(),Toast.LENGTH_LONG)
                    toast!!.show()
                }
                "confirm_bet" -> {
                    confirmBet()
                }
                else -> {
                    toast = Toast.makeText(this,it.eventName,Toast.LENGTH_LONG)
                    toast!!.show()
                }
            }
        }
        model!!.browserLoading.observe(this){
            if (it == true) {
                runOnUiThread {
                    masterStatus!!.text = "Loading..."
                }
            } else {
                runOnUiThread {
                    masterStatus!!.text = "Loaded!"
                }
            }
        }
        model!!.createConnection(sharedPref!!)
    }

    private fun onOpenBet(automationEvents: AutomationEvents) {
        var Oteam = ""
        var Obacklay = automationEvents.eventArgs[1]
        var Oodds = automationEvents.eventArgs[2]
        var Ostake = automationEvents.eventArgs[3]

        if (automationEvents.eventArgs[0] == "team1"){
            Oteam = "0"
        } else if (automationEvents.eventArgs[0] == "team2"){
            Oteam = "4"
        }

        webView!!.evaluateJavascript("document.querySelectorAll(\".$Obacklay-odd.exch-odd-button\")[$Oteam].click();"){
            runOnUiThread{
                masterStatus!!.text = it
            }
        }
    }

    private fun placeBet(automationEvents: AutomationEvents) {
        var Oteam = ""
        var Obacklay = automationEvents.eventArgs[1]
        var Oodds = automationEvents.eventArgs[2]
        var Ostake = automationEvents.eventArgs[3]

        if (automationEvents.eventArgs[0] == "team1"){
            Oteam = "0"
        } else if (automationEvents.eventArgs[0] == "team2"){
            Oteam = "4"
        }

        webView!!.evaluateJavascript(
            "document.querySelector(\".odds-ctn input\").value = $Oodds;"
        ) {
            runOnUiThread{
                masterStatus!!.text = it
            }
        }

        webView!!.evaluateJavascript(
            "document.querySelector(\".stake-ctn input\").value = $Ostake;"
        ) {
            runOnUiThread{
                masterStatus!!.text = it
            }
        }
    }

    private fun confirmBet() {
        webView!!.evaluateJavascript("document.querySelector(\".place-btn\").click();") {
            runOnUiThread {
                masterStatus!!.text = it
            }
        }
    }

    private fun startBrowser(){
        val url = "https://betbhai.com/home"
        webView!!.loadUrl(url)
        webView!!.webViewClient = object : WebViewClient(){

            override fun onPageFinished(view: WebView?, url: String?) {
                model!!.browserLoading.value = false
                //SystemClock.sleep(5000)
            }

            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                model!!.browserLoading.value = true
            }
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        when (keyCode) {
            KeyEvent.KEYCODE_VOLUME_UP -> {
                model!!.sendCommand(AutomationObject("bet","confirm_bet", arrayOf()))
            }
        }
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.y11menu,menu)

        model!!.connected.observe(this){
            if (it){
                menu.getItem(1).setIcon(R.mipmap.reset_green_round)
            } else {
                menu.getItem(1).setIcon(R.mipmap.reset_red_round)
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

            R.id.reloadBrowserBtn -> {
                webView!!.reload()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun openConfig(){
        val intent = Intent(this,ConfigActivity::class.java)
        startActivity(intent)
    }

    override fun openBet(dialog: DialogFragment, oddsData: OddsData) {
        val automationObject = AutomationObject("bet","open_bet", arrayOf<String>(
            oddsData.team,
            oddsData.backlay,
            oddsData.odds.toString(),
            oddsData.stake.toString()
        ))
        model!!.sendCommand(automationObject)
    }

    override fun onDialogPositiveClick(dialog: DialogFragment,oddsData: OddsData) {
        val automationObject = AutomationObject("bet","place_bet", arrayOf<String>(
            oddsData.team,
            oddsData.backlay,
            oddsData.odds.toString(),
            oddsData.stake.toString()
        ))
        model!!.sendCommand(automationObject)
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
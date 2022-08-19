package com.fshangala.y11mlambo

import org.json.JSONObject

class AutomationEvents(json:String): JSONObject(json) {
    val eventType:String = this.optString("event_type")
    val eventName:String = this.optString("event")
}
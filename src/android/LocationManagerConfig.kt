package loc

import org.json.JSONObject

object LocationManagerConfig {

    var url: String? = null
    var uuid: String? = null
    var extras: JSONObject? = null

    fun update(data: JSONObject) {
        url = data.getString("url")!!
        uuid = data.getString("uuid")!!
        extras = data.getJSONObject("extras") ?: JSONObject()
    }
}
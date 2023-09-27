package cc.unitmesh.connection

import io.vertx.core.json.JsonObject
import io.vertx.json.schema.impl.JsonRef
import java.io.File


class ConnectionParser {
    fun loadSchemaByType(): JsonObject? {
        val context = File(loadResource()).readText()
        val obj = JsonObject(context)

        val resolved = JsonRef.resolve(obj)

        val properties = resolved.getJsonObject("properties")
        val required = resolved.getJsonArray("required")

        return resolved
    }


    private fun loadResource(): String {
        return this.javaClass.getResource("/schemas/AzureOpenAIConnection.schema.json")!!.file
    }
}